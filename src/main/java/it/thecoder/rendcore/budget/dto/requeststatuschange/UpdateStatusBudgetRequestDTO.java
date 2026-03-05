package it.thecoder.rendcore.budget.dto.requeststatuschange;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class UpdateStatusBudgetRequestDTO {

    @NotNull(message = "New budget status must be valorized")
    private String newBudgetStatusId;

    private String note;
}
