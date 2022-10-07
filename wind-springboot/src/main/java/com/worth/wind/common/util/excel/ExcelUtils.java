package com.worth.wind.common.util.excel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.worth.wind.blog.enums.FilePathEnum;
import com.worth.wind.blog.service.RedisService;
import com.worth.wind.common.constant.BusinessWorkConst;
import com.worth.wind.common.exception.BizException;
import com.worth.wind.common.strategy.context.UploadStrategyContext;
import com.worth.wind.common.util.BusinessWorkUtils;
import com.worth.wind.common.util.CompletableFutureUtils;
import com.worth.wind.common.util.FileUtils;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
public class ExcelUtils {

    private static final UploadStrategyContext uploadExportFile = SpringUtil.getBean(UploadStrategyContext.class);
    public static long getCount(String filePath, int sheetNo) {
        LongAdder count = new LongAdder();
        EasyExcel.read(filePath, new PageReadListener<>(list -> count.add(list.size()))).sheet(sheetNo).doRead();
        return count.sum();
    }

    public static long getCount(String filePath) {
        return getCount(filePath, 0);
    }

    public static <T, E> String export(String workName, List<T> source, Class<E> clazz) {
        return export(workName, source, null, clazz);
    }

    public static <T, E> String export(String workName, List<T> source, Function<T, E> function, Class<E> clazz) {
        String businessId = BusinessWorkUtils.startBusiness(BusinessWorkConst.WORK_TYPE_EXPORT, workName + IdUtil.fastSimpleUUID(), null, null);
        CompletableFutureUtils.runAsync(() -> {
            File file = null;
            String fileId = null;
            try {
                List<E> result = new ArrayList<>();
                for (T target : source) {
                    E apply;
                    if (function != null) {
                        apply = function.apply(target);
                    } else {
                        apply = ConverterRegistry.getInstance().convert(clazz, target);
                    }
                    if (apply != null) {
                        result.add(apply);
                    }
                }
                file = Paths.get(FilePathEnum.IMG.getPath(), StrUtil.format("{}_{}", workName, IdUtil.fastSimpleUUID()) + ".xlsx").toFile();
                EasyExcel.write(file, clazz).registerWriteHandler(ExcelStyleHandler.defaultStyleHandler())
                        .sheet(workName).doWrite(result);
                fileId = uploadExportFile.executeUploadStrategy(FileUtils.getMultipartFile(file), FilePathEnum.EXCEL.getPath());
            } finally {
                BusinessWorkUtils.finishBusiness(businessId, fileId);
                FileUtil.del(file);
            }
        }, "导出文件");
        return businessId;
    }

    /**
     * 导出大文件
     *
     * @param workName 任务名称，还会作为Excel的sheet名
     * @param getData  分页获取数据，传入参数为当前页数，返回类型即为Excel类型
     * @param clazz    clazz 导出的Excel对应类型
     * @return {@link String}
     */
    public static <T> String exportBigWithBusiness(String workName, Function<Integer, List<T>> getData, Class<T> clazz, WriteHandler... writeHandlers) {
        // 开启business
        String businessId = BusinessWorkUtils.startBusiness(BusinessWorkConst.WORK_TYPE_EXPORT, workName + IdUtil.fastSimpleUUID());
        CompletableFuture.supplyAsync(() -> exportBig(workName, getData, e -> e, clazz, writeHandlers)).whenComplete((fileId, e) -> {
            log.error("", e);
            BusinessWorkUtils.finishBusiness(businessId, fileId);
        });
        return businessId;
    }

    /**
     * 导出大文件 需要传入表头及该列对应的字段名 通过反射获取
     *
     * @param workName      任务名称，还会作为Excel的sheet名
     * @param getData       分页获取数据，传入参数为当前页数，返回类型即为Excel类型
     * @param heads         表头
     * @param fieldNames    字段名称
     * @param writeHandlers 装饰器
     * @return {@link String}
     */
    public static <T> String exportBigWithBusiness(String workName, Function<Integer, List<T>> getData, List<List<String>> heads, List<String> fieldNames, WriteHandler... writeHandlers) {
        // 开启business
        String businessId = BusinessWorkUtils.startBusiness(BusinessWorkConst.WORK_TYPE_EXPORT, workName + IdUtil.fastSimpleUUID());
        CompletableFuture.supplyAsync(() -> exportBig(workName, getData, heads, fieldNames, writeHandlers)).whenComplete((fileId, e) -> {
            log.error("", e);
            BusinessWorkUtils.finishBusiness(businessId, fileId);
        });
        return businessId;
    }

    /**
     * 导出大文件
     *
     * @param workName 任务名称，还会作为Excel的sheet名
     * @param getData  分页获取数据，传入参数为当前页数，返回类型即为Excel类型
     * @param clazz    clazz 导出的Excel对应类型
     * @return {@link String}
     */
    public static <T> String exportBig(String workName, Function<Integer, List<T>> getData, Class<T> clazz, WriteHandler... writeHandlers) {
        return exportBig(workName, getData, e -> e, clazz, writeHandlers);
    }

    /**
     * 导出大文件
     *
     * @param workName 任务名称，还会作为Excel的sheet名
     * @param getData  分页获取数据，传入参数为
     * @param convert  将getData返回的类型转换为Excel类
     * @param clazz    clazz 导出的Excel对应类型
     * @return {@link String}
     */
    public static <T, E> String exportBig(String workName, Function<Integer, List<T>> getData, Function<T, E> convert, Class<E> clazz, WriteHandler... writeHandlers) {
        File file = Paths.get(FilePathEnum.TMP.getPath(), StrUtil.format("{}_{}", workName, IdUtil.fastSimpleUUID()) + ".xlsx").toFile();
        String fileId = null;
        // 第一个writeHandler为样式配置
        ExcelWriter excelWriter = null;
        try {
            ExcelWriterBuilder builder = EasyExcel.write(file, clazz).registerWriteHandler(ExcelStyleHandler.defaultStyleHandler());
            if (ArrayUtil.isNotEmpty(writeHandlers)) {
                for (WriteHandler writeHandler : writeHandlers) {
                    builder.registerWriteHandler(writeHandler);
                }
            }
            excelWriter = builder.build();
            WriteSheet writeSheet = EasyExcel.writerSheet(workName).build();
            int page = 1;
            List<T> datas = null;
            boolean isEnd = false;
            do {
                datas = getData.apply(page);
                if (CollUtil.isEmpty(datas)) {
                    datas = new ArrayList<>();
                    isEnd = true;
                }
                List<E> convertData = null;
                if (convert != null) {
                    convertData = datas.stream().map(convert).collect(Collectors.toList());
                } else {
                    convertData = datas.stream().map(data -> (E) ConverterRegistry.getInstance().convert(clazz, data)).collect(Collectors.toList());
                }
                excelWriter.write(convertData, writeSheet);
                if (isEnd) {
                    break;
                }
                page++;
            } while (CollUtil.isNotEmpty(datas));
        } finally {
            if (excelWriter != null) {
                excelWriter.close();
            }
            fileId = uploadExportFile.executeUploadStrategy(FileUtils.getMultipartFile(file), FilePathEnum.EXCEL.getPath());
            FileUtil.del(file);
        }
        return fileId;
    }

    /**
     * 导出大文件 需要传入表头及该列对应的字段名 通过反射获取
     *
     * @param workName      任务名称，还会作为Excel的sheet名
     * @param getData       分页获取数据，传入参数为
     * @param heads         表头
     * @param writeHandlers 编写处理程序
     * @return {@link String}
     */
    public static <T> String exportBig(String workName, Function<Integer, List<T>> getData, List<List<String>> heads, List<String> fieldNames, WriteHandler... writeHandlers) {
        File file = Paths.get( uploadExportFile.getUploadPath(FilePathEnum.TMP.getPath()), StrUtil.format("{}_{}", workName, IdUtil.fastSimpleUUID()) + ".xlsx").toFile();
        String fileId = null;
        // 第一个writeHandler为样式配置
        ExcelWriter excelWriter = null;
        try {
            ExcelWriterBuilder builder = EasyExcel.write(file).head(heads).registerWriteHandler(ExcelStyleHandler.defaultStyleHandler());
            if (ArrayUtil.isNotEmpty(writeHandlers)) {
                for (WriteHandler writeHandler : writeHandlers) {
                    builder.registerWriteHandler(writeHandler);
                }
            }
            excelWriter = builder.build();
            WriteSheet writeSheet = EasyExcel.writerSheet(workName).build();
            int page = 1;
            List<T> datas = null;
            boolean isEnd = false;
            do {
                datas = getData.apply(page);
                if (CollUtil.isEmpty(datas)) {
                    datas = new ArrayList<>();
                    isEnd = true;
                }
                if (isEnd) {
                    break;
                }
                excelWriter.write(parseToExportData(datas, fieldNames), writeSheet);
                page++;
            } while (CollUtil.isNotEmpty(datas));
        } finally {
            if (excelWriter != null) {
                excelWriter.close();
            }
            fileId =uploadExportFile.executeUploadStrategy(FileUtils.getMultipartFile(file), FilePathEnum.EXCEL.getPath());
            FileUtil.del(file);
        }
        return fileId;
    }

    private static <T> List<List<String>> parseToExportData(List<T> datas, List<String> fieldNames) {
        List<List<String>> rowDatas = new ArrayList<>();
        for (T data : datas) {
            List<String> rowData = new ArrayList<>();
            for (String fieldName : fieldNames) {
                Map<String,String> map=(Map<String, String>) data;
                if(map.get(fieldName)==null){
                    rowData.add(StrUtil.EMPTY);
                }else{
                    rowData.add(map.get(fieldName));
                }

//                if (!ReflectUtil.hasField(data.getClass(), fieldName)) {
//                    rowData.add(StrUtil.EMPTY);
//                    continue;
//                }
//                Object fieldValue = ReflectUtil.getFieldValue(data, fieldName);
//                if (fieldValue == null) {
//                    rowData.add(StrUtil.EMPTY);
//                } else {
//                    rowData.add(fieldValue.toString());
//                }
            }
            rowDatas.add(rowData);
        }
        return rowDatas;
    }

    public static <T> String importExcel(String fileId, String workName, Consumer<List<T>> consumer, Class<T> clazz, int rowNums) {
        String filePath = uploadExportFile.getFilePath(fileId);
        if (StrUtil.isEmpty(filePath)) {
            throw new BizException("导入失败，未找到对应文件");
        }

        String businessId = BusinessWorkUtils.startBusiness(BusinessWorkConst.WORK_TYPE_IMPORT,
                StrUtil.format("{}_{}", workName, fileId), fileId, null);
        CompletableFutureUtils.runAsync(() -> {
            try {
                long count = getCount(filePath);
                EasyExcel.read(filePath, clazz, new PageExcelHandler<>(consumer, count, businessId)).headRowNumber(rowNums).sheet().doRead();
            } finally {
                BusinessWorkUtils.finishBusiness(businessId);
            }
        }, "");
        return businessId;
    }

    /**
     * 同步导入
     * @param fileId
     * @param workName
     * @param consumer
     * @param clazz
     * @param rowNums
     * @return
     * @param <T>
     */
    public static <T> String importExcelSync(String fileId, String workName, Consumer<List<T>> consumer, Class<T> clazz, int rowNums) {
        String filePath =  uploadExportFile.getFilePath(fileId);
        if (StrUtil.isEmpty(filePath)) {
            throw new BizException("导入失败，未找到对应文件");
        }

        String businessId = BusinessWorkUtils.startBusiness(BusinessWorkConst.WORK_TYPE_IMPORT,
                StrUtil.format("{}_{}", workName, fileId), fileId, null);

        try {
            long count = getCount(filePath);
            EasyExcel.read(filePath, clazz, new PageExcelHandler<>(consumer, count, businessId)).headRowNumber(rowNums).sheet().doRead();
        } finally {
            BusinessWorkUtils.finishBusiness(businessId);
        }
        return businessId;
    }
}
