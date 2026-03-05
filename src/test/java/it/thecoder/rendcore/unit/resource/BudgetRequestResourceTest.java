package it.thecoder.rendcore.unit.resource;


import it.thecoder.rendcore.budget.api.BudgetRequestResource;
import it.thecoder.rendcore.budget.dto.PagedResultDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.BaseBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.BaseInsertBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.DetailBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.InsertBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.requeststatuschange.UpdateStatusBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.search.SearchBudgetRequest;
import it.thecoder.rendcore.budget.service.BudgetRequestService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetRequestResourceTest {

    @Mock
    private BudgetRequestService budgetRequestService;

    @InjectMocks
    private BudgetRequestResource budgetRequestResource;


    @Test
    @Order(1)
    void findBudgetRequestTest(){

        SearchBudgetRequest fakeRequest = new SearchBudgetRequest();

        PagedResultDTO<BaseBudgetRequestDTO> fakeResult = new PagedResultDTO<>();

        when(budgetRequestService.findBudgetRequest(fakeRequest)).thenReturn(fakeResult);

        var result =  budgetRequestResource.findBudgetRequest(fakeRequest);

        assertNotNull(result);
        verify(budgetRequestService, times(1)).findBudgetRequest(fakeRequest);
    }

    @Test
    @Order(2)
    void getBudgetRequestTest(){
        UUID fakeId =  UUID.randomUUID();
        DetailBudgetRequestDTO fakeResult = mock(DetailBudgetRequestDTO.class);

        when(fakeResult.getId()).thenReturn(fakeId);
        when(budgetRequestService.getBudgetRequestById(fakeId)).thenReturn(fakeResult);

        var result = budgetRequestResource.getBudgetRequest(fakeId);

        assertNotNull(result);
        assertEquals(fakeId, result.getId());
        verify(budgetRequestService,times(1)).getBudgetRequestById(fakeId);
    }

    @Test
    @Order(3)
    void createBudgetRequestTest(){
        InsertBudgetRequestDTO fakeDto = mock(InsertBudgetRequestDTO.class);
        UUID fakeId = UUID.randomUUID();

        when(budgetRequestService.createBudgetRequest(fakeDto)).thenReturn(fakeId);

        var result =  budgetRequestResource.createBudgetRequest(fakeDto);

        assertNotNull(result);
        assertEquals(fakeId,result.getPayload());
        assertNotNull(result.getMessage());
        verify(budgetRequestService, times(1)).createBudgetRequest(fakeDto);
    }

    @Test
    @Order(4)
    void updateBudgetRequestTest(){

        UUID fakeId =  UUID.randomUUID();
        BaseInsertBudgetRequestDTO fakeDto =  mock(BaseInsertBudgetRequestDTO.class);

        when(budgetRequestService.updateBudgetRequest(fakeId, fakeDto)).thenReturn(fakeId);

        var result = budgetRequestResource.updateBudgetRequest(fakeId, fakeDto);

        assertNotNull(result);
        assertEquals(fakeId,result.getPayload());
        assertNotNull(result.getMessage());
        verify(budgetRequestService, times(1)).updateBudgetRequest(fakeId, fakeDto);
    }

    @Test
    @Order(5)
    void updateBudgetRequestStatusTest(){
        UUID fakeId =  UUID.randomUUID();
        UpdateStatusBudgetRequestDTO fakeDto =   mock(UpdateStatusBudgetRequestDTO.class);

        when(budgetRequestService.updateBudgetRequestStatus(fakeId, fakeDto)).thenReturn(fakeId);

        var result = budgetRequestResource.updateBudgetRequestStatus(fakeId, fakeDto);

        assertNotNull(result);
        assertEquals(fakeId,result.getPayload());
        assertNotNull(result.getMessage());
        verify(budgetRequestService, times(1)).updateBudgetRequestStatus(fakeId, fakeDto);

    }

    @Test
    @Order(6)
    void deleteBudgetRequestTest(){
        UUID fakeId =  UUID.randomUUID();

        doNothing().when(budgetRequestService).deleteBudgetRequest(fakeId);

        var result = budgetRequestResource.deleteBudgetRequest(fakeId);

        assertNotNull(result);
        assertNotNull(result.getMessage());
        assertNull(result.getPayload());
        verify(budgetRequestService, times(1)).deleteBudgetRequest(fakeId);
    }
}
