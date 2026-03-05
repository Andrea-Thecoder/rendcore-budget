package it.thecoder.rendcore.unit.service;

import it.thecoder.rendcore.budget.dto.record.BudgetAccountingData;
import it.thecoder.rendcore.budget.dto.search.SearchBudgetAccounting;
import it.thecoder.rendcore.budget.model.BudgetRequest;
import it.thecoder.rendcore.budget.model.BudgetStatusType;
import it.thecoder.rendcore.budget.model.BudgetType;
import it.thecoder.rendcore.budget.model.enumerator.TransactionType;
import it.thecoder.rendcore.budget.service.BudgetRequestService;
import it.thecoder.rendcore.budget.service.CSVService;
import it.thecoder.rendcore.budget.service.VBudgetAccountingService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CSVServiceTest {

    @Mock
    private BudgetRequestService budgetRequestService;

    @Mock
    private VBudgetAccountingService vBudgetAccountingService;

    @Spy
    @InjectMocks
    private CSVService csvService;


    @Test
    @Order(1)
    void createFullExportCSVTest() throws IOException {
        BudgetRequest br = mock(BudgetRequest.class);
        populateBudgetRequest(br);
        when(budgetRequestService.findAllBudgetRequests())
                .thenReturn(List.of(br));

        Response response = csvService.createFullExportCSV();

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("text/csv", response.getMediaType().toString());

        String contentDisposition =
                response.getHeaderString("Content-Disposition");

        assertNotNull(contentDisposition);
        assertTrue(contentDisposition.startsWith("attachment; filename=budget-requests_"));


        InputStream is = (InputStream) response.getEntity();
        String csv = new String(is.readAllBytes());

        assertTrue(csv.contains("title"));
        assertTrue(csv.contains("Test"));
        assertTrue(csv.contains(BigDecimal.TEN.toString()));

    }

    @Test
    @Order(2)
    void createFullExportCSVNoContentTest() {
        when(budgetRequestService.findAllBudgetRequests())
                .thenReturn(List.of());

        Response response = csvService.createFullExportCSV();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        assertNull(response.getEntity());
        assertNull(response.getHeaderString("Content-Disposition"));
        assertNull(response.getMediaType());
    }

    @Test
    @Order(3)
    void createAccountingCSVTest() throws IOException {
        BudgetAccountingData data =
                new BudgetAccountingData(
                        "2024-01",
                        YearMonth.of(2024, 1),
                        BigDecimal.valueOf(100),
                        BigDecimal.valueOf(40),
                        BigDecimal.valueOf(60)
                );

        when(vBudgetAccountingService.getBudgetData(any(SearchBudgetAccounting.class)))
                .thenReturn(List.of(data));

        Response response = csvService.createAccountingCSV(mock(SearchBudgetAccounting.class));

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("text/csv", response.getMediaType().toString());

        String contentDisposition =
                response.getHeaderString("Content-Disposition");

        assertNotNull(contentDisposition);
        assertTrue(contentDisposition.startsWith("attachment; filename=budget-report_"));

        InputStream is = (InputStream) response.getEntity();
        String csv = new String(is.readAllBytes());

        assertTrue(csv.contains("2024-01"));
        assertTrue(csv.contains("TOTAL"));
        assertTrue(csv.contains(BigDecimal.valueOf(60).toString()));
    }


    @Test
    @Order(4)
    void createAccountingCSVNoContentTest() {
        when(vBudgetAccountingService.getBudgetData(any()))
                .thenReturn(List.of());

        Response response = csvService.createAccountingCSV(mock(SearchBudgetAccounting.class));

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        assertNull(response.getEntity());
        assertNull(response.getHeaderString("Content-Disposition"));
        assertNull(response.getMediaType());
    }


    private void populateBudgetRequest(BudgetRequest br) {
        when(br.getId()).thenReturn(UUID.randomUUID());
        when(br.getTitle()).thenReturn("Test");
        when(br.getDescription()).thenReturn("Desc");
        when(br.getAmount()).thenReturn(BigDecimal.TEN);
        when(br.getCurrency()).thenReturn("EUR");
        when(br.getTransactionType()).thenReturn(TransactionType.INCOME);
        when(br.getBudgetStatus()).thenReturn(mock(BudgetStatusType.class));
        when(br.getBudgetType()).thenReturn(mock(BudgetType.class));
        when(br.getUserRequestId()).thenReturn(UUID.randomUUID());
        when(br.get_createdAt()).thenReturn(OffsetDateTime.now());
    }

}
