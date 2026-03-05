package it.thecoder.rendcore.integration;

import io.ebean.DB;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.thecoder.rendcore.budget.api.BudgetRequestResource;
import it.thecoder.rendcore.budget.dto.PagedResultDTO;
import it.thecoder.rendcore.budget.dto.SimpleResultDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.BaseInsertBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.DetailBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.InsertBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.requeststatuschange.UpdateStatusBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.search.SearchBudgetRequest;
import it.thecoder.rendcore.budget.exception.ExceptionResponse;
import it.thecoder.rendcore.budget.model.BudgetRequest;
import it.thecoder.rendcore.budget.model.BudgetStatusChange;
import it.thecoder.rendcore.budget.model.BudgetStatusType;
import it.thecoder.rendcore.budget.model.BudgetType;
import it.thecoder.rendcore.budget.model.enumerator.TransactionType;
import it.thecoder.rendcore.integration.factory.BudgetRequestTestFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(BudgetRequestResource.class)
@Slf4j
@TestSecurity(user = "test")
@DisplayName("BudgetRequestResource - Integration Tests")

class BudgetRequestResourceIT extends AbstractBaseIT {



    @Test
    @Order(1)
    void findBudgetRequest_returnsPagedResult() {
        SearchBudgetRequest request = new SearchBudgetRequest();
        request.setPage(2);
        request.setSize(20);
        Response response = given()
                .when()
                .queryParam("page", request.getPage())
                .queryParam("size", request.getSize())
                .get()
                .then()
                .extract()
                .response();

        assertEquals(200, response.getStatusCode(), "Expected status code 200");

        PagedResultDTO<DetailBudgetRequestDTO> result = response.as(new TypeRef<>() {});

        assertNotNull(result);
        assertNotNull(result.getList());
        assertEquals(result.getPage(), request.getPage());
        assertEquals(result.getPageSize(), request.getSize());
        assertTrue(result.getTotalRows() >= 0);
    }

    @Test
    @Order(2)
    void createBudgetRequestTest(){
        InsertBudgetRequestDTO dto = BudgetRequestTestFactory.validInsertExpenseDTO();

        Response response = given()
                .when()
                .body(dto)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .extract()
                .response();

        assertEquals(200, response.getStatusCode(), "Expected status code 200");

        SimpleResultDTO<UUID> result = response.as(new TypeRef<>() {});
        assertNotNull(result);
        assertNotNull(result.getPayload());

        BudgetRequest saved = DB.find(BudgetRequest.class)
                .where()
                .idEq(result.getPayload())
                .findOne();

        assertNotNull(saved);
        assertEquals(dto.getAmount(), saved.getAmount());
        assertEquals(dto.getDescription(), saved.getDescription());

    }

    @ParameterizedTest
    @MethodSource("it.thecoder.rendcore.integration.factory.BudgetRequestTestFactory#invalidInsertDTOs")
    @Order(3)
    void createBudgetRequestInvalidTest(InsertBudgetRequestDTO dto) {

        Response response = given()
                .when()
                .body(dto)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .extract()
                .response();

        assertNotEquals(200, response.getStatusCode(), "Expected status code is not 200");

        ExceptionResponse error = response.as(ExceptionResponse.class);

        assertNotNull(error);
        assertEquals(400, error.getStatus());
        assertNotNull(error.getViolations());
        assertFalse(error.getViolations().isEmpty());

    }



    @Test
    @Order(4)
    void getBudgetRequestTest(){
        setEmployee();
        UUID id = createBudgetRequestForInternalTest();

        Response response = given()
                .when()
                .get("/"+id.toString())
                .then()
                .extract()
                .response();
        assertEquals(200, response.getStatusCode(), "Expected status code 200");

        DetailBudgetRequestDTO result =  response.as(new TypeRef<>() {});

        assertNotNull(result);
        assertEquals(result.getId(),id);
        assertNotNull(result.getTitle());
        assertNotNull(result.getAmount());
        assertNotNull(result.getDescription());

    }


    @Test
    @Order(5)
    void updateBudgetRequestTest(){
        setEmployee();
        UUID id = createBudgetRequestForInternalTest();
        BaseInsertBudgetRequestDTO dto = BudgetRequestTestFactory.validUpdateDTO();
        Response response = given()
                .when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/"+id.toString())
                .then()
                .extract()
                .response();

        assertEquals(200, response.getStatusCode(), "Expected status code 200");

        SimpleResultDTO<UUID> result = response.as(new TypeRef<>() {});
        assertNotNull(result);
        assertNotNull(result.getPayload());


        BudgetRequest saved = DB.find(BudgetRequest.class)
                .where()
                .idEq(result.getPayload())
                .findOne();

        assertNotNull(saved);
        assertEquals(dto.getAmount(), saved.getAmount());
        assertEquals(dto.getDescription(), saved.getDescription());

    }

    @ParameterizedTest
    @MethodSource("it.thecoder.rendcore.integration.factory.BudgetRequestTestFactory#invalidUpdateDTOs")
    @Order(6)
    void updateBudgetRequestInvalidTest(BaseInsertBudgetRequestDTO dto){
        setEmployee();
        UUID id = createBudgetRequestForInternalTest();
        Response response = given()
                .when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/"+id.toString())
                .then()
                .extract()
                .response();

        assertNotEquals(200, response.getStatusCode(), "Expected status code is not 200");

        ExceptionResponse error = response.as(ExceptionResponse.class);

        assertNotNull(error);
        assertEquals(400, error.getStatus());
        assertNotNull(error.getViolations());
        assertFalse(error.getViolations().isEmpty());

    }

    @Test
    @Order(7)
    void updateStatusBudgetRequestTest(){
        setEmployee();
        UUID id = createBudgetRequestForInternalTest();
        UpdateStatusBudgetRequestDTO dto = BudgetRequestTestFactory.validUpdateStatusDTO();
        Response response = given()
                .when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/"+id.toString()+"/status")
                .then()
                .extract()
                .response();

        assertEquals(200, response.getStatusCode(), "Expected status code 200");

        SimpleResultDTO<UUID> result = response.as(new TypeRef<>() {});
        assertNotNull(result);
        assertNotNull(result.getPayload());

        BudgetRequest saved = DB.find(BudgetRequest.class)
                .where()
                .idEq(result.getPayload())
                .findOne();

        assertNotNull(saved);
        assertEquals(dto.getNewBudgetStatusId(), saved.getBudgetStatus().getId());

    }

    @ParameterizedTest
    @MethodSource("it.thecoder.rendcore.integration.factory.BudgetRequestTestFactory#invalidUpdateStatusDTOs")
    @Order(8)
    void updateStatusBudgetRequestInvalidTest(UpdateStatusBudgetRequestDTO dto){
        setEmployee();
        UUID id = createBudgetRequestForInternalTest();
        Response response = given()
                .when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/"+id.toString()+"/status")
                .then()
                .extract()
                .response();

        assertNotEquals(200, response.getStatusCode(), "Expected status code is not 200");

        ExceptionResponse error = response.as(ExceptionResponse.class);

        assertNotNull(error);
        assertEquals(400, error.getStatus());
        assertNotNull(error.getViolations());
        assertFalse(error.getViolations().isEmpty());

    }


    @Test
    @Order(9)
    void deleteBudgetRequestTest(){
        setEmployee();
        UUID id = createBudgetRequestForInternalTest();

        Response response = given()
                .when()
                .delete("/"+id.toString())
                .then()
                .extract()
                .response();

        assertEquals(200, response.getStatusCode(), "Expected status code 200");

        SimpleResultDTO<Void> result = response.as(new TypeRef<>() {});
        assertNotNull(result);
        assertNotNull(result.getMessage());
        assertEquals("Budget Request deleted successfully", result.getMessage());

        BudgetRequest saved = DB.find(BudgetRequest.class)
                .where()
                .idEq(id)
                .findOne();

        assertNotNull(saved);
        assertEquals("CANCELLED",saved.getBudgetStatus().getId());
    }



    private UUID createBudgetRequestForInternalTest(){
        InsertBudgetRequestDTO dto = BudgetRequestTestFactory.validInsertExpenseDTO();

        Response response = given()
                .when()
                .body(dto)
                .contentType(ContentType.JSON)
                .post()
                .then()
                .extract()
                .response();

        assertEquals(200, response.getStatusCode(), "Expected status code 200");

        SimpleResultDTO<UUID> result = response.as(new TypeRef<>() {});
        return result.getPayload();

    }

    public static void setupForDocumentTest(){
        BudgetRequest br = new BudgetRequest();
        br.setDescription("Test accounting");
        br.setAmount(new BigDecimal("500.00"));
        br.setCurrency("EUR");
        br.setTitle("Test");
        br.setUserRequestId(UUID.randomUUID());
        br.setBudgetStatus(DB.find(BudgetStatusType.class, "APPROVED"));
        br.setBudgetType(DB.find(BudgetType.class, "STRUCTURAL"));
        br.setTransactionType(TransactionType.EXPENSE);
        DB.save(br);

        BudgetStatusChange sc = new BudgetStatusChange();
        sc.setRequest(br);
        sc.setOldStatus(DB.find(BudgetStatusType.class, "IN_REVIEW"));
        sc.setNewStatus(DB.find(BudgetStatusType.class, "APPROVED"));
        sc.setPerformedBy(UUID.randomUUID());
        sc.setNote("Test setup");
        DB.save(sc);
    }


}
