package it.thecoder.rendcore.budget.validator;

import it.thecoder.rendcore.budget.annotation.ValidYearMonthRange;
import it.thecoder.rendcore.budget.dto.search.SearchBudgetAccounting;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.time.YearMonth;

public class YearMonthRangeValidator  implements ConstraintValidator<ValidYearMonthRange, SearchBudgetAccounting> {

    @Override
    public boolean isValid(SearchBudgetAccounting value,
                           ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        String startDate = value.getStartDate();
        String endDate   = value.getEndDate();

        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            return true;
        }

        YearMonth start = YearMonth.parse(startDate);
        YearMonth end   = YearMonth.parse(endDate);

        return !end.isBefore(start);
    }
}
