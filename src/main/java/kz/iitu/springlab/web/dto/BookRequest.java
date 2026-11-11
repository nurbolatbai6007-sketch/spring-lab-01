package kz.iitu.springlab.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record BookRequest(
        @NotBlank(message = "{book.title.required}")
        @Size(max = 120, message = "{book.title.size}")
        String title,

        @NotNull(message = "{book.author.required}")
        Long authorId,

        @Min(value = 1450, message = "{book.year.range}")
        @Max(value = 2100, message = "{book.year.range}")
        int year,

        @NotNull(message = "{book.price.required}")
        @DecimalMin(value = "0.0", inclusive = false, message = "{book.price.min}")
        @Digits(integer = 6, fraction = 2, message = "{book.price.digits}")
        BigDecimal price
) {}