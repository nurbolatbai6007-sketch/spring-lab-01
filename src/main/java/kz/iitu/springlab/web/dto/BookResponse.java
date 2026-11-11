package kz.iitu.springlab.web.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record BookResponse(
        Long id,
        String title,
        String author,
        int year,
        BigDecimal price,
        Instant createdAt
) {}