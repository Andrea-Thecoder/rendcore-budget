package it.thecoder.rendcore.budget.dto.budgetrequest;

import it.thecoder.rendcore.budget.dto.employee.SummaryEmployeeDTO;
import it.thecoder.rendcore.budget.dto.requeststatuschange.DetailRequestStatusChangeDTO;
import it.thecoder.rendcore.budget.model.BudgetRequest;
import it.thecoder.rendcore.budget.model.BudgetType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import static it.thecoder.rendcore.budget.utils.DateUtils.localDateTimeFromOffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public final class DetailBudgetRequestDTO extends BaseBudgetRequestDTO {

    private String description;
    private LocalDateTime createdAt;
    private BudgetType budgetType;
    private SummaryEmployeeDTO employee;
    private List<DetailRequestStatusChangeDTO> requestStatusChangeList;

    public static DetailBudgetRequestDTO of (BudgetRequest br){
        DetailBudgetRequestDTO dto = new DetailBudgetRequestDTO();
        dto.setId(br.getId());
        dto.setTitle(br.getTitle());
        dto.setDescription(br.getDescription());
        dto.setAmount(br.getAmount());
        dto.setCurrency(br.getCurrency());
        dto.setTransactionType(br.getTransactionType());
        dto.setBudgetStatus(br.getBudgetStatus());
        dto.setBudgetType(br.getBudgetType());
        dto.setCreatedAt(localDateTimeFromOffsetDateTime(br.get_createdAt()));
        return dto;
    }


}
