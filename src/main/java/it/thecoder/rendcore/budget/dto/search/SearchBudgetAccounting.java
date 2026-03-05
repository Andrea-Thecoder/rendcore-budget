package it.thecoder.rendcore.budget.dto.search;

import io.ebean.ExpressionList;
import it.thecoder.rendcore.budget.annotation.ValidYearMonthRange;
import it.thecoder.rendcore.budget.utils.DateUtils;
import jakarta.ws.rs.QueryParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ValidYearMonthRange
public class SearchBudgetAccounting {

    @QueryParam("start_date")
    private String startDate;

    @QueryParam("end_date")
    private String endDate;



    private static final String QUERY_FIELD = "createdAt";


    public <T> void filterBuilder(ExpressionList<T> query) {

        LocalDateTime from = DateUtils.toYearMonthStartDateTime(startDate);
        LocalDateTime to = DateUtils.toYearMonthEndDateTime(endDate);
        if (from != null) {
            query.ge(QUERY_FIELD, from);
        }

        if (to != null) {
            query.le(QUERY_FIELD, to);
        }
    }

}
