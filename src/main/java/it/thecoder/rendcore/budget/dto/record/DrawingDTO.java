package it.thecoder.rendcore.budget.dto.record;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Arrays;
import java.util.Objects;

@Builder
public record DrawingDTO(
        @NotBlank String title,
        @NotNull Sheet sheet,
        @NotNull CoordinatePoint startingCoordinate,
        String[] categoryName,
        @Valid CoordinatePoint startingCell,
        @Valid CoordinatePoint endingCell
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DrawingDTO(
                String title1, Sheet sheet1, CoordinatePoint coordinate, String[] name,
                CoordinatePoint cell, CoordinatePoint endingCell1
        ))) return false;
        return Objects.equals(title, title1) &&
                Objects.equals(sheet, sheet1) &&
                Objects.equals(startingCoordinate, coordinate) &&
                Arrays.equals(categoryName, name) &&
                Objects.equals(startingCell, cell) &&
                Objects.equals(endingCell, endingCell1);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(title, sheet, startingCoordinate, startingCell, endingCell);
        result = 31 * result + Arrays.hashCode(categoryName);
        return result;
    }

    @Override
    public String toString() {
        return "DrawingDTO{" +
                "title=" + title +
                ", sheet=" + sheet +
                ", startingCoordinate=" + startingCoordinate +
                ", categoryName=" + Arrays.toString(categoryName) +
                ", startingCell=" + startingCell +
                ", endingCell=" + endingCell +
                "}";
    }

    public record CoordinatePoint(
            @NotNull @PositiveOrZero int column,
            @NotNull @PositiveOrZero int row
    ) {
    }
}



