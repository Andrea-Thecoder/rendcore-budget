    package it.thecoder.rendcore.integration;


    import io.quarkus.test.common.http.TestHTTPEndpoint;
    import io.quarkus.test.junit.QuarkusTest;
    import io.quarkus.test.security.TestSecurity;
    import io.restassured.response.Response;
    import it.thecoder.rendcore.budget.api.DocumentResource;
    import lombok.extern.slf4j.Slf4j;
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Order;
    import org.junit.jupiter.api.Test;

    import static io.restassured.RestAssured.given;
    import static org.junit.jupiter.api.Assertions.*;

    @QuarkusTest
    @TestHTTPEndpoint(DocumentResource.class)
    @Slf4j
    @TestSecurity(user = "test")
    @DisplayName("DocumentResource - Integration Tests")
    class DocumentResourceIT extends AbstractBaseIT {


        @Test
        @Order(1)
        void generateCSVTest() {
            BudgetRequestResourceIT.setupForDocumentTest();
            Response response = given()
                    .when()
                    .get()
                    .then()
                    .extract()
                    .response();

            assertEquals(200, response.getStatusCode(), "Expected status code 200");
            assertNotNull(response.getBody());
            assertEquals("text/csv", response.getContentType().split(";")[0].trim());
        }

        @Test
        @Order(2)
        void generateCSVEmptyTest() {
            Response response = given()
                    .when()
                    .get()
                    .then()
                    .extract()
                    .response();
            assertEquals(204, response.getStatusCode(), "Expected status code 204");
        }

        @Test
        @Order(3)
        void generateCSVWithAllDataEmptyTest() {
            Response response = given()
                    .when()
                    .get("/all-data")
                    .then()
                    .extract()
                    .response();

            assertEquals(204, response.getStatusCode(), "Expected status code 204");
        }

        @Test
        @Order(4)
        void generateCSVWithAllDataTest() {
            BudgetRequestResourceIT.setupForDocumentTest();

            Response response = given()
                    .when()
                    .get("/all-data")
                    .then()
                    .extract()
                    .response();

            assertEquals(200, response.getStatusCode(), "Expected status code 200");
            assertNotNull(response.getBody());
            assertEquals("text/csv", response.getContentType().split(";")[0].trim());
        }

        @Test
        @Order(5)
        void generateXLSXAccountingEmptyTest() {
            Response response = given()
                    .when()
                    .get("/xlsx-accounting")
                    .then()
                    .extract()
                    .response();

            assertEquals(204, response.getStatusCode(), "Expected status code 204");
        }

        @Test
        @Order(6)
        void generateXLSXAccountingTest() {
            BudgetRequestResourceIT.setupForDocumentTest();

            Response response = given()
                    .when()
                    .get("/xlsx-accounting")
                    .then()
                    .extract()
                    .response();

            assertEquals(200, response.getStatusCode(), "Expected status code 200");
            assertNotNull(response.getBody());
            assertEquals(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    response.getContentType().split(";")[0].trim()
            );
            assertNotNull(response.getHeader("Content-Disposition"));
            assertTrue(response.getHeader("Content-Disposition").contains("attachment"));
            assertTrue(response.getHeader("Content-Disposition").contains("budget-report"));
        }

        @Test
        @Order(7)
        void generateCSVWithFilterMatchingDataTest() {
            BudgetRequestResourceIT.setupForDocumentTest();

            Response response = given()
                    .when()
                    .queryParam("start_date", "2026-01")
                    .queryParam("end_date", "2026-12")
                    .get()
                    .then()
                    .extract()
                    .response();

            assertEquals(200, response.getStatusCode(), "Expected status code 200");
            assertEquals("text/csv", response.getContentType().split(";")[0].trim());
        }

        @Test
        @Order(8)
        void generateCSVWithFilterNoDataTest() {
            BudgetRequestResourceIT.setupForDocumentTest();

            Response response = given()
                    .when()
                    .queryParam("start_date", "2020-01")
                    .queryParam("end_date", "2020-12")
                    .get()
                    .then()
                    .extract()
                    .response();

            assertEquals(204, response.getStatusCode(), "Expected status code 204");
        }

    }
