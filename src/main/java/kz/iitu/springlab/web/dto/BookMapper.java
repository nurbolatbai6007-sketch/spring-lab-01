package kz.iitu.springlab.web.dto;

import kz.iitu.springlab.catalog.Book;
import kz.iitu.springlab.catalog.Genre;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookMapper {

    public Book toDomain(BookRequest request) {
        return new Book(
                request.title(),
                Genre.NON_FICTION, // заглушка, пока клиент не передаёт genre
                request.year(),
                request.price()
        );
    }

    public BookResponse toResponse(Book book) {
        String authorName = book.getAuthor() != null ? book.getAuthor().getName() : "Unknown";
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                authorName,
                book.getYear(),
                book.getPrice(),
                book.getCreatedAt()
        );
    }

    public List<BookResponse> toResponseList(List<Book> books) {
        return books.stream()
                .map(this::toResponse)
                .toList();
    }
}