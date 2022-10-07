package com.worth.wind.common.util.excel;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.worth.wind.common.util.BusinessWorkUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.function.Consumer;

public class PageExcelHandler<T> implements ReadListener<T> {

    public static int BATCH_COUNT = 1000;

    /** 当前处理到的条数 */
    private long currentPos = 0;

    /** Excel数据总数 */
    private final long count;

    private final String businessId;

    private List<T> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    private final Consumer<List<T>> consumer;

    public PageExcelHandler(Consumer<List<T>> consumer, long count, String businessId) {
        this.consumer = consumer;
        this.count = count;
        this.businessId = businessId;
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            consumer.accept(cachedDataList);
            currentPos += cachedDataList.size();
            updateProgress();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (CollectionUtils.isNotEmpty(cachedDataList)) {
            consumer.accept(cachedDataList);
        }
    }

    private void updateProgress() {
        double progress = NumberUtil.div(currentPos, count, 2) * 100;
        BusinessWorkUtils.updateBusinessProgress(businessId, (int) progress);
        cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    }
}
