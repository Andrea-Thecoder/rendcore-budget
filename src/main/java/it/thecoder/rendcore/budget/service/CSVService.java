package it.thecoder.rendcore.budget.service;

import it.thecoder.rendcore.budget.dto.record.BudgetAccountingData;
import it.thecoder.rendcore.budget.dto.search.SearchBudgetAccounting;
import it.thecoder.rendcore.budget.model.BudgetRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;

import static it.thecoder.rendcore.budget.utils.GenericUtils.generateFilename;

@ApplicationScoped
@Slf4j
public class CSVService {

    @Inject
    VBudgetAccountingService vBudgetAccountingService;

    @Inject
    BudgetRequestService  budgetRequestService;

    private static final String[] ACCOUNTING_HEADERS = {"Month", "Income", "Expense", "Balance"};
    private static final String[] FULL_EXPORT_HEADERS = {
            "id",
            "title",
            "description",
            "amount",
            "currency",
            "transaction_type",
            "budget_status",
            "budget_type",
            "user_request_id",
            "_created_at",
            "_created_by",
            "_updated_at",
            "_updated_by",
            "version"
    };

    public Response createFullExportCSV(){
        log.info("createFullExportCSV: Starting creating full export CSV.");
        List<BudgetRequest> fullBudgetRequests = budgetRequestService.findAllBudgetRequests();
        if (CollectionUtils.isEmpty(fullBudgetRequests)) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        CSVFormat csvFormat = CSVFormat.EXCEL.builder()
                .setDelimiter(";")
                .setHeader(FULL_EXPORT_HEADERS)
                .setRecordSeparator("\n")
                .build();
        try (
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
                CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)
        ) {
            writefullExportCSV(csvPrinter, fullBudgetRequests);
        } catch (IOException e) {
            log.error("createFullExportCSV; Error while creating CSV. Error message: {}", e.getMessage());
            return Response.serverError()
                    .entity("Error while creating CSV.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        String filename = generateFilename("budget-requests");
        log.info("createFullExportCSV; File name: {}", filename);
        return Response.ok(new ByteArrayInputStream(output.toByteArray()))
                .type("text/csv")
                .header("Content-Disposition", String.format("attachment; filename=%s", filename))
                .build();
    }

    public Response createAccountingCSV(SearchBudgetAccounting request) {
        List<BudgetAccountingData> budgetDataList = vBudgetAccountingService.getBudgetData(request);
        if (CollectionUtils.isEmpty(budgetDataList)) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        CSVFormat csvFormat = CSVFormat.EXCEL.builder()
                .setDelimiter(";")
                .setHeader(ACCOUNTING_HEADERS)
                .setRecordSeparator("\n")
                .build();
        try (
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
                CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)
        ) {
            writeAccountingCSV(csvPrinter, budgetDataList);

        } catch (IOException e) {
            log.error("createAccountingCSV; Error while creating CSV. Error message: {}", e.getMessage());
            return Response.serverError()
                    .entity("Error while creating CSV.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        String filename = generateFilename("budget-report");
        log.info("createAccountingCSV; File name: {}", filename);
        return Response.ok(new ByteArrayInputStream(output.toByteArray()))
                .type("text/csv")
                .header("Content-Disposition", String.format("attachment; filename=%s", filename))
                .build();
    }

    private void writeAccountingCSV(CSVPrinter printer, List<BudgetAccountingData> budgetDataList) throws IOException {
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (BudgetAccountingData budgetData : budgetDataList) {
            totalIncome = totalIncome.add(budgetData.income());
            totalExpense = totalExpense.add(budgetData.expense());
            printer.printRecord(budgetAccountingDataToCsvRecord(budgetData));
        }
        printer.println();
        printer.println();
        printer.printRecord( "TOTAL","INCOME","EXPENSE","BALANCE");
        printer.printRecord( "",totalIncome,totalExpense,totalIncome.subtract(totalExpense));
    }

    private void writefullExportCSV(CSVPrinter printer, List<BudgetRequest> budgetDataList) throws IOException {
        for (BudgetRequest budgetRequest : budgetDataList) {
            printer.printRecord(budgetRequestToCsvRecord(budgetRequest));
        }
    }

    private Object[] budgetRequestToCsvRecord(BudgetRequest budgetRequest){
        return new Object[]{
                budgetRequest.getId(),
                budgetRequest.getTitle(),
                budgetRequest.getDescription(),
                budgetRequest.getAmount(),
                budgetRequest.getCurrency(),
                budgetRequest.getTransactionType(),
                budgetRequest.getBudgetStatus().getId(),
                budgetRequest.getBudgetType().getId(),
                budgetRequest.getUserRequestId(),
                budgetRequest.get_createdAt(),
                budgetRequest.get_createdBy(),
                budgetRequest.get_updatedAt(),
                budgetRequest.get_updatedBy(),
                budgetRequest.get_version()
        };
    }

    private Object[] budgetAccountingDataToCsvRecord(BudgetAccountingData budgetData) {
        return new Object[]{
                budgetData.yearMonth(),
                budgetData.income(),
                budgetData.expense(),
                budgetData.balance()
        };
    }




}
