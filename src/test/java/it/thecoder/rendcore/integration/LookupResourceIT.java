package it.thecoder.rendcore.integration;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import it.thecoder.rendcore.budget.api.LookupResource;
import it.thecoder.rendcore.budget.dto.SimpleResultDTO;
import it.thecoder.rendcore.budget.model.BudgetStatusType;
import it.thecoder.rendcore.budget.model.BudgetType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(LookupResource.class)
@Slf4j
@TestSecurity(user = "test")
@DisplayName("LookupResource - Integration Tests")

class LookupResourceIT extends AbstractBaseIT {

    @Test
    @Order(1)
    void findBudgetTypeTest(){

        Response response = given()
                .when()
                .get("/budget-type")
                .then()
                .extract()
                .response();

        assertEquals(200, response.getStatusCode(), "Expected status code 200");

        SimpleResultDTO<List<BudgetType>> result =  response.as(new TypeRef<>() {});

        assertNotNull(result);
        assertNotNull(result.getPayload());
        assertFalse(result.getPayload().isEmpty());

    }

    @Test
    @Order(2)
    void findBudgetStatusTest(){

        Response response = given()
                .when()
                .get("/budget-status")
                .then()
                .extract()
                .response();

        assertEquals(200, response.getStatusCode(), "Expected status code 200");

        SimpleResultDTO<List<BudgetStatusType>> result =  response.as(new TypeRef<>() {});

        assertNotNull(result);
        assertNotNull(result.getPayload());
        assertFalse(result.getPayload().isEmpty());

    }
}
