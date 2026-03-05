package it.thecoder.rendcore.budget.service;

import io.ebean.Database;
import it.thecoder.rendcore.budget.model.BudgetStatusType;
import it.thecoder.rendcore.budget.model.BudgetType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApplicationScoped
@Slf4j


public class LookupService {

    @Inject
    Database db;


    public List<BudgetType> findBudgetType(){
      return db.find(BudgetType.class)
                .setLabel("findBudgetType").findList();
    }

    public List<BudgetStatusType> findBudgetStatusType(){
       return db.find(BudgetStatusType.class)
                .setLabel("findBudgetType").findList();
    }
}
