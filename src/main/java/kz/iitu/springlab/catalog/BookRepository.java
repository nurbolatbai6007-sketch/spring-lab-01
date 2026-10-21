package kz.iitu.springlab.catalog;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class BookRepository {

    private final Map<Long, Book> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public BookRepository() {
        save(new Book(null, "Effective Java", "Joshua Bloch", 2018, new BigDecimal("45.99"), Instant.now()));
        save(new Book(null, "Clean Code", "Robert Martin", 2008, new BigDecimal("39.50"), Instant.now()));
        save(new Book(null, "Spring in Action", "Craig Walls", 2022, new BigDecimal("49.00"), Instant.now()));
    }

    public List<Book> findAll() {
        return List.copyOf(storage.values());
    }

    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Book save(Book book) {
        long id = book.id() != null ? book.id() : sequence.incrementAndGet();
        Book stored = new Book(id, book.title(), book.author(), book.year(), book.price(), book.createdAt());
        storage.put(id, stored);
        return stored;
    }

    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }
}