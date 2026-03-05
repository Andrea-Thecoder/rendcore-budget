package it.thecoder.rendcore.budget.dto.requeststatuschange;

import it.thecoder.rendcore.budget.dto.employee.SummaryEmployeeDTO;
import it.thecoder.rendcore.budget.model.BudgetRequest;
import it.thecoder.rendcore.budget.model.BudgetStatusChange;
import it.thecoder.rendcore.budget.model.BudgetStatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetailRequestStatusChangeDTO {

    private UUID id;
    private BudgetRequest request;
    private BudgetStatusType oldStatus;
    private BudgetStatusType newStatus;
    private SummaryEmployeeDTO perfomedEmployee;
    private String note;


    public static DetailRequestStatusChangeDTO of(BudgetStatusChange rsc) {
        DetailRequestStatusChangeDTO dto = new DetailRequestStatusChangeDTO();
        dto.setId(rsc.getId());
        dto.setRequest(rsc.getRequest());
        dto.setOldStatus(rsc.getOldStatus());
        dto.setNewStatus(rsc.getNewStatus());
        dto.setNote(rsc.getNote());
        return dto;
    }
}
