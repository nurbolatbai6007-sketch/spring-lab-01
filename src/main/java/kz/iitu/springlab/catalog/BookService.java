package kz.iitu.springlab.catalog;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> findAll(String author) {
        if (author == null || author.isBlank()) {
            return repository.findAll();
        }
        return repository.findAll().stream()
                .filter(b -> b.author().toLowerCase().contains(author.toLowerCase()))
                .toList();
    }

    public Book findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public Book create(Book book) {
        return repository.save(book);
    }

    public boolean delete(Long id) {
        return repository.deleteById(id);
    }

    public BookStats stats() {
        List<Book> all = repository.findAll();
        if (all.isEmpty()) {
            return new BookStats(0, 0, 0);
        }
        int earliest = all.stream().mapToInt(Book::year).min().orElse(0);
        int latest   = all.stream().mapToInt(Book::year).max().orElse(0);
        return new BookStats(all.size(), earliest, latest);
    }
}