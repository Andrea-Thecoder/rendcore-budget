package it.thecoder.rendcore.integration.factory;

import it.thecoder.rendcore.budget.dto.budgetrequest.BaseInsertBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.budgetrequest.InsertBudgetRequestDTO;
import it.thecoder.rendcore.budget.dto.requeststatuschange.UpdateStatusBudgetRequestDTO;
import it.thecoder.rendcore.budget.model.enumerator.TransactionType;

import java.math.BigDecimal;
import java.util.stream.Stream;

public final class BudgetRequestTestFactory {
    private BudgetRequestTestFactory() {}

    public static InsertBudgetRequestDTO validInsertExpenseDTO() {
        InsertBudgetRequestDTO dto = new InsertBudgetRequestDTO();
        dto.setTitle("Test Budget Request");
        dto.setDescription("Test description");
        dto.setAmount(BigDecimal.valueOf(1000,2));
        dto.setCurrency("EUR");
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setBudgetTypeId("STRUCTURAL");
        return dto;
    }

    public static InsertBudgetRequestDTO validInsertIncomeDTO() {
        InsertBudgetRequestDTO dto = new InsertBudgetRequestDTO();
        dto.setTitle("Test Budget Request");
        dto.setDescription("Test description");
        dto.setAmount(BigDecimal.valueOf(4000,2));
        dto.setCurrency("EUR");
        dto.setTransactionType(TransactionType.INCOME);
        dto.setBudgetTypeId("STRUCTURAL");
        return dto;
    }

    public static BaseInsertBudgetRequestDTO validUpdateDTO() {
        BaseInsertBudgetRequestDTO dto = new BaseInsertBudgetRequestDTO();
        dto.setDescription("Valid description");
        dto.setAmount(BigDecimal.valueOf(100,2));
        dto.setCurrency("EUR");
        return dto;
    }

    public static UpdateStatusBudgetRequestDTO validUpdateStatusDTO() {
        UpdateStatusBudgetRequestDTO dto = new UpdateStatusBudgetRequestDTO();
        dto.setNote("Test notes");
        dto.setNewBudgetStatusId("IN_REVIEW");
        return dto;
    }

    public static Stream<InsertBudgetRequestDTO> invalidInsertDTOs() {

        // Title null
        InsertBudgetRequestDTO nullTitle = validInsertIncomeDTO();
        nullTitle.setTitle(null);

        // Title blank
        InsertBudgetRequestDTO blankTitle = validInsertIncomeDTO();
        blankTitle.setTitle(" ");

        // Transaction null
        InsertBudgetRequestDTO nullTransaction = validInsertExpenseDTO();
        nullTransaction.setTransactionType(null);

        // BudgetType null
        InsertBudgetRequestDTO nullBudgetType = validInsertExpenseDTO();
        nullBudgetType.setBudgetTypeId(null);

        return Stream.of(
                nullTitle,
                blankTitle,
                nullTransaction,
                nullBudgetType
        );
    }

    public static Stream<BaseInsertBudgetRequestDTO> invalidUpdateDTOs() {

        // Description null
        BaseInsertBudgetRequestDTO nullDescription = validUpdateDTO();
        nullDescription.setDescription(null);

        // Description blank
        BaseInsertBudgetRequestDTO blankDescription = validUpdateDTO();
        blankDescription.setDescription(" ");

        // Amount null
        BaseInsertBudgetRequestDTO nullAmount = validUpdateDTO();
        nullAmount.setAmount(null);

        // Amount zero (non valido perché DecimalMin > 0)
        BaseInsertBudgetRequestDTO zeroAmount = validUpdateDTO();
        zeroAmount.setAmount(BigDecimal.ZERO);

        // Amount negativo
        BaseInsertBudgetRequestDTO negativeAmount = validUpdateDTO();
        negativeAmount.setAmount(BigDecimal.valueOf(-10));

        // Currency null
        BaseInsertBudgetRequestDTO nullCurrency = validUpdateDTO();
        nullCurrency.setCurrency(null);

        // Currency blank
        BaseInsertBudgetRequestDTO blankCurrency = validUpdateDTO();
        blankCurrency.setCurrency(" ");

        return Stream.of(
                nullDescription,
                blankDescription,
                nullAmount,
                zeroAmount,
                negativeAmount,
                nullCurrency,
                blankCurrency
        );
    }

    public static Stream<UpdateStatusBudgetRequestDTO> invalidUpdateStatusDTOs() {
        return Stream.of(
                "CREATED",
                "DRAFT",
                "APPROVED",
                "REJECTED"
        ).map(status -> {
            UpdateStatusBudgetRequestDTO dto = new UpdateStatusBudgetRequestDTO();
            dto.setNewBudgetStatusId(status);
            dto.setNote("Invalid transition test");
            return dto;
        });
    }
}
