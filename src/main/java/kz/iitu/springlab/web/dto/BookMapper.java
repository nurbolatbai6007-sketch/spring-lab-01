package kz.iitu.springlab.web.dto;

import kz.iitu.springlab.catalog.Book;
import kz.iitu.springlab.catalog.Genre;
import kz.iitu.springlab.catalog.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class BookMapper {

    public Book toDomain(BookRequest request) {
        // Клиент передаёт price как BigDecimal, оборачиваем в Money(amount, "USD")
        Money price = request.price() != null
                ? new Money(request.price(), "USD")
                : null;

        return new Book(
                request.title(),
                Genre.NON_FICTION,
                request.year(),
                price
        );
    }

    public BookResponse toResponse(Book book) {
        String authorName = book.getAuthor() != null ? book.getAuthor().getName() : "Unknown";
        BigDecimal priceAmount = book.getPrice() != null ? book.getPrice().getAmount() : null;

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                authorName,
                book.getYear(),
                priceAmount,
                book.getCreatedAt()
        );
    }

    public List<BookResponse> toResponseList(List<Book> books) {
        return books.stream()
                .map(this::toResponse)
                .toList();
    }
}