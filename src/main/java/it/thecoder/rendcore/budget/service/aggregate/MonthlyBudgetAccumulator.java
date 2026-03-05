package it.thecoder.rendcore.budget.service.aggregate;

import it.thecoder.rendcore.budget.model.VBudgetAccounting;
import it.thecoder.rendcore.budget.model.enumerator.TransactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class MonthlyBudgetAccumulator {

    private BigDecimal totalIncome = BigDecimal.ZERO;
    private BigDecimal totalExpense = BigDecimal.ZERO;

    public void add(VBudgetAccounting vBudgetAccounting) {
        if (vBudgetAccounting == null) return;

        if (TransactionType.INCOME == vBudgetAccounting.getTransactionType())
            totalIncome = totalIncome.add(vBudgetAccounting.getAmount());
        else
            totalExpense = totalExpense.add(vBudgetAccounting.getAmount());
    }

    public BigDecimal getBalance() {
        return totalIncome.subtract(totalExpense);
    }
}
