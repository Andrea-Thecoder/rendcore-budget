package it.thecoder.rendcore.budget.model;

import io.ebean.annotation.View;
import it.thecoder.rendcore.budget.model.enumerator.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@View(name = "v_budget_accounting")
public class VBudgetAccounting {

    @Id
    @Column(name = "request_id")
    private UUID budgetRequestId;

    private BigDecimal amount;
    private String currency;
    private String budgetType;
    private String status;
    @Enumerated(EnumType.STRING)
    private TransactionType  transactionType;
    private LocalDateTime createdAt;


}
