package it.thecoder.rendcore.budget.utils;

import org.apache.poi.ss.usermodel.*;

public class XLSXStyles {

    public final CellStyle header;
    public final CellStyle amount;
    public final CellStyle date;
    public final CellStyle total;

    private XLSXStyles(
            CellStyle header,
            CellStyle amount,
            CellStyle date,
            CellStyle total
    ) {
        this.header = header;
        this.amount = amount;
        this.date = date;
        this.total = total;
    }

    public static XLSXStyles create(Workbook workbook) {
        DataFormat format = workbook.createDataFormat();

        // HEADER
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);

        CellStyle header = workbook.createCellStyle();
        header.setFont(headerFont);
        header.setAlignment(HorizontalAlignment.CENTER);
        header.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        header.setBorderBottom(BorderStyle.THIN);

        // AMOUNT
        CellStyle amount = workbook.createCellStyle();
        amount.setDataFormat(format.getFormat("#,##0.00"));
        amount.setAlignment(HorizontalAlignment.RIGHT);

        // DATE
        CellStyle date = workbook.createCellStyle();
        date.setDataFormat(format.getFormat("yyyy-mm"));

        // TOTAL
        Font totalFont = workbook.createFont();
        totalFont.setBold(true);

        CellStyle total = workbook.createCellStyle();
        total.setFont(totalFont);
        total.setDataFormat(format.getFormat("#,##0.00"));
        total.setAlignment(HorizontalAlignment.RIGHT);
        total.setBorderTop(BorderStyle.MEDIUM);

        return new XLSXStyles(header, amount, date, total);
    }
}

