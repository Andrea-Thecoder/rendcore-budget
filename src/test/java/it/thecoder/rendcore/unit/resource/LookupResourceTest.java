package it.thecoder.rendcore.unit.resource;


import it.thecoder.rendcore.budget.api.LookupResource;
import it.thecoder.rendcore.budget.model.BudgetStatusType;
import it.thecoder.rendcore.budget.model.BudgetType;
import it.thecoder.rendcore.budget.service.LookupService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LookupResourceTest {

    @InjectMocks
    private LookupResource lookupResource;

    @Mock
    private LookupService lookupService;


    @Test
    @Order(1)
    void findBudgetTypeTest(){

        List<BudgetType> fakeResult = new ArrayList<>();

        when(lookupService.findBudgetType()).thenReturn(fakeResult);

        var result = lookupResource.findBudgetType();

        assertNotNull(result);
        assertEquals(fakeResult, result.getPayload());
        verify(lookupService, times(1)).findBudgetType();
    }

    @Test
    @Order(2)
    void findBudgetStatusTest(){

        List<BudgetStatusType> fakeResult = new ArrayList<>();

        when(lookupService.findBudgetStatusType()).thenReturn(fakeResult);

        var result = lookupResource.findBudgetStatus();

        assertNotNull(result);
        assertEquals(fakeResult, result.getPayload());
        verify(lookupService, times(1)).findBudgetStatusType();
    }


}
