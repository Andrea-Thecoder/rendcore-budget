package it.thecoder.rendcore.budget.cache;

import io.quarkus.cache.CacheResult;
import it.thecoder.rendcore.budget.client.EmployeeClient;
import it.thecoder.rendcore.budget.dto.employee.SummaryEmployeeDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.UUID;

@ApplicationScoped

public class EmployeeCacheService {

    @Inject
    @RestClient
    EmployeeClient  employeeClient;

    /**
     * Returns employee info, cached globally across the application.
     * First call → hits REST service.
     * Following calls → served from in-memory cache.
     */
    @CacheResult(cacheName = "employee-summary-cache")
    public SummaryEmployeeDTO getEmployeeSummary(UUID id) {
        return employeeClient.getEmployeeProfileById(id);
    }
}
