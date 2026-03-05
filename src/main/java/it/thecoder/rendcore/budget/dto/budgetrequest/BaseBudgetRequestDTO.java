package it.thecoder.rendcore.budget.dto.budgetrequest;

import it.thecoder.rendcore.budget.model.BudgetRequest;
import it.thecoder.rendcore.budget.model.BudgetStatusType;
import it.thecoder.rendcore.budget.model.enumerator.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public sealed class BaseBudgetRequestDTO permits DetailBudgetRequestDTO{

    protected UUID id ;
    protected String title;
    protected BigDecimal amount;
    protected String currency;

    protected TransactionType transactionType;
    protected BudgetStatusType budgetStatus;


    public static BaseBudgetRequestDTO  of (BudgetRequest budgetRequest){
        BaseBudgetRequestDTO dto = new BaseBudgetRequestDTO();
        dto.setId(budgetRequest.getId());
        dto.setTitle(budgetRequest.getTitle());
        dto.setAmount(budgetRequest.getAmount());
        dto.setCurrency(budgetRequest.getCurrency());
        dto.setTransactionType(budgetRequest.getTransactionType());
        dto.setBudgetStatus(budgetRequest.getBudgetStatus());
        return dto;
    }
}
