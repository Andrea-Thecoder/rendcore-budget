package it.thecoder.rendcore.unit.service;

import it.thecoder.rendcore.budget.cache.EmployeeCacheService;
import it.thecoder.rendcore.budget.dto.requeststatuschange.DetailRequestStatusChangeDTO;
import it.thecoder.rendcore.budget.dto.requeststatuschange.InsertRequestStatusChangeDTO;
import it.thecoder.rendcore.budget.model.BudgetRequest;
import it.thecoder.rendcore.budget.model.BudgetStatusChange;
import it.thecoder.rendcore.budget.model.BudgetStatusType;
import it.thecoder.rendcore.budget.security.JWTInspector;
import it.thecoder.rendcore.budget.service.RequestStatusChangeService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestStatusChangeServiceTest extends AbstractMockServiceTest<BudgetStatusChange> {


    @Spy
    @InjectMocks
    RequestStatusChangeService requestStatusChangeService;

    @Mock
    JWTInspector jwtInspector;

    @Mock
    EmployeeCacheService employeeCacheService;

    @Test
    @Order(1)
    void findRequestStatusChangeByRequestIdTest() {

        UUID id = UUID.randomUUID();
        List<BudgetStatusChange> fakeResult = new ArrayList<>();
        fakeResult.add(mock(BudgetStatusChange.class));
        baseQuery(BudgetStatusChange.class);
        when(expressionList.eq(anyString(), any(UUID.class))).thenReturn(expressionList);
        when(expressionList.findList()).thenReturn(fakeResult);

        List<DetailRequestStatusChangeDTO> result = requestStatusChangeService.findRequestStatusChangeByRequestId(id);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(expressionList).findList();
    }

    @Test
    @Order(2)
    void findRequestStatusChangeByRequestIdTestFail() {

        UUID id = UUID.randomUUID();

        baseQuery(BudgetStatusChange.class);
        when(expressionList.eq(anyString(), any(UUID.class))).thenReturn(expressionList);
        when(expressionList.findList()).thenReturn(List.of());

        List<DetailRequestStatusChangeDTO> result = requestStatusChangeService.findRequestStatusChangeByRequestId(id);

        assertTrue(result.isEmpty());
        verify(expressionList).findList();
    }


    @Test
    @Order(3)
    void insertRequestStatusChangeForUpdateTest() {

        BudgetRequest fakeBR = mock(BudgetRequest.class);
        BudgetStatusType fakeOldStatusType = mock(BudgetStatusType.class);
        fakeOldStatusType.setId("DRAFT");
        fakeOldStatusType.setDescription("draft");

        BudgetStatusType fakeNewStatusType = mock(BudgetStatusType.class);
        fakeNewStatusType.setId("CREATED");
        fakeNewStatusType.setDescription("created");
        fakeBR.setBudgetStatus(fakeNewStatusType);

        BudgetStatusChange fakeBSC = mock(BudgetStatusChange.class);

        when(jwtInspector.getUserId()).thenReturn(UUID.randomUUID());
        try (MockedConstruction<InsertRequestStatusChangeDTO> mocked =
                     mockConstruction(InsertRequestStatusChangeDTO.class, (mock, ctx) -> {
                         when(mock.toEntity()).thenReturn(fakeBSC);
                     })) {

            requestStatusChangeService.insertRequestStatusChangeForUpdate(
                    fakeBR, fakeOldStatusType, "note", transaction);

            verify(fakeBSC).insert(transaction);
        }
    }

    @Test
    @Order(4)
    void insertRequestStatusChangeForUpdateBrNullTest() {

        try (MockedConstruction<InsertRequestStatusChangeDTO> mocked =
                     mockConstruction(InsertRequestStatusChangeDTO.class)) {

            assertThrows(NullPointerException.class, () ->
                    requestStatusChangeService.insertRequestStatusChangeForUpdate(
                            null, mock(BudgetStatusType.class), "note", transaction)
            );
        }
    }

    @Test
    @Order(5)
    void insertRequestStatusChangeForUpdateJwtNullTest() {

        BudgetRequest fakeBR = mock(BudgetRequest.class);
        when(fakeBR.getBudgetStatus()).thenReturn(mock(BudgetStatusType.class));
        when(jwtInspector.getUserId()).thenReturn(null);

        BudgetStatusChange fakeBSC = mock(BudgetStatusChange.class);

        try (MockedConstruction<InsertRequestStatusChangeDTO> mocked =
                     mockConstruction(InsertRequestStatusChangeDTO.class, (mock, ctx) -> {
                         when(mock.toEntity()).thenReturn(fakeBSC);
                     })) {

            requestStatusChangeService.insertRequestStatusChangeForUpdate(
                    fakeBR, mock(BudgetStatusType.class), "note", transaction);

            // il dto riceve null come performedBy
            InsertRequestStatusChangeDTO capturedDto = mocked.constructed().get(0);
            verify(capturedDto).setPerformedBy(null);

            // insert viene chiamato lo stesso
            verify(fakeBSC).insert(transaction);
        }
    }

    @Test
    @Order(6)
    void createRequestStatusChange() {
        BudgetRequest fakeBR = mock(BudgetRequest.class);
        BudgetStatusType fakeStatusType = mock(BudgetStatusType.class);
        fakeStatusType.setId("CANCELLED");
        fakeStatusType.setDescription("cancelled");
        fakeBR.setBudgetStatus(fakeStatusType);

        BudgetStatusChange fakeBSC = mock(BudgetStatusChange.class);
        try (MockedConstruction<InsertRequestStatusChangeDTO> mocked =
                     mockConstruction(InsertRequestStatusChangeDTO.class, (mock, ctx) -> {
                         when(mock.toEntity()).thenReturn(fakeBSC);
                     })) {
            requestStatusChangeService.createRequestStatusChange(fakeBR, transaction);
            verify(fakeBSC).insert(transaction);
        }
    }

    @Test
    @Order(7)
    void createRequestStatusChange_brNull() {

        assertThrows(NullPointerException.class, () ->
                requestStatusChangeService.createRequestStatusChange(null, transaction)
        );
    }
}
