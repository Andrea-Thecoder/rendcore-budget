package it.thecoder.rendcore.budget.service;

import io.ebean.Database;
import io.quarkus.cache.CacheResult;
import it.thecoder.rendcore.budget.exception.ServiceException;
import it.thecoder.rendcore.budget.model.BudgetStatusTransition;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j

public class BudgetStatusTransitionService {


    @Inject
    Database db;

    public void validate(String from, String to) {
        if (!isStatusAllowed(from, to)) {
            log.error("BudgetStatusTransitionService: Invalid transition from {} →  {}", from, to);
            throw new ServiceException("Invalid transition: " + from + " → " + to);
        }
    }

    @CacheResult(cacheName = "status-transition-cache")
    protected boolean isStatusAllowed(String from, String to) {
        return db.find(BudgetStatusTransition.class)
                .setLabel("findIsAllowedBudgetStatusTransition")
                .where()
                .eq("fromStatus.id", from)
                .eq("toStatus.id", to)
                .exists();
    }





}
