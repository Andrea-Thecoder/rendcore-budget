package it.thecoder.rendcore.budget.model;

import io.ebean.annotation.History;
import io.ebean.annotation.Index;
import it.thecoder.rendcore.budget.model.enumerator.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@History
@Entity
@Table(name = "budget_request")
public class BudgetRequest extends AbstractAuditable {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    @Index
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision =  12, scale = 2)
    @Index
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    @Index
    private UUID userRequestId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Index
    private TransactionType transactionType;

    @ManyToOne
    @JoinColumn(nullable = false,name = "budget_status_id")
    @Index
    private BudgetStatusType budgetStatus;

    @ManyToOne
    @JoinColumn(name = "budget_type_id")
    @Index
    private BudgetType budgetType;




}
