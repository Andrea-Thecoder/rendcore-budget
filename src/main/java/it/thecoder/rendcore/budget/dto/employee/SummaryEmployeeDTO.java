package it.thecoder.rendcore.budget.dto.employee;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SummaryEmployeeDTO {
    private UUID id;
    private String firstname;
    private String lastname;
    private String employeeNumber;

    @JsonCreator
    public SummaryEmployeeDTO(
            @JsonProperty("id")UUID id,
            @JsonProperty("firstname")String firstname,
            @JsonProperty("lastname")String lastname,
            @JsonProperty("employeeNumber")String employeeNumber
    ){
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.employeeNumber = employeeNumber;
    }
}
