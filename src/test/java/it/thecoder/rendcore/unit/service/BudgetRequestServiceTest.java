package it.thecoder.rendcore.unit.service;

import io.ebean.ExpressionList;
import it.thecoder.rendcore.budget.cache.EmployeeCacheService;
import it.thecoder.rendcore.budget.dto.PagedResultDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.BaseBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.BaseInsertBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.DetailBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.InsertBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.employee.SummaryEmployeeDTO;
import it.thecoder.rendcore.budget.dto.requeststatuschange.DetailRequestStatusChangeDTO;
import it.thecoder.rendcore.budget.dto.requeststatuschange.UpdateStatusBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.search.SearchBudgetRequest;
import it.thecoder.rendcore.budget.exception.ServiceException;
import it.thecoder.rendcore.budget.model.BudgetRequest;
import it.thecoder.rendcore.budget.model.BudgetStatusType;
import it.thecoder.rendcore.budget.security.JWTInspector;
import it.thecoder.rendcore.budget.service.BudgetRequestService;
import it.thecoder.rendcore.budget.service.BudgetStatusTransitionService;
import it.thecoder.rendcore.budget.service.RequestStatusChangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetRequestServiceTest extends AbstractMockServiceTest<BudgetRequest> {

    @Spy
    @InjectMocks
    BudgetRequestService budgetRequestService;

    @Mock
    JWTInspector jwtInspector;

    @Mock
    RequestStatusChangeService requestStatusChangeService;

    @Mock
    EmployeeCacheService employeeCacheService;

    @Mock
    BudgetStatusTransitionService budgetStatusTransitionService;

    private UUID fakeId;

    @BeforeEach
    void setup() {
        fakeId = UUID.randomUUID();
        lenient().when(db.beginTransaction()).thenReturn(transaction);
    }

    @Test
    @Order(1)
    void findBudgetRequestTest() {
        SearchBudgetRequest fakeRequest = mock(SearchBudgetRequest.class);

        baseQuery(BudgetRequest.class);
        when(expressionList.findPagedList()).thenReturn(pagedList);

        PagedResultDTO<BaseBudgetRequestDTO> result = budgetRequestService.findBudgetRequest(fakeRequest);

        assertNotNull(result);
        verify(fakeRequest).filterBuilder(any(ExpressionList.class));
        verify(fakeRequest).pagination(any(ExpressionList.class), anyString());
        verify(expressionList).findPagedList();

    }


    @Test
    @Order(2)
    void getBudgetRequestByIdTest() {

        BudgetRequest fakeBR = mock(BudgetRequest.class);
        List<DetailRequestStatusChangeDTO> fakeDetailRequest = new ArrayList<>();
        SummaryEmployeeDTO fakeSummaryEmployeeDTO = mock(SummaryEmployeeDTO.class);
        getQuery(fakeBR);
        when(requestStatusChangeService.findRequestStatusChangeByRequestId(fakeId)).thenReturn(fakeDetailRequest);
        when(fakeBR.getUserRequestId()).thenReturn(UUID.randomUUID());
        when(fakeBR.getId()).thenReturn(fakeId);
        when(employeeCacheService.getEmployeeSummary(any(UUID.class))).thenReturn(fakeSummaryEmployeeDTO);

        DetailBudgetRequestDTO result = budgetRequestService.getBudgetRequestById(fakeId);

        assertNotNull(result);
        assertEquals(fakeId, result.getId());
        verify(requestStatusChangeService).findRequestStatusChangeByRequestId(fakeId);
        verify(employeeCacheService).getEmployeeSummary(any(UUID.class));

    }

    @Test
    @Order(3)
    void createBudgetRequestTest() {
        InsertBudgetRequestDTO fakeDto = mock(InsertBudgetRequestDTO.class);
        BudgetRequest fakeBR = mock(BudgetRequest.class);

        when(jwtInspector.getUserId()).thenReturn(UUID.randomUUID());
        when(fakeDto.toEntity()).thenReturn(fakeBR);
        when(fakeBR.getId()).thenReturn(fakeId);
        doNothing().when(requestStatusChangeService).createRequestStatusChange(fakeBR, transaction);

        UUID result = budgetRequestService.createBudgetRequest(fakeDto);

        assertNotNull(result);
        verify(fakeBR).insert(transaction);
        verify(requestStatusChangeService).createRequestStatusChange(fakeBR, transaction);
        verify(transaction).commit();

    }

    @Test
    @Order(4)
    void createBudgetRequestInsertFailTest() {
        InsertBudgetRequestDTO fakeDto = mock(InsertBudgetRequestDTO.class);
        BudgetRequest fakeBR = mock(BudgetRequest.class);

        when(jwtInspector.getUserId()).thenReturn(UUID.randomUUID());
        when(fakeDto.toEntity()).thenReturn(fakeBR);

        doThrow(RuntimeException.class)
                .when(fakeBR)
                .insert(transaction);

        assertThrows(ServiceException.class, () -> budgetRequestService.createBudgetRequest(fakeDto));

        verify(transaction, never()).commit();
        verify(transaction).close();

    }

    @Test
    @Order(5)
    void createBudgetRequestStatusChangeFailTest() {
        InsertBudgetRequestDTO fakeDto = mock(InsertBudgetRequestDTO.class);
        BudgetRequest fakeBR = mock(BudgetRequest.class);

        when(jwtInspector.getUserId()).thenReturn(UUID.randomUUID());
        when(fakeDto.toEntity()).thenReturn(fakeBR);

        doThrow(RuntimeException.class)
                .when(requestStatusChangeService).createRequestStatusChange(fakeBR, transaction);

        assertThrows(ServiceException.class, () -> budgetRequestService.createBudgetRequest(fakeDto));

        verify(transaction, never()).commit();
        verify(fakeBR).insert(transaction);
        verify(transaction).close();

    }

    @Test
    @Order(6)
    void updateBudgetRequestTest() {
        BaseInsertBudgetRequestDTO fakeDto = mock(BaseInsertBudgetRequestDTO.class);
        BudgetRequest fakeBR = mock(BudgetRequest.class);

        getQuery(fakeBR);
        doNothing().when(fakeDto).toUpdate(fakeBR);
        when(fakeBR.getId()).thenReturn(fakeId);
        when(fakeBR.getBudgetStatus()).thenReturn(new BudgetStatusType("DRAFT", "draft tester"));

        UUID result = budgetRequestService.updateBudgetRequest(fakeId, fakeDto);

        assertNotNull(result);
        assertEquals(fakeId, result);
        verify(fakeBR).save(transaction);
        verify(transaction).commit();

    }

    @Test
    @Order(7)
    void updateBudgetRequestInvalidStatusTest() {
        BaseInsertBudgetRequestDTO fakeDto = mock(BaseInsertBudgetRequestDTO.class);
        BudgetRequest fakeBR = mock(BudgetRequest.class);

        getQuery(fakeBR);

        when(fakeBR.getBudgetStatus()).thenReturn(new BudgetStatusType("NO-VALID-STATUS", "invalid status!"));


        assertThrows(ServiceException.class, () -> budgetRequestService.updateBudgetRequest(fakeId, fakeDto));

        verify(transaction, never()).commit();

    }

    @Test
    @Order(8)
    void updateBudgetRequestFailSaveTest() {
        BaseInsertBudgetRequestDTO fakeDto = mock(BaseInsertBudgetRequestDTO.class);
        BudgetRequest fakeBR = mock(BudgetRequest.class);

        getQuery(fakeBR);
        doNothing().when(fakeDto).toUpdate(fakeBR);
        when(fakeBR.getBudgetStatus()).thenReturn(new BudgetStatusType("DRAFT", "draft tester"));

        doThrow(RuntimeException.class).when(fakeBR).save(transaction);

        assertThrows(ServiceException.class, () -> budgetRequestService.updateBudgetRequest(fakeId, fakeDto));

        verify(transaction, never()).commit();
        verify(transaction).close();

    }

    @Test
    @Order(9)
    void updateBudgetRequestStatusTest() {
        BudgetRequest fakeBR = mock(BudgetRequest.class);
        UpdateStatusBudgetRequestDTO fakeDto = mock(UpdateStatusBudgetRequestDTO.class);


        getQuery(fakeBR);
        when(fakeDto.getNewBudgetStatusId()).thenReturn("APPROVED");
        when(fakeBR.getId()).thenReturn(fakeId);
        when(fakeBR.getBudgetStatus()).thenReturn(new BudgetStatusType("DRAFT", "draft tester"));

        UUID result = budgetRequestService.updateBudgetRequestStatus(fakeId, fakeDto);

        assertNotNull(result);
        assertEquals(fakeId, result);
        verify(fakeBR).save(transaction);
        verify(transaction).commit();
    }

    @Test
    @Order(9)
    void updateBudgetRequestStatusInvalidNewStatusTest() {
        BudgetRequest fakeBR = mock(BudgetRequest.class);
        UpdateStatusBudgetRequestDTO fakeDto = mock(UpdateStatusBudgetRequestDTO.class);


        getQuery(fakeBR);
        when(fakeDto.getNewBudgetStatusId()).thenReturn("INVALID-STATUS");
        when(fakeBR.getBudgetStatus()).thenReturn(new BudgetStatusType("DRAFT", "draft tester"));

        doThrow(ServiceException.class)
                .when(budgetStatusTransitionService).validate(anyString(), anyString());

        assertThrows(ServiceException.class, () -> budgetRequestService.updateBudgetRequestStatus(fakeId, fakeDto));

        verify(transaction, never()).commit();

    }

    @Test
    @Order(10)
    void deleteBudgetRequestTest() {
        BudgetRequest fakeBR = mock(BudgetRequest.class);
        BudgetStatusType cancelledStatus = mock(BudgetStatusType.class);
        when(db.reference(
                BudgetStatusType.class,
                RequestStatusChangeService.CANCELLED_STATE
        )).thenReturn(cancelledStatus);

        when(fakeBR.getBudgetStatus()).thenReturn(new BudgetStatusType("DRAFT", "draft tester"));
        getQuery(fakeBR);
        budgetRequestService.deleteBudgetRequest(fakeId);

        verify(fakeBR).update(transaction);
        verify(fakeBR).setBudgetStatus(cancelledStatus);
        verify(transaction).commit();
    }

    @Test
    @Order(11)
    void deleteBudgetRequestInvalidInsertStatusTest() {
        BudgetRequest fakeBR = mock(BudgetRequest.class);
        when(fakeBR.getBudgetStatus()).thenReturn(new BudgetStatusType("DRAFT", "draft tester"));
        getQuery(fakeBR);
        doThrow(RuntimeException.class).when(requestStatusChangeService).insertRequestStatusChangeForUpdate(eq(fakeBR),any(BudgetStatusType.class),eq(transaction));
        assertThrows(ServiceException.class, () -> budgetRequestService.deleteBudgetRequest(fakeId));

        verify(fakeBR,never()).update(transaction);
        verify(transaction,never()).commit();
    }

    @Test
    @Order(12)
    void deleteBudgetRequestInvalidStatusTest() {
        BudgetRequest fakeBR = mock(BudgetRequest.class);
        when(fakeBR.getBudgetStatus()).thenReturn(new BudgetStatusType("DRAFT", "draft tester"));
        getQuery(fakeBR);
        doThrow(ServiceException.class).when(budgetStatusTransitionService).validate(anyString(), eq(RequestStatusChangeService.CANCELLED_STATE));
        assertThrows(ServiceException.class, () -> budgetRequestService.deleteBudgetRequest(fakeId));

        verify(fakeBR,never()).update(transaction);
        verify(transaction,never()).commit();
    }

    private void getQuery(BudgetRequest fakeBR) {
        Optional<BudgetRequest> optional = fakeId == null ? Optional.empty() : Optional.of(fakeBR);
        baseQuery(BudgetRequest.class);
        when(expressionList.idEq(any(UUID.class))).thenReturn(expressionList);
        when(expressionList.findOneOrEmpty()).thenReturn(optional);
    }

}
