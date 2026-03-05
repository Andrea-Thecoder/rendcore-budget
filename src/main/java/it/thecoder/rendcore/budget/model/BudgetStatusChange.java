package it.thecoder.rendcore.budget.model;


import io.ebean.annotation.Index;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "budget_status_change")
public class BudgetStatusChange extends  AbstractAuditable {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @Index
    @JoinColumn(name = "request_id",nullable = false)
    BudgetRequest request;

    @ManyToOne
    @JoinColumn(name = "old_status_id", nullable = false)
    @Index
    BudgetStatusType oldStatus;

    @ManyToOne
    @JoinColumn(name = "new_status_id",nullable = false)
    @Index
    BudgetStatusType newStatus;

    @Column(nullable = false)
    @Index
    private UUID performedBy;

    @Column(columnDefinition = "TEXT")
    private String note;



}
