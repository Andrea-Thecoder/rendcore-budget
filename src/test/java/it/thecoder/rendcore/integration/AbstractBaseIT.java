package it.thecoder.rendcore.integration;

import io.ebean.DB;
import io.quarkus.test.InjectMock;
import it.thecoder.rendcore.budget.client.EmployeeClient;
import it.thecoder.rendcore.budget.dto.employee.SummaryEmployeeDTO;
import it.thecoder.rendcore.budget.security.JWTInspector;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.util.UUID;

import static org.mockito.Mockito.when;

public abstract class AbstractBaseIT {

    @InjectMock
    @RestClient
    EmployeeClient employeeClient;

    @InjectMock
    JWTInspector jwt;


    @BeforeEach
    void setUp() {
        when(jwt.getUserId()).thenReturn(UUID.randomUUID());
        DB.sqlUpdate("DELETE FROM budget_status_change").execute();
        DB.sqlUpdate("DELETE FROM budget_request").execute();
    }


    protected void setEmployee(){
        SummaryEmployeeDTO fakeEmployee = new SummaryEmployeeDTO();
        fakeEmployee.setId(UUID.randomUUID());
        fakeEmployee.setFirstname("fakeFirstname");
        fakeEmployee.setLastname("fakeLastname");
        fakeEmployee.setEmployeeNumber("21383");
        when(employeeClient.getEmployeeProfileById(Mockito.any()))
                .thenReturn(fakeEmployee);
    }


}
