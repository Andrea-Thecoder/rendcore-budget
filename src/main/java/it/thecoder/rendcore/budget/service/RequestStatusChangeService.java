package it.thecoder.rendcore.budget.service;

import io.ebean.Database;
import io.ebean.Transaction;
import it.thecoder.rendcore.budget.cache.EmployeeCacheService;
import it.thecoder.rendcore.budget.dto.requeststatuschange.DetailRequestStatusChangeDTO;
import it.thecoder.rendcore.budget.dto.requeststatuschange.InsertRequestStatusChangeDTO;
import it.thecoder.rendcore.budget.model.BudgetRequest;
import it.thecoder.rendcore.budget.model.BudgetStatusChange;
import it.thecoder.rendcore.budget.model.BudgetStatusType;
import it.thecoder.rendcore.budget.security.JWTInspector;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class RequestStatusChangeService {

    @Inject
    Database db;

    @Inject
    JWTInspector jwt;

    @Inject
    EmployeeCacheService employeeCacheService;

    public static final String DRAFT_STATE = "DRAFT";
    public static final String CANCELLED_STATE = "CANCELLED";
    private static final String CREATED_STATE = "CREATED";
    private static final String CREATED_NOTES = "Initial budget request creation";
    private static final String CANCELLED_NOTES = "Budget request cancelled";


    public List<DetailRequestStatusChangeDTO> findRequestStatusChangeByRequestId(UUID requestId) {
        log.info("RequestStatusChangeService.findRequestStatusChangeByRequestId()");
        List<BudgetStatusChange> budgetStatusChangeList = db.find(BudgetStatusChange.class)
                .setLabel("findRequestStatusChangeByRequestId")
                .where()
                .eq("request.id", requestId)
                .findList();
        if (CollectionUtils.isEmpty(budgetStatusChangeList)) return List.of();
        return budgetStatusChangeList.stream().map(rsc -> {
            DetailRequestStatusChangeDTO dto = new DetailRequestStatusChangeDTO();
            dto.setPerfomedEmployee(employeeCacheService.getEmployeeSummary(rsc.getPerformedBy()));
            return dto;
        }).toList();
    }


    public void insertRequestStatusChangeForUpdate(BudgetRequest br, BudgetStatusType oldStatusType, String note, Transaction tx) {
        log.info("Inserting BudgetStatusChange for update for budget status: {} ", oldStatusType);
        InsertRequestStatusChangeDTO dto = new InsertRequestStatusChangeDTO();
        dto.setOldStatus(oldStatusType);
        dto.setNewStatus(br.getBudgetStatus());
        dto.setRequest(br);
        dto.setPerformedBy(jwt.getUserId());
        if (StringUtils.isNotBlank(note)) dto.setNote(note);
        createRequestStatusChangeNoTransaction(dto, tx);
    }

    public void insertRequestStatusChangeForUpdate(BudgetRequest br, BudgetStatusType oldStatusType, Transaction tx) {
        log.info("Inserting BudgetStatusChange for update for budget status: {} ", oldStatusType);
        InsertRequestStatusChangeDTO dto = new InsertRequestStatusChangeDTO();
        dto.setOldStatus(oldStatusType);
        dto.setNewStatus(br.getBudgetStatus());
        dto.setRequest(br);
        dto.setPerformedBy(jwt.getUserId());
        dto.setNote(CANCELLED_NOTES);
        createRequestStatusChangeNoTransaction(dto, tx);
    }

    public void createRequestStatusChange(BudgetRequest br, Transaction tx) {
        log.info("Inserting BudgetStatusChange for budget status: {}", br.getBudgetStatus());
        InsertRequestStatusChangeDTO dto = new InsertRequestStatusChangeDTO();
        dto.setOldStatus(db.find(BudgetStatusType.class, CREATED_STATE));
        dto.setNewStatus(br.getBudgetStatus());
        dto.setRequest(br);
        dto.setPerformedBy(br.getUserRequestId());
        dto.setNote(CREATED_NOTES);
        createRequestStatusChangeNoTransaction(dto, tx);
    }

    private void createRequestStatusChangeNoTransaction(InsertRequestStatusChangeDTO dto, Transaction tx) {
        BudgetStatusChange rsc = dto.toEntity();
        rsc.insert(tx);
        log.debug("Status change inserted successfully");
    }
}
