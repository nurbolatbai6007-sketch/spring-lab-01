package kz.iitu.springlab.catalog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> findAll(String author) {
        List<Book> all = repository.findAll();
        if (author == null || author.isBlank()) {
            return all;
        }
        return all.stream()
                .filter(b -> b.getAuthor() != null
                        && b.getAuthor().getName() != null
                        && b.getAuthor().getName().toLowerCase().contains(author.toLowerCase()))
                .toList();
    }

    public Book findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public Book create(Book book) {
        if (book.getId() == null) {
            boolean exists = repository.findAll().stream()
                    .anyMatch(b -> b.getTitle().equalsIgnoreCase(book.getTitle())
                            && b.getAuthor() != null && book.getAuthor() != null
                            && b.getAuthor().getName() != null && book.getAuthor().getName() != null
                            && b.getAuthor().getName().equalsIgnoreCase(book.getAuthor().getName()));
            if (exists) {
                throw new DuplicateTitleException(book.getTitle());
            }
        }
        return repository.save(book);
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }

    public BookStats stats() {
        List<Book> all = repository.findAll();
        if (all.isEmpty()) {
            return new BookStats(0, 0, 0);
        }
        int earliest = all.stream().mapToInt(Book::getYear).min().orElse(0);
        int latest   = all.stream().mapToInt(Book::getYear).max().orElse(0);
        return new BookStats(all.size(), earliest, latest);
    }

    // === Pagination (Task 4) ===
    public Page<Book> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Book> findByGenre(Genre genre, Pageable pageable) {
        return repository.findByGenre(genre, pageable);
    }
}