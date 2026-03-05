package it.thecoder.rendcore.budget.service;

import io.ebean.Database;
import io.ebean.ExpressionList;
import it.thecoder.rendcore.budget.dto.record.BudgetAccountingData;
import it.thecoder.rendcore.budget.dto.search.SearchBudgetAccounting;
import it.thecoder.rendcore.budget.model.VBudgetAccounting;
import it.thecoder.rendcore.budget.service.aggregate.MonthlyBudgetAccumulator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Slf4j

public class VBudgetAccountingService {

    @Inject
    Database db;

    public List<VBudgetAccounting> findBudgetAccounting(SearchBudgetAccounting request){
        ExpressionList<VBudgetAccounting> query = db.find(VBudgetAccounting.class)
                .setLabel("findBudgetAccounting")
                .where();

        request.filterBuilder(query);

        return query.orderBy("createdAt DESC").findList();
    }

    public List<BudgetAccountingData> getBudgetData(SearchBudgetAccounting request){
        List<VBudgetAccounting> vBudgetAccountingList =  findBudgetAccounting(request);

        if(CollectionUtils.isEmpty(vBudgetAccountingList)) return List.of();

        Map<YearMonth, MonthlyBudgetAccumulator> monthlyMap = createMonthlyMap(vBudgetAccountingList);

        if(monthlyMap.isEmpty()) return List.of();

        return monthlyMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> BudgetAccountingData.buildBudgetAccountingData(entry.getKey(),entry.getValue()))
                .toList();

    }

    private Map<YearMonth, MonthlyBudgetAccumulator> createMonthlyMap(List<VBudgetAccounting> vBudgetAccountingList){
        Map<YearMonth, MonthlyBudgetAccumulator> monthlyMap = new HashMap<>();

        for(VBudgetAccounting vBudgetAccounting : vBudgetAccountingList){
            if(vBudgetAccounting.getCreatedAt() == null) continue;

            YearMonth yearMonth = YearMonth.from(vBudgetAccounting.getCreatedAt());

            MonthlyBudgetAccumulator monthlyAccumulator =monthlyMap.computeIfAbsent(yearMonth, k -> new MonthlyBudgetAccumulator());

            monthlyAccumulator.add(vBudgetAccounting);

        }
        return monthlyMap;
    }

    public YearMonth findOldestYearMonth(){
        return db.find(VBudgetAccounting.class)
                .setLabel("findOldestYearMonth")
                .where()
                .orderBy("createdAt ASC")
                .setMaxRows(1)
                .findOneOrEmpty()
                .map(accounting ->  YearMonth.from(accounting.getCreatedAt()))
                .orElse(null);
    }

    public YearMonth findNearestYearMonth(){
        return db.find(VBudgetAccounting.class)
                .setLabel("findNearestYearMonth")
                .where()
                .orderBy("createdAt DESC")
                .setMaxRows(1)
                .findOneOrEmpty()
                .map(accounting ->  YearMonth.from(accounting.getCreatedAt()))
                .orElse(null);
    }

}
