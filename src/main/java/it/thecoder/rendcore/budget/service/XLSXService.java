package it.thecoder.rendcore.budget.service;

import it.thecoder.rendcore.budget.config.XLSXConfig;
import it.thecoder.rendcore.budget.dto.record.BudgetAccountingData;
import it.thecoder.rendcore.budget.dto.search.SearchBudgetAccounting;
import it.thecoder.rendcore.budget.exception.ServiceException;
import it.thecoder.rendcore.budget.model.VBudgetAccounting;
import it.thecoder.rendcore.budget.model.enumerator.SheetAggregator;
import it.thecoder.rendcore.budget.model.enumerator.TransactionType;
import it.thecoder.rendcore.budget.utils.DateUtils;
import it.thecoder.rendcore.budget.utils.GenericUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Slf4j

public class XLSXService {

    @Inject
    XLSXConfig xlsxConfig;

    @Inject
    VBudgetAccountingService vBudgetAccountingService;


    private static final String[] SUMMARY_HEADERS = {"PERIOD", "TOTAL INCOME", "TOTAL EXPENSE", "BALANCE"};
    private static final String[] SHEET_HEADERS = {"DATE", "INCOME", "EXPENSE", "BALANCE"};
    private static final String[] MONTLY_SHEET_HEADERS = {"DATE", "INCOME", "EXPENSE", "CURRENCY"};

    public byte[] exportbudgetAccountingXLSX(SearchBudgetAccounting request) {
        log.info("exportXLSX: Starting creating xlsx file");

        List<BudgetAccountingData> accountingList = vBudgetAccountingService.getBudgetData(request);
        if (CollectionUtils.isEmpty(accountingList)) return new byte[0];
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (Workbook workbook = new XSSFWorkbook()) {
            String period = elaborateDateRange(request);
            createSummarySheet(workbook, accountingList, period);
            setupCreateSheet(request, workbook, accountingList);
            workbook.write(output);
            return output.toByteArray();

        } catch (Exception e) {
            log.error("exportXLSX: Error creating xlsx file.", e);
            throw new ServiceException("Error while creating XLSX. Try Again later.");
        }
    }

    private void createSummarySheet(Workbook workbook, List<BudgetAccountingData> accountingList, String period) {
        log.info("createSummarySheet: Starting creating summary sheet");
        Sheet summarySheet = workbook.createSheet("Summary");
        createHeader(summarySheet, SUMMARY_HEADERS);
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        for (BudgetAccountingData accounting : accountingList) {
            totalIncome = totalIncome.add(accounting.income());
            totalExpense = totalExpense.add(accounting.expense());
        }
        double balance = (totalIncome.subtract(totalExpense)).doubleValue();
        Row summaryRow = summarySheet.createRow(1);
        summaryRow.createCell(0).setCellValue(period);
        summaryRow.createCell(1).setCellValue(totalIncome.doubleValue());
        summaryRow.createCell(2).setCellValue(totalExpense.doubleValue());
        summaryRow.createCell(3).setCellValue(balance);
    }

    private String elaborateDateRange(SearchBudgetAccounting request){
        YearMonth from;
        YearMonth to;

        if (StringUtils.isNotBlank(request.getStartDate()))
            from = YearMonth.parse(request.getStartDate());
        else
            from = vBudgetAccountingService.findOldestYearMonth();

        if (StringUtils.isNotBlank(request.getEndDate()))
            to = YearMonth.parse(request.getEndDate());
        else
            to = vBudgetAccountingService.findNearestYearMonth();

        return String.format("%s - %s", from.getYear(), to.getYear());
    }

    private void setupCreateSheet(SearchBudgetAccounting request, Workbook workbook, List<BudgetAccountingData> accountingList) {
        log.info("setupCreateSheet: Setup other sheets");
        long timeRange = DateUtils.extractTimeRange(request.getStartDate(), request.getEndDate());
        SheetAggregator aggregator = SheetAggregator.resolveAggregation(timeRange, xlsxConfig.monthsSheet(), xlsxConfig.quartersSheet());

        switch (aggregator) {
            case MONTHLY -> createMonthlySheet(workbook, accountingList);
            case QUARTERLY -> createQuarterlySheet(workbook, accountingList);
            case YEARLY -> createYearlySheet(workbook, accountingList);
        }
    }

    private void createMonthlySheet(Workbook workbook, List<BudgetAccountingData> accountingList) {
        for (BudgetAccountingData accounting : accountingList) {
            String yearMonth = accounting.yearMonthObj().toString();
            Sheet sheet = workbook.createSheet(yearMonth);
            createHeader(sheet, MONTLY_SHEET_HEADERS);
            List<VBudgetAccounting> budgetAccountings = vBudgetAccountingService.findBudgetAccounting(
                    new SearchBudgetAccounting(yearMonth, yearMonth));
            int rowIndex = 1;
            for (VBudgetAccounting budgetAccount : budgetAccountings) {
                createMonthlyRows(sheet, budgetAccount, rowIndex++);
            }
        }
    }

    private void createQuarterlySheet(Workbook workbook, List<BudgetAccountingData> accountingList) {
        getQuarterlyMap(accountingList).forEach((quarter, accountings) -> {
            Sheet sheet = workbook.createSheet(quarter);
            createHeader(sheet, SHEET_HEADERS);
            int rowIndex = 1;
            for (BudgetAccountingData accounting : accountings) {
                createRows(sheet, accounting, rowIndex++);
            }
        });
    }

    private void createYearlySheet(Workbook workbook, List<BudgetAccountingData> accountingList) {
        getAccountingMap(accountingList).forEach((year, accountings) -> {
            Sheet sheet = workbook.createSheet(year.toString());
            createHeader(sheet, SHEET_HEADERS);
            int rowIndex = 1;
            for (BudgetAccountingData accounting : accountings) {
                createRows(sheet, accounting, rowIndex++);
            }
        });


    }


    private void createRows(Sheet sheet, BudgetAccountingData accounting, int rowIndex) {
        Row row = sheet.createRow(rowIndex);
        double balance = (accounting.income().subtract(accounting.expense())).doubleValue();
        row.createCell(0).setCellValue(accounting.yearMonth());
        row.createCell(1).setCellValue(accounting.income().doubleValue());
        row.createCell(2).setCellValue(accounting.expense().doubleValue());
        row.createCell(3).setCellValue(balance);
    }

    private void createMonthlyRows(Sheet sheet, VBudgetAccounting budgetAccount, int rowIndex) {
        double income = 0;
        double expense = 0;

        if (budgetAccount.getTransactionType() == TransactionType.INCOME)
            income = budgetAccount.getAmount().doubleValue();
        else
            expense = budgetAccount.getAmount().doubleValue();

        Row row = sheet.createRow(rowIndex);
        row.createCell(0).setCellValue(DateUtils.localDateTimeString(budgetAccount.getCreatedAt()));
        row.createCell(1).setCellValue(income);
        row.createCell(2).setCellValue(expense);
        row.createCell(3).setCellValue(budgetAccount.getCurrency());
    }

    private void createHeader(Sheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

    }

    private Map<Integer, List<BudgetAccountingData>> getAccountingMap(List<BudgetAccountingData> accountingList) {
        Map<Integer, List<BudgetAccountingData>> accountingByYear = new HashMap<>();
        for (BudgetAccountingData accounting : accountingList) {
            Integer year = accounting.yearMonthObj().getYear();
            accountingByYear
                    .computeIfAbsent(year, k -> new ArrayList<>())
                    .add(accounting);
        }
        return accountingByYear;
    }

    private Map<String, List<BudgetAccountingData>> getQuarterlyMap(List<BudgetAccountingData> accountingList) {
        Map<String, List<BudgetAccountingData>> accountingByQuarterly = new HashMap<>();
        for (BudgetAccountingData accounting : accountingList) {
            YearMonth yearMonth = accounting.yearMonthObj();
            int year = yearMonth.getYear();
            int quarter = DateUtils.getQuarter(yearMonth);
            String quarterKey = GenericUtils.generateQuarterLabel(quarter, year);
            accountingByQuarterly
                    .computeIfAbsent(quarterKey, k -> new ArrayList<>())
                    .add(accounting);
        }
        return accountingByQuarterly;
    }
}
