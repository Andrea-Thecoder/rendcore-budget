package it.thecoder.rendcore.budget.model.enumerator;

import it.thecoder.rendcore.budget.utils.DateUtils;

/**
 * Defines the aggregation level used to generate Excel sheets
 * based on the time range between the requested start and end dates.
 *
 * <p>The aggregation level is determined by comparing the calculated
 * time range (expressed in months) with two configurable thresholds:</p>
 *
 * <ul>
 *   <li>{@code monthsThreshold} – maximum number of months for which
 *       sheets are generated on a monthly basis</li>
 *   <li>{@code quartersThreshold} – maximum number of months for which
 *       sheets are generated on a quarterly basis</li>
 * </ul>
 *
 * <p>The resolution rules are the following:</p>
 *
 * <ul>
 *   <li>If {@code timeRange} is equal to {@link DateUtils#UNBOUNDED_RANGE}
 *       or greater than {@code quartersThreshold}, yearly sheets are generated</li>
 *   <li>If {@code timeRange} is greater than {@code monthsThreshold}
 *       and less than or equal to {@code quartersThreshold},
 *       quarterly sheets are generated</li>
 *   <li>If {@code timeRange} is less than or equal to {@code monthsThreshold},
 *       monthly sheets are generated</li>
 * </ul>
 *
 * <p>The {@code timeRange} value represents the difference in months
 * between the requested {@code from} and {@code to} dates.</p>
 */
public enum SheetAggregator {
    MONTHLY, QUARTERLY, YEARLY;


    /**
     * Resolves the sheet aggregation level based on the given time range
     * and configuration thresholds.
     *
     * @param timeRange the difference in months between the requested
     *                  start and end dates; may be {@link DateUtils#UNBOUNDED_RANGE}
     * @param monthsThreshold maximum number of months for monthly aggregation
     * @param quartersThreshold maximum number of months for quarterly aggregation
     * @return the resolved {@link SheetAggregator} value
     */
    public static SheetAggregator resolveAggregation(
            long timeRange, int monthsThreshold, int quartersThreshold) {

        if(timeRange == DateUtils.UNBOUNDED_RANGE || timeRange > quartersThreshold)
            return SheetAggregator.YEARLY;

        if (timeRange > monthsThreshold) return SheetAggregator.QUARTERLY;

        return SheetAggregator.MONTHLY;
    }
}
