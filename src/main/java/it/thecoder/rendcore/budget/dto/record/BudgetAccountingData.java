package it.thecoder.rendcore.budget.dto.record;

import it.thecoder.rendcore.budget.service.aggregate.MonthlyBudgetAccumulator;
import it.thecoder.rendcore.budget.utils.DateUtils;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.YearMonth;

@Builder

public record BudgetAccountingData(
        @NotNull String yearMonth,
        @NotNull YearMonth yearMonthObj,
        BigDecimal income,
        BigDecimal expense,
        @NotNull BigDecimal balance
        ) {

        public static BudgetAccountingData buildBudgetAccountingData(
                YearMonth yearMonth,
                MonthlyBudgetAccumulator accumulator
        ) {
                return new BudgetAccountingData(yearMonth, accumulator);
        }

        private BudgetAccountingData(YearMonth yearMonth, MonthlyBudgetAccumulator budgetAccumulator){
                this(
                        DateUtils.monthLabel(yearMonth),
                        yearMonth,
                        budgetAccumulator.getTotalIncome(),
                        budgetAccumulator.getTotalExpense(),
                        budgetAccumulator.getBalance()
                );
        }
}
