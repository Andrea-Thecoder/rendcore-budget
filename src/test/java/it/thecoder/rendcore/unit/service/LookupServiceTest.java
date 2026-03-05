package it.thecoder.rendcore.unit.service;

import io.ebean.Database;
import io.ebean.Query;
import it.thecoder.rendcore.budget.model.BudgetStatusType;
import it.thecoder.rendcore.budget.model.BudgetType;
import it.thecoder.rendcore.budget.service.LookupService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LookupServiceTest {

    @Mock
    private Database db;

    @Mock
    private Query<BudgetType> queryBT;

    @Mock
    private Query<BudgetStatusType> queryBST;


    @Spy
    @InjectMocks
    private LookupService lookupService;

    @Test
    @Order(1)
    void findBudgetTypeTest() {
        when(db.find(BudgetType.class)).thenReturn(queryBT);
        when(queryBT.setLabel(anyString())).thenReturn(queryBT);
        when(queryBT.findList()).thenReturn(List.of());

        List<BudgetType> result = lookupService.findBudgetType();

        assertNotNull(result);
        verify(db).find(BudgetType.class);
        verify(queryBT).setLabel("findBudgetType");
        verify(queryBT).findList();
    }

    @Test
    void findBudgetStatusTypeTest() {
        when(db.find(BudgetStatusType.class)).thenReturn(queryBST);
        when(queryBST.setLabel(anyString())).thenReturn(queryBST);
        when(queryBST.findList()).thenReturn(List.of());

        List<BudgetStatusType> result = lookupService.findBudgetStatusType();

        assertNotNull(result);
        verify(db).find(BudgetStatusType.class);
        verify(queryBST).setLabel("findBudgetType");
        verify(queryBST).findList();
    }


}
