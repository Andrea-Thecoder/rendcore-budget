package it.thecoder.rendcore.unit.service;


import it.thecoder.rendcore.budget.config.XLSXConfig;
import it.thecoder.rendcore.budget.dto.record.BudgetAccountingData;
import it.thecoder.rendcore.budget.dto.search.SearchBudgetAccounting;
import it.thecoder.rendcore.budget.exception.ServiceException;
import it.thecoder.rendcore.budget.model.VBudgetAccounting;
import it.thecoder.rendcore.budget.model.enumerator.TransactionType;
import it.thecoder.rendcore.budget.service.BudgetRequestService;
import it.thecoder.rendcore.budget.service.VBudgetAccountingService;
import it.thecoder.rendcore.budget.service.XLSXService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class XLSXServiceTest {

    @Mock
    private BudgetRequestService budgetRequestService;

    @Mock
    private VBudgetAccountingService vBudgetAccountingService;

    @Mock
    private XLSXConfig xlsxConfig;

    @Spy
    @InjectMocks
    private XLSXService xlsxService;



    @Test
    @Order(1)
    void exportBudgetAccountingXLSXMonthlyTest() {

        SearchBudgetAccounting request = mock(SearchBudgetAccounting.class);
        when(request.getStartDate()).thenReturn("2024-01");
        when(request.getEndDate()).thenReturn("2024-03");

        BudgetAccountingData data = buildFakeAccountingData("2024-01", 2024, 1);

        when(vBudgetAccountingService.getBudgetData(request)).thenReturn(List.of(data));
        when(vBudgetAccountingService.findBudgetAccounting(any())).thenReturn(List.of(buildFakeVBudgetAccounting()));
        when(xlsxConfig.monthsSheet()).thenReturn(12);
        when(xlsxConfig.quartersSheet()).thenReturn(36);

        byte[] result = xlsxService.exportbudgetAccountingXLSX(request);

        assertNotNull(result);
        assertTrue(result.length > 0);
        assertValidXLSXWithData(result);
    }

    @Test
    @Order(2)
    void exportBudgetAccountingXLSXQuarterlyTest() {
        // range tra 12 e 36 mesi → QUARTERLY
        SearchBudgetAccounting request = mock(SearchBudgetAccounting.class);
        when(request.getStartDate()).thenReturn("2022-01");
        when(request.getEndDate()).thenReturn("2023-06");

        BudgetAccountingData data = buildFakeAccountingData("2022-01", 2022, 1);

        when(vBudgetAccountingService.getBudgetData(request)).thenReturn(List.of(data));
        when(xlsxConfig.monthsSheet()).thenReturn(12);
        when(xlsxConfig.quartersSheet()).thenReturn(36);

        byte[] result = xlsxService.exportbudgetAccountingXLSX(request);

        assertNotNull(result);
        assertTrue(result.length > 0);
        assertValidXLSXWithData(result);
    }

    @Test
    @Order(3)
    void exportBudgetAccountingXLSXYearlyTest() {
        SearchBudgetAccounting request = mock(SearchBudgetAccounting.class);
        when(request.getStartDate()).thenReturn("2020-01");
        when(request.getEndDate()).thenReturn("2024-12");

        BudgetAccountingData data = buildFakeAccountingData("2020-01", 2020, 1);

        when(vBudgetAccountingService.getBudgetData(request)).thenReturn(List.of(data));
        when(xlsxConfig.monthsSheet()).thenReturn(12);
        when(xlsxConfig.quartersSheet()).thenReturn(36);

        byte[] result = xlsxService.exportbudgetAccountingXLSX(request);

        assertNotNull(result);
        assertTrue(result.length > 0);
        assertValidXLSXWithData(result);
    }


    @Test
    @Order(4)
    void exportBudgetAccountingXLSXEmptyListTest() {
        when(vBudgetAccountingService.getBudgetData(any())).thenReturn(List.of());

        byte[] result = xlsxService.exportbudgetAccountingXLSX(mock(SearchBudgetAccounting.class));

        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    @Order(5)
    void exportBudgetAccountingXLSXNoRangeTest() {
        SearchBudgetAccounting request = mock(SearchBudgetAccounting.class);
        when(request.getStartDate()).thenReturn("");
        when(request.getEndDate()).thenReturn("");

        BudgetAccountingData data = buildFakeAccountingData("2024-01", 2024, 1);

        when(vBudgetAccountingService.getBudgetData(request)).thenReturn(List.of(data));
        when(vBudgetAccountingService.findOldestYearMonth()).thenReturn(YearMonth.of(2024, 1));
        when(vBudgetAccountingService.findNearestYearMonth()).thenReturn(YearMonth.of(2024, 3));
        when(xlsxConfig.monthsSheet()).thenReturn(12);
        when(xlsxConfig.quartersSheet()).thenReturn(36);

        byte[] result = xlsxService.exportbudgetAccountingXLSX(request);

        assertNotNull(result);
        assertTrue(result.length > 0);
        assertValidXLSXWithData(result);
        verify(vBudgetAccountingService).findOldestYearMonth();
        verify(vBudgetAccountingService).findNearestYearMonth();
    }

    @Test
    @Order(6)
    void exportBudgetAccountingXLSXExceptionTest() {
        SearchBudgetAccounting request = mock(SearchBudgetAccounting.class);
        when(request.getStartDate()).thenReturn("data-malformata"); // ← farà esplodere YearMonth.parse

        BudgetAccountingData data = buildFakeAccountingData("2024-01", 2024, 1);
        when(vBudgetAccountingService.getBudgetData(request)).thenReturn(List.of(data));

        assertThrows(ServiceException.class, () ->
                xlsxService.exportbudgetAccountingXLSX(request)
        );
    }

    private void assertValidXLSXWithData(byte[] result) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(result))) {
            assertTrue(workbook.getNumberOfSheets() > 0, "Deve avere almeno un foglio");

            XSSFSheet summary = workbook.getSheet("Summary");
            assertNotNull(summary, "Il foglio Summary deve esistere");

            assertTrue(summary.getLastRowNum() > 0, "Deve avere almeno una riga dati oltre all'header");

            XSSFRow headerRow = summary.getRow(0);
            assertNotNull(headerRow, "La riga header non deve essere null");
            assertNotNull(headerRow.getCell(0), "La prima colonna dell'header non deve essere null");

            XSSFRow dataRow = summary.getRow(1);
            assertNotNull(dataRow, "La prima riga dati non deve essere null");

        } catch (IOException e) {
            fail("Il risultato non è un XLSX valido: " + e.getMessage());
        }
    }



    private BudgetAccountingData buildFakeAccountingData(String yearMonth, int year, int month) {
        return new BudgetAccountingData(
                yearMonth,
                YearMonth.of(year, month),
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(300)
        );
    }

    private VBudgetAccounting buildFakeVBudgetAccounting() {
        VBudgetAccounting v = new VBudgetAccounting();
        v.setTransactionType(TransactionType.INCOME);
        v.setAmount(BigDecimal.valueOf(500));
        v.setCreatedAt(LocalDateTime.now());
        v.setCurrency("EUR");
        return v;
    }

}
