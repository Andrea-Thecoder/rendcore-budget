package it.thecoder.rendcore.budget.dto.budgetrequest;

import io.ebean.DB;
import it.thecoder.rendcore.budget.model.BudgetRequest;
import it.thecoder.rendcore.budget.model.BudgetType;
import it.thecoder.rendcore.budget.model.enumerator.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public final class InsertBudgetRequestDTO extends BaseInsertBudgetRequestDTO {

    @NotBlank(message = "Title must be valorized")
    private String title;

    @NotNull(message = "Transaction must be valorized")
    private TransactionType transactionType;

    @NotNull(message = "Budget Type must be valorized")
    private String budgetTypeId;

    public BudgetRequest toEntity(){
        BudgetRequest br = new BudgetRequest();
        br.setTitle(this.title.trim());
        br.setDescription(this.description.trim());
        br.setAmount(this.amount);
        br.setCurrency(this.currency);
        br.setTransactionType(this.transactionType);
        br.setBudgetType(DB.reference(BudgetType.class,this.budgetTypeId));
        return br;
    }






}
