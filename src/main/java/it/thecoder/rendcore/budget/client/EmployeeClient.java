package it.thecoder.rendcore.budget.client;

import it.thecoder.rendcore.budget.dto.employee.SummaryEmployeeDTO;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.UUID;

@RegisterRestClient(configKey = "rendcore-employee")
@Path("integration/employee")
public interface EmployeeClient {

        @GET
        @Path("{id}")
        @Consumes("application/json")
        @Produces("application/json")
        SummaryEmployeeDTO getEmployeeProfileById(
                        @PathParam("id") UUID id);
}
