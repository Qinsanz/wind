package com.worth.wind.common.util.excel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * excel自适应列宽 拉过去就能直接套用自动适应列宽
 */
@Log4j2
public class ExcelExportAutoWidthStrategy extends AbstractColumnWidthStyleStrategy {
    private static final int MAX_COLUMN_WIDTH = 60;
    private static final int DEFAULT_COLUMN_WIDTH = 10;
    private Map<Integer, Map<Integer, Integer>> CACHE = new HashMap(8);

    public ExcelExportAutoWidthStrategy() {
    }

    /**
     * todo  还需要调整
     * @param writeSheetHolder
     * @param cellDataList
     * @param cell
     * @param head
     * @param relativeRowIndex
     * @param isHead
     */
    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
        boolean needSetWidth = isHead || !CollUtil.isEmpty(cellDataList);
        if (needSetWidth) {
            Map<Integer, Integer> maxColumnWidthMap = (Map) CACHE.get(writeSheetHolder.getSheetNo());
            if (maxColumnWidthMap == null) {
                maxColumnWidthMap = new HashMap(16);
                CACHE.put(writeSheetHolder.getSheetNo(), maxColumnWidthMap);
            }

            Integer columnWidth = this.dataLength(cellDataList, cell, isHead);

            if (columnWidth >= 0) {
                if (columnWidth > MAX_COLUMN_WIDTH) {
                    columnWidth = MAX_COLUMN_WIDTH;
                }
                Integer maxColumnWidth = (Integer) ((Map) maxColumnWidthMap).get(cell.getColumnIndex());
                if (maxColumnWidth == null || columnWidth > maxColumnWidth) {
                    ((Map) maxColumnWidthMap).put(cell.getColumnIndex(), columnWidth);
                    writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), columnWidth * 256);
                }
            }
        }
    }

    private Integer dataLength(List<WriteCellData<?>> cellDataList, Cell cell, Boolean isHead) {
        // TODO: 6/18/2022 验证一下这里的dataList大小
        if (isHead) {
            return cell.getStringCellValue().getBytes().length;
        } else {
            CellData cellData = (CellData) cellDataList.get(0);
            CellDataTypeEnum type = cellData.getType();
            if (type == null) {
                return DEFAULT_COLUMN_WIDTH;
            } else {
                switch (type) {
                    case STRING:
                        double length = 0;
                        String stringValue = cellData.getStringValue();
                        if (StrUtil.isEmpty(stringValue)) {
                            return DEFAULT_COLUMN_WIDTH;
                        }
                        for (char c : stringValue.toCharArray()) {
                            if (CharUtil.isAscii(c)) {
                                length += 1.5;
                            } else {
                                length += 3;
                            }
                        }
                        return (int) Math.ceil(length);
                    case BOOLEAN:
                        return cellData.getBooleanValue().toString().getBytes().length;
                    case NUMBER:
                        return cellData.getNumberValue().toString().getBytes().length;
                    default:
                        return DEFAULT_COLUMN_WIDTH;
                }
            }
        }
    }
}
