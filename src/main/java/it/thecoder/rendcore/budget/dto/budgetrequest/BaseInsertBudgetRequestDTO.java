package it.thecoder.rendcore.budget.dto.budgetrequest;

import it.thecoder.rendcore.budget.model.BudgetRequest;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public sealed class BaseInsertBudgetRequestDTO permits InsertBudgetRequestDTO {

    @NotBlank(message = "Description must be valorized")
    protected String description;

    @NotNull(message = "Amount must be valorized")
    @DecimalMin(value = "0.000", inclusive = false)
    protected BigDecimal amount;

    //@DefaultValue("EUR")
    @NotBlank(message = "Currency must be valorized")
    protected String currency;

    public void toUpdate(BudgetRequest br ){
        br.setAmount(amount);
        br.setDescription(description);
        br.setCurrency(currency);
    }
}
