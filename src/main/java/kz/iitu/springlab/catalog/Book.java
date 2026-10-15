package kz.iitu.springlab.catalog;

import java.math.BigDecimal;

public record Book(Long id, String title, String author, int year, BigDecimal price) {
}