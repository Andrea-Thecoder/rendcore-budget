package it.thecoder.rendcore.budget.dto.requeststatuschange;

import it.thecoder.rendcore.budget.model.BudgetRequest;
import it.thecoder.rendcore.budget.model.BudgetStatusChange;
import it.thecoder.rendcore.budget.model.BudgetStatusType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class InsertRequestStatusChangeDTO {

    @NotNull(message = "Request must be valorized")
    BudgetRequest request;

    @NotNull(message = "Old Status must be valorized")
    BudgetStatusType oldStatus;

    @NotNull(message = "New Status must be valorized")
    BudgetStatusType newStatus;

    @NotNull(message = "Performed By must be valorized")
    private UUID performedBy;

    private String note;


    public BudgetStatusChange toEntity(){
        BudgetStatusChange rsc = new BudgetStatusChange();
        rsc.setRequest(this.request);
        rsc.setOldStatus(this.oldStatus);
        rsc.setNewStatus(this.newStatus);
        rsc.setPerformedBy(this.performedBy);
        rsc.setNote(this.note);
        return rsc;
    }




}
