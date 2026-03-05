package it.thecoder.rendcore.budget.dto.record;

import it.thecoder.rendcore.budget.model.BudgetRequest;
import it.thecoder.rendcore.budget.model.BudgetStatusType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record InsertRequestStatusChange(
        @NotNull BudgetRequest request,
        @NotNull BudgetStatusType oldStatus,
        String note
        ) {
}
