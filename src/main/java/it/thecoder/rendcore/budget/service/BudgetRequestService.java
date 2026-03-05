package it.thecoder.rendcore.budget.service;


import io.ebean.Database;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import io.ebean.Transaction;
import it.thecoder.rendcore.budget.cache.EmployeeCacheService;
import it.thecoder.rendcore.budget.dto.PagedResultDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.BaseBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.BaseInsertBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.DetailBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.InsertBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.requeststatuschange.UpdateStatusBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.search.SearchBudgetRequest;
import it.thecoder.rendcore.budget.exception.ServiceException;
import it.thecoder.rendcore.budget.model.BudgetRequest;
import it.thecoder.rendcore.budget.model.BudgetStatusType;
import it.thecoder.rendcore.budget.security.JWTInspector;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class BudgetRequestService {

    @Inject
    Database db;

    @Inject
    JWTInspector jwtInspector;

    @Inject
    RequestStatusChangeService requestStatusChangeService;

    @Inject
    EmployeeCacheService employeeCacheService;

    @Inject
    BudgetStatusTransitionService budgetStatusTransitionService;

    public PagedResultDTO<BaseBudgetRequestDTO> findBudgetRequest(
            SearchBudgetRequest request) {
        log.info("findBudgetRequest : starting finding budget request...");
        ExpressionList<BudgetRequest> budgetQuery = db.find(BudgetRequest.class)
                .setLabel("findBudgetRequest")
                .where();

        request.filterBuilder(budgetQuery);
        request.pagination(budgetQuery, "_createdAt DESC, title ASC, amount DESC");

        PagedList<BudgetRequest> budgetPaged = budgetQuery.findPagedList();
        return PagedResultDTO.of(budgetPaged, BaseBudgetRequestDTO::of);
    }

    public DetailBudgetRequestDTO getBudgetRequestById(UUID id) {
        log.info("getBudgetRequestById: Starting retrieving budget request with id : {}", id);
        BudgetRequest br = budgetRequestById(id);
        DetailBudgetRequestDTO dto = DetailBudgetRequestDTO.of(br);
        dto.setRequestStatusChangeList(requestStatusChangeService.findRequestStatusChangeByRequestId(id));
        dto.setEmployee(employeeCacheService.getEmployeeSummary(br.getUserRequestId()));
        return dto;
    }

    public UUID createBudgetRequest(InsertBudgetRequestDTO dto) {
        log.info("createBudgetRequest: Creating new budget request...");
        try (Transaction tx = db.beginTransaction()) {
            BudgetRequest br = dto.toEntity();
            br.setBudgetStatus(db.reference(BudgetStatusType.class, RequestStatusChangeService.DRAFT_STATE));
            br.setUserRequestId(jwtInspector.getUserId());
            br.insert(tx);
            requestStatusChangeService.createRequestStatusChange(br, tx);
            tx.commit();
            return br.getId();
        } catch (Exception e) {
            log.error("createBudgetRequest: Error while creating new  budget request. Error message: {}", e.getMessage());
            throw new ServiceException("Error while creating new budget request. Please try again later.");
        }
    }


    public UUID updateBudgetRequest(UUID id, BaseInsertBudgetRequestDTO dto) {
        log.info("updateBudgetRequest : Starting updating information for budget request with ID : {}", id);
        BudgetRequest br = budgetRequestById(id);
        validateUpdatable(br);
        try (Transaction tx = db.beginTransaction()) {
            dto.toUpdate(br);
            br.save(tx);
            tx.commit();
            return br.getId();
        } catch (Exception e) {
            log.error("updateBudgetRequest: Error while updating budget request with ID: {} . Error message: {}", id, e.getMessage());
            throw new ServiceException("Error while updating budget request. Please try again later.");
        }
    }

    public UUID updateBudgetRequestStatus(UUID id, UpdateStatusBudgetRequestDTO dto) {
        log.info("updateBudgetRequestStatus: Starting updating status for budget request with id : {}", id);
        BudgetRequest br = budgetRequestById(id);
        budgetStatusTransitionService.validate(br.getBudgetStatus().getId(), dto.getNewBudgetStatusId());
        try (Transaction tx = db.beginTransaction()) {
            BudgetStatusType oldStatus = br.getBudgetStatus();
            br.setBudgetStatus(db.reference(BudgetStatusType.class, dto.getNewBudgetStatusId()));
            requestStatusChangeService.insertRequestStatusChangeForUpdate(br, oldStatus, dto.getNote(), tx);
            br.save(tx);
            tx.commit();
            return br.getId();
        } catch (Exception e) {
            log.error("updateBudgetRequestStatus: Error while updating status for budget request with ID: {} . Error message: {}", id, e.getMessage());
            throw new ServiceException("Error while updating status budget request. Please try again later.");
        }
    }

    public void deleteBudgetRequest(UUID id) {
        log.info("deleteBudgetRequest: Deleting budget request with ID : {}", id);
        BudgetRequest br = budgetRequestById(id);
        budgetStatusTransitionService.validate(br.getBudgetStatus().getId(), RequestStatusChangeService.CANCELLED_STATE);
        try (Transaction tx = db.beginTransaction()) {
            BudgetStatusType oldStatus = br.getBudgetStatus();
            br.setBudgetStatus(db.reference(BudgetStatusType.class, RequestStatusChangeService.CANCELLED_STATE));
            requestStatusChangeService.insertRequestStatusChangeForUpdate(br, oldStatus, tx);
            br.update(tx);
            tx.commit();
        } catch (Exception e) {
            log.error("deleteBudgetRequest: Error while deleting a budget request with ID: {} . Error message: {}", id, e.getMessage());
            throw new ServiceException("Error while deleting budget request. Please try again later.");
        }
    }

    public List<BudgetRequest> findAllBudgetRequests() {
        log.info("findAllBudgetRequests: Starting find all budget requests.");
        return db.find(BudgetRequest.class)
                .setLabel("findAllBudgetRequests")
                .where()
                .orderBy("_created_at desc")
                .findList();
    }


    private BudgetRequest budgetRequestById(UUID id) {
        return db.find(BudgetRequest.class)
                .setLabel("getBudgetRequestById")
                .where()
                .idEq(id)
                .findOneOrEmpty()
                .orElseThrow(() -> {
                    log.error("Error while retrieving budget request with ID : {}", id);
                    return new ServiceException("Budget request with ID: " + id + " not found.");
                });
    }

    public void validateUpdatable(BudgetRequest br) {
        if (!RequestStatusChangeService.DRAFT_STATE.equals(br.getBudgetStatus().getId())) {
            log.info(
                    "validateUpdatable: Cannot update budget request with ID: {} because its status is not DRAFT",
                    br.getId()
            );
            throw new ServiceException("This budget request cannot be updated because it is not in DRAFT status.");
        }
    }



}
