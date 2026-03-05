package it.thecoder.rendcore.budget.api;

import it.thecoder.rendcore.budget.dto.SimpleResultDTO;
import it.thecoder.rendcore.budget.model.BudgetStatusType;
import it.thecoder.rendcore.budget.model.BudgetType;
import it.thecoder.rendcore.budget.service.LookupService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Tag(name = "API Lookup")
@Path("lookup")
@Slf4j
public class LookupResource {

    @Inject
    LookupService lookupService;

    @GET
    @Path("budget-type")
    @Operation(summary = "API find budget type", description = "API return a list of all budget type ")
    public SimpleResultDTO<List<BudgetType>> findBudgetType(){
        log.info("LookupResource - findBudgetType");
        return SimpleResultDTO.<List<BudgetType>>builder()
                .payload(lookupService.findBudgetType())
                .build();
    }

    @GET
    @Path("budget-status")
    @Operation(summary = "API find budget status", description = "API return a list of all budget status ")
    public SimpleResultDTO<List<BudgetStatusType>> findBudgetStatus(){
        log.info("LookupResource - findBudgetStatus");
        return SimpleResultDTO.<List<BudgetStatusType>>builder()
                .payload(lookupService.findBudgetStatusType())
                .build();
    }



}
