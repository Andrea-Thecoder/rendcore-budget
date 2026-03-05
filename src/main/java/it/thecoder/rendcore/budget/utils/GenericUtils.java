package it.thecoder.rendcore.budget.utils;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
@Slf4j

public final class GenericUtils {

    private GenericUtils() {}

    /**
     * Returns {@code true} if the given {@link BigDecimal} is not {@code null}
     * and its numeric value is not zero.
     * <p>
     * The comparison uses {@link BigDecimal#compareTo(BigDecimal)} to ignore scale differences.
     *
     * @param number the {@link BigDecimal} to check, may be {@code null}
     * @return {@code true} if {@code number} is non-null and not zero; {@code false} otherwise
     */
    public static boolean isNotNullOrZero(BigDecimal number) {
        return number != null && number.compareTo(BigDecimal.ZERO) != 0;
    }

    public static String generateFilename(String filenamePrefix){
        return String.format("%s_%s",filenamePrefix, LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm")));
    }

    public static String generateQuarterLabel(int quarter, int year){
        if (quarter == 0 || year == 0) return null;
        return String.format("Q%s - %s", quarter, year);
    }






}
