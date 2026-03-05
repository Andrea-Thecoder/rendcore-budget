package it.thecoder.rendcore.budget.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;


/**
 * Utility class for common date and time operations used across the application.
 * <p>
 * This class provides helper methods for:
 * <ul>
 *   <li>Converting {@link String} representations of {@link YearMonth} into
 *       {@link LocalDateTime} boundaries (start and end of month)</li>
 *   <li>Handling conversions between {@link OffsetDateTime} and {@link LocalDateTime}</li>
 *   <li>Parsing {@link YearMonth} values from strings</li>
 *   <li>Generating human-readable month labels in Italian locale</li>
 * </ul>
 *
 * <p>
 * All methods are {@code static} and null-safe where applicable.
 * This class is not intended to be instantiated.
 */
@Slf4j
public final class DateUtils {

    private DateUtils() {
    }

    public static final long UNBOUNDED_RANGE = -1L;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy",Locale.ITALIAN);

    /**
     * Converts a {@code yyyy-MM} string into a {@link LocalDateTime}
     * representing the start of the given month (00:00:00).
     *
     * @param yearMonthString the year-month string in {@code yyyy-MM} format
     * @return the start of the month as {@link LocalDateTime}, or {@code null} if blank
     */
    public static LocalDateTime toYearMonthStartDateTime(String yearMonthString) {
        if (StringUtils.isBlank(yearMonthString)) return null;
        YearMonth yearMonth = YearMonth.parse(yearMonthString);
        return yearMonth.atDay(1).atStartOfDay();
    }

    /**
     * Converts a {@code yyyy-MM} string into a {@link LocalDateTime}
     * representing the end of the given month (23:59:59.999999999).
     *
     * @param yearMonthString the year-month string in {@code yyyy-MM} format
     * @return the end of the month as {@link LocalDateTime}, or {@code null} if blank
     */
    public static LocalDateTime toYearMonthEndDateTime(String yearMonthString) {
        if (StringUtils.isBlank(yearMonthString)) return null;
        YearMonth yearMonth = YearMonth.parse(yearMonthString);
        return yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
    }

    /**
     * Converts an {@link OffsetDateTime} into a {@link LocalDateTime}
     * using the system default time zone.
     *
     * @param time the offset date-time to convert
     * @return the corresponding {@link LocalDateTime}, or {@code null} if input is null
     */
    public static LocalDateTime localDateTimeFromOffsetDateTime(OffsetDateTime time) {
        if (time == null) {
            return null;
        }
        return time.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Parses a {@code yyyy-MM} string into a {@link YearMonth}.
     *
     * @param dateString the year-month string in {@code yyyy-MM} format
     * @return the parsed {@link YearMonth}, or {@code null} if blank
     */
    public static YearMonth fromStringToYearMonth(String dateString) {
        if (StringUtils.isBlank(dateString)) return null;
        return YearMonth.parse(dateString);
    }

    /**
     * Builds a human-readable month label from a {@link YearMonth},
     * using the Italian locale (e.g. {@code "gennaio 2024"}).
     *
     * @param yearMonth the year-month value
     * @return a formatted month label, or {@code null} if input is null
     */
    public static String monthLabel(YearMonth yearMonth) {
        if (yearMonth == null) return null;

        return yearMonth.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ITALIAN)
                + " " + yearMonth.getYear();
    }

    /**
     * Builds a human-readable month label from a {@link LocalDateTime},
     * using the Italian locale (e.g. {@code "gennaio 2024"}).
     *
     * @param dateTime the date-time value
     * @return a formatted month label, or {@code null} if input is null
     */
    public static String monthLabel(LocalDateTime dateTime) {
        if (dateTime == null) return null;

        return dateTime.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ITALIAN)
                + " " + dateTime.getYear();
    }

    /**
     * Calculates the time range in months between two dates expressed as {@link YearMonth}.
     * <p>
     * The method converts the input strings into {@link YearMonth} instances and returns
     * the number of whole months between the start and end dates.
     * <p>
     * The result is positive if {@code dateEnd} is after {@code dateStart},
     * zero if they represent the same month,
     * and negative if {@code dateEnd} is before {@code dateStart}.
     *
     * @param dateStart the start date in {@code yyyy-MM} format
     * @param dateEnd   the end date in {@code yyyy-MM} format
     * @return the number of months between start and end, or {@link #UNBOUNDED_RANGE}
     *         if the range is unbounded
     */
    public static long extractTimeRange(String dateStart, String dateEnd) {
        YearMonth from = fromStringToYearMonth(dateStart);
        YearMonth to = fromStringToYearMonth(dateEnd);
        if (from == null || to == null) return UNBOUNDED_RANGE;
        return ChronoUnit.MONTHS.between(from, to);
    }

    public static int getQuarter(YearMonth yearMonth) {
        if (yearMonth == null) return 0;
        int  month = yearMonth.getMonthValue();
        return  (month -1 ) / 3 + 1;
    }

    public static String localDateTimeString(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DATE_TIME_FORMATTER);
    }

}
