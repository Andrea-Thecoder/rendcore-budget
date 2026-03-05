package it.thecoder.rendcore.budget.dto.search;

import io.ebean.ExpressionList;
import it.thecoder.rendcore.budget.model.enumerator.TransactionType;
import jakarta.ws.rs.QueryParam;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

import static it.thecoder.rendcore.budget.utils.GenericUtils.isNotNullOrZero;

public final class SearchBudgetRequest extends BaseSearchRequest {

    @QueryParam("title")
    private String title;

    @QueryParam("min-amount")
    private BigDecimal minAmount;

    @QueryParam("max-amount")
    private BigDecimal maxAmount;

    @QueryParam("transaction")
    private TransactionType transactionType;

    @QueryParam("budget-type")
    private String budgetTypeId;

    @QueryParam("budget-status")
    private String budgetStatusId;

    public <T> void filterBuilder(ExpressionList<T> query) {
        if (StringUtils.isNotBlank(title)) {
            query.ilike("title", "%" + title.trim() + "%");
        }

        if (transactionType != null) {
            query.eq("transactionType", transactionType);
        }

        if (budgetTypeId != null) {
            query.eq("budgetType.id", budgetTypeId);
        }

        if (budgetStatusId != null) {
            query.eq("budgetStatus.id", budgetStatusId);
        }

        if (isNotNullOrZero(minAmount) && isNotNullOrZero(maxAmount)) {
            query.between("amount", minAmount, maxAmount);
        } else if (isNotNullOrZero(minAmount)) {
            query.ge("amount", minAmount);
        } else if (isNotNullOrZero(maxAmount)) {
            query.le("amount", maxAmount);
        }
    }


}
