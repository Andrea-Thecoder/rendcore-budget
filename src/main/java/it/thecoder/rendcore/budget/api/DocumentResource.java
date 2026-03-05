package it.thecoder.rendcore.budget.api;

import it.thecoder.rendcore.budget.dto.search.SearchBudgetAccounting;
import it.thecoder.rendcore.budget.service.CSVService;
import it.thecoder.rendcore.budget.service.XLSXService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.io.ByteArrayInputStream;

import static it.thecoder.rendcore.budget.utils.GenericUtils.generateFilename;

@Tag(name = "API Document")
@Path("document")
@Produces({"text/csv", "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"})
@Slf4j
public class DocumentResource {

    @Inject
    CSVService csvService;

    @Inject
    XLSXService xlsxService;

    @GET
    @Operation(
            summary = "Download CSV of budget accounting",
            description = "API for downloading CSV budget accounting. Can be filtered for months/years"
    )
    public Response generateCSV(
            @BeanParam @Valid SearchBudgetAccounting request) {
        log.info("DocumentResource - generateCSV");
        return csvService.createAccountingCSV(request);
    }

    @GET
    @Path("all-data")
    @Operation(
            summary = "Download CSV of all budget request data",
            description = "API for downloading CSV all budget request data."
    )
    public Response generateCSVWithAllData() {
        log.info("DocumentResource - generateCSVWithAllData");
        return csvService.createFullExportCSV();
    }

    @GET
    @Path("xlsx-accounting")
    @Operation(
            summary = "Download XLSX of budget accounting.",
            description = "API for downloading XLSX of budget accounting. Can be filter for Months/years"
    )
    public Response generateXLSXAccounting(
            @BeanParam @Valid SearchBudgetAccounting request
    ) {
        log.info("DocumentResource - generateXLSXAccounting");
        byte[] file;
        try {
            file = xlsxService.exportbudgetAccountingXLSX(request);
        } catch (Exception e) {
            return Response.serverError()
                    .entity("Error while creating XLSX.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        if(file.length == 0 ) return Response.status(Response.Status.NO_CONTENT).build();

        String filename = generateFilename("budget-report");
        log.info("createAccountingCSV; File name: {}", filename);
        return Response.ok(new ByteArrayInputStream(file))
                .type("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .header("Content-Disposition", String.format("attachment; filename=%s", filename))
                .build();
    }
}
