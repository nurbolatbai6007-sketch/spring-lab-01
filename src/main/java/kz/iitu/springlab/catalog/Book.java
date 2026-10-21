package kz.iitu.springlab.catalog;

import java.math.BigDecimal;
import java.time.Instant;

public record Book(Long id, String title, String author, int year, BigDecimal price, Instant createdAt) {
}