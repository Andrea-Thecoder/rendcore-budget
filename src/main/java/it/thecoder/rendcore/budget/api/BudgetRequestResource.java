package it.thecoder.rendcore.budget.api;


import it.thecoder.rendcore.budget.dto.PagedResultDTO;
import it.thecoder.rendcore.budget.dto.SimpleResultDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.BaseBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.BaseInsertBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.DetailBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.InsertBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.requeststatuschange.UpdateStatusBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.search.SearchBudgetRequest;
import it.thecoder.rendcore.budget.service.BudgetRequestService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Tag(name = "API Budget Request")
@Path("budget-request")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class BudgetRequestResource {

    @Inject
    BudgetRequestService  budgetRequestService;

    @GET
    @Operation(
            summary = "Find all budget request",
            description = "API for find all budget request. Can be filtered"
    )
    public PagedResultDTO<BaseBudgetRequestDTO> findBudgetRequest(
            @BeanParam SearchBudgetRequest request
            ){
        log.info("BudgetRequestResource - findBudgetRequest");
        return budgetRequestService.findBudgetRequest(request);
    }

    @GET
    @Path("{id}")
    @Operation(
            summary = "Get Budget Request Detail by ID",
            description = "API for get a Budget Request detail by ID. Include all history of change status."
    )
    public DetailBudgetRequestDTO getBudgetRequest(
            @PathParam("id") UUID id){
        log.info("BudgetRequestResource - getBudgetRequest");
        return budgetRequestService.getBudgetRequestById(id);
    }

    @POST
    @Operation(
            summary = "Create new Budget Request",
            description = "API for create a new budget request."
    )
    public SimpleResultDTO<UUID> createBudgetRequest(
                @Valid InsertBudgetRequestDTO dto
            ){
        log.info("BudgetRequestResource - createBudgetRequest");
        return SimpleResultDTO.<UUID>builder()
                .payload( budgetRequestService.createBudgetRequest(dto))
                .message("Budget Request created successfully")
                .build();
    }

    @PUT
    @Path("{id}")
    @Operation(
            summary = "Update budget request",
            description = "API for update budget request. This work only while stato is flag in CREATED or UPDATE. "
    )
    public SimpleResultDTO<UUID> updateBudgetRequest(
            @PathParam("id") UUID id,
            @Valid BaseInsertBudgetRequestDTO dto
            ){
        log.info("BudgetRequestResource - updateBudgetRequest");
        return SimpleResultDTO.<UUID>builder()
                .payload(budgetRequestService.updateBudgetRequest(id,dto))
                .message("Budget Request  update successfully")
                .build();
    }

    @PUT
    @Path("{id}/status")
    @Operation(
            summary = "Update budget request",
            description = "API for update budget request. This work only while stato is flag in CREATED or UPDATE. "
    )
    public SimpleResultDTO<UUID> updateBudgetRequestStatus(
            @PathParam("id") UUID id,
            @Valid UpdateStatusBudgetRequestDTO dto
    ){
        log.info("BudgetRequestResource - updateBudgetRequestStatus");
        return SimpleResultDTO.<UUID>builder()
                .payload(budgetRequestService.updateBudgetRequestStatus(id,dto))
                .message("Budget Request  update successfully")
                .build();
    }

    @DELETE
    @Path("{id}")
    @Operation(
            summary = "Delete budget request",
            description = "API for delete a budget request. "
    )
    public SimpleResultDTO<Void>  deleteBudgetRequest(
            @PathParam("id") UUID id
    ){
        log.info("BudgetRequestResource - deleteBudgetRequest");
        budgetRequestService.deleteBudgetRequest(id);
        return SimpleResultDTO.<Void>builder()
                .message("Budget Request deleted successfully")
                .build();
    }

}
