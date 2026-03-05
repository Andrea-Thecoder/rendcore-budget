package it.thecoder.rendcore.budget.model;


import io.ebean.annotation.Index;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "budget_status_transition")
@Index(name = "from_to_status", columnNames = {"from_status_id","to_status_id"})
public class BudgetStatusTransition {

    @Id
    @GeneratedValue
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "from_status_id", nullable = false)
    private BudgetStatusType fromStatus;

    @ManyToOne(optional = false)
    @JoinColumn(name = "to_status_id", nullable = false)
    private BudgetStatusType toStatus;


}
