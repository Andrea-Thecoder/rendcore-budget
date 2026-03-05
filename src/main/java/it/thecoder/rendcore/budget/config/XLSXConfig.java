package it.thecoder.rendcore.budget.config;

import io.smallrye.config.ConfigMapping;
import jakarta.ws.rs.DefaultValue;

@ConfigMapping(prefix = "xlsx.config")
public interface XLSXConfig {


    /**
     * Maximum range (in months) between 'from' and 'to' dates
     * for which Excel sheets are generated on a monthly basis.
     * <p>
     * If the difference between 'from' and 'to' is less than or
     * equal to this value, monthly sheets are generated.
     * If the range exceeds this value, sheets are generated quarterly.
     */
    @DefaultValue("12")
    int monthsSheet();

    /**
     * Maximum range (in months) between 'from' and 'to' dates
     * for which Excel sheets are generated on a quarterly basis.
     * <p>
     * If the difference between 'from' and 'to' is:
     * - greater than monthsSheet
     * - and less than or equal to this value
     * then quarterly sheets are generated.
     * <p>
     * If the range exceeds this value, sheets are generated annually.
     */
    @DefaultValue("36")
    int quartersSheet();

    @DefaultValue("true")
    boolean autoSizeColumns();

    @DefaultValue("true")
    boolean freezeHeader();

    @DefaultValue("100")
    int zoomLevel();



}
