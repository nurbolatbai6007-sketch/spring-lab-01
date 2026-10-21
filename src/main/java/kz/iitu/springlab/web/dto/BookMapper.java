package kz.iitu.springlab.web.dto;

import kz.iitu.springlab.catalog.Book;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class BookMapper {

    // Из запроса клиента в сущность (id и дата ставятся сервером)
    public Book toDomain(BookRequest request) {
        return new Book(
                null,
                request.title(),
                request.author(),
                request.year(),
                request.price(),
                Instant.now()
        );
    }

    // Из сущности в ответ клиенту
    public BookResponse toResponse(Book book) {
        return new BookResponse(
                book.id(),
                book.title(),
                book.author(),
                book.year(),
                book.price(),
                book.createdAt()
        );
    }

    // Из списка сущностей в список ответов
    public List<BookResponse> toResponseList(List<Book> books) {
        return books.stream()
                .map(this::toResponse)
                .toList();
    }
}