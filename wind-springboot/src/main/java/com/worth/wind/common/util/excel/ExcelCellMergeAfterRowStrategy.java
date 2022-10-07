package com.worth.wind.common.util.excel;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

@Log4j2
public class ExcelCellMergeAfterRowStrategy implements RowWriteHandler {
    private int mergeRowIndex;//从哪一行开始合并
    private int[] mergeColumnIndex;//excel合并的列
    private int[] signNum;//合并的标识列
    private long total;//总行数

    private int lastRow;
    private int firstCol;
    private int lastCol;
    private int firstRow;

    private int mergeCount = 1;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public ExcelCellMergeAfterRowStrategy(int mergeRowIndex, int[] mergeColumnIndex, int[] signNum, long total) {
        this.mergeRowIndex = mergeRowIndex;
        this.mergeColumnIndex = mergeColumnIndex;
        this.signNum = signNum;
        this.total = total;
    }

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        //当前行
        int curRowIndex = row.getRowNum();

        if (curRowIndex == 0) {
            //赋初值 第一行
            firstRow = curRowIndex;
        }
        //开始合并位置
        if (curRowIndex >= mergeRowIndex) {
            mergeWithPrevAnyRow(writeSheetHolder.getSheet(), curRowIndex, row, signNum);
        }
    }

    public void mergeWithPrevAnyRow(Sheet sheet, int curRowIndex, Row row, int[] signNum) {
        //判断是否合并单元格
        boolean curEqualsPre = false;
        for (int index : signNum) {
            Cell currentCell = row.getCell(index);
            Object currentData = currentCell.getCellType() == CellType.STRING ? currentCell.getStringCellValue() : currentCell.getNumericCellValue();
            Row preRow = row.getSheet().getRow(curRowIndex - 1);
            Cell lastCell = preRow.getCell(index);
            Object preData = lastCell.getCellType() == CellType.STRING ? lastCell.getStringCellValue() : lastCell.getNumericCellValue();
            if (StrUtil.isNotBlank(StrUtil.toString(currentData)) && StrUtil.isNotEmpty(StrUtil.toString(preData)) && currentData.equals(preData)) {
                curEqualsPre = true;
            } else {
                curEqualsPre = false;
                break;
            }
        }

        //判断前一个和后一个相同 并且 标识位相同
        if (curEqualsPre) {
            lastRow = curRowIndex;
            mergeCount++;
        }
        //excel过程中合并
        if (!curEqualsPre && mergeCount > 1) {
            mergeSheet(firstRow, lastRow, mergeColumnIndex, sheet);
            mergeCount = 1;
        }
        //excel结尾处合并
        if (mergeCount > 1 && total == curRowIndex) {
            mergeSheet(firstRow, lastRow, mergeColumnIndex, sheet);
            mergeCount = 1;
        }

        if (total == curRowIndex && total != 1) {
            mergeSheet(1, (int) total, new int[]{0}, sheet);
        }

        if (!curEqualsPre) {
            firstRow = curRowIndex;
        }
    }

    private void mergeSheet(int firstRow, int lastRow, int[] mergeColumnIndex, Sheet sheet) {
        for (int colNum : mergeColumnIndex) {
            firstCol = colNum;
            lastCol = colNum;
            CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
            sheet.addMergedRegion(cellRangeAddress);
        }
    }
}
