package it.thecoder.rendcore.unit.resource;


import it.thecoder.rendcore.budget.api.DocumentResource;
import it.thecoder.rendcore.budget.dto.search.SearchBudgetAccounting;
import it.thecoder.rendcore.budget.service.CSVService;
import it.thecoder.rendcore.budget.service.XLSXService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentResourceTest {

    @InjectMocks
    private DocumentResource documentResource;

    @Mock
    private CSVService csvService;

    @Mock
    private XLSXService xlsxService;


    @Test
    @Order(1)
    void generateCSVTest() {
        SearchBudgetAccounting fakeRequest = new SearchBudgetAccounting();
        Response fakeResult = Response.ok(new ByteArrayInputStream("test,data".getBytes()))
                .type("text/csv")
                .header("Content-Disposition", "attachment; filename=test.csv")
                .build();

        when(csvService.createAccountingCSV(any(SearchBudgetAccounting.class))).thenReturn(fakeResult);

        Response response = documentResource.generateCSV(fakeRequest);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(csvService,times(1)).createAccountingCSV(any(SearchBudgetAccounting.class));
    }

    @Test
    @Order(2)
    void generateCSVWithAllDataTest() {
        Response fakeResult = Response.ok(new ByteArrayInputStream("test,data".getBytes()))
                .type("text/csv")
                .header("Content-Disposition", "attachment; filename=test.csv")
                .build();
        when(csvService.createFullExportCSV()).thenReturn(fakeResult);
        Response response = documentResource.generateCSVWithAllData();
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(csvService,times(1)).createFullExportCSV();
    }

    @Test
    @Order(3)
    void generateXLSXAccountingTest() {
        SearchBudgetAccounting fakeRequest = new SearchBudgetAccounting();
        byte[] testData = "xlsx-content".getBytes();
        when(xlsxService.exportbudgetAccountingXLSX(any(SearchBudgetAccounting.class)))
                .thenReturn(testData);
        Response response = documentResource.generateXLSXAccounting(fakeRequest);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                response.getHeaderString("Content-Type"));
        assertTrue(response.getHeaderString("Content-Disposition").contains("attachment"));
        verify(xlsxService,times(1)).exportbudgetAccountingXLSX(any(SearchBudgetAccounting.class));
    }

    @Test
    @Order(4)
    void generateCSVNoContentTest() {
        SearchBudgetAccounting fakeRequest = new SearchBudgetAccounting();
        Response expectedResponse = Response.status(Response.Status.NO_CONTENT).build();
        when(csvService.createAccountingCSV(any(SearchBudgetAccounting.class)))
                .thenReturn(expectedResponse);


        Response actualResponse = documentResource.generateCSV(fakeRequest);


        assertNotNull(actualResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), actualResponse.getStatus());
        verify(csvService, times(1)).createAccountingCSV(any(SearchBudgetAccounting.class));
    }


    @Test
    @Order(5)
    void generateCSVWithAllDataTestNoContentTest() {
        Response expectedResponse = Response.status(Response.Status.NO_CONTENT).build();
        when(csvService.createFullExportCSV()).thenReturn(expectedResponse);


        Response actualResponse = documentResource.generateCSVWithAllData();


        assertNotNull(actualResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), actualResponse.getStatus());
        verify(csvService, times(1)).createFullExportCSV();
    }

    @Test
    @Order(6)
    void generateXLSXAccountingTestNoContent() {
        SearchBudgetAccounting fakeRequest = new SearchBudgetAccounting();
        byte[] emptyData = new byte[0];
        when(xlsxService.exportbudgetAccountingXLSX(any(SearchBudgetAccounting.class)))
                .thenReturn(emptyData);

        // Act
        Response actualResponse = documentResource.generateXLSXAccounting(fakeRequest);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), actualResponse.getStatus());
        verify(xlsxService, times(1)).exportbudgetAccountingXLSX(any(SearchBudgetAccounting.class));
    }



}
