package kz.iitu.springlab.web;

import io.swagger.v3.oas.annotations.Hidden;
import kz.iitu.springlab.catalog.Book;
import kz.iitu.springlab.catalog.BookNotFoundException;
import kz.iitu.springlab.catalog.BookService;
import kz.iitu.springlab.catalog.BookStats;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Hidden
@RestController
@RequestMapping("/api/legacy-books")
public class BookRestController {

    private final BookService service;

    public BookRestController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public List<Book> list(@RequestParam(required = false) String author,
                           @RequestParam(defaultValue = "10") int limit) {
        return service.findAll(author).stream().limit(limit).toList();
    }

    @GetMapping("/stats")
    public BookStats stats() {
        return service.stats();
    }

    @GetMapping("/{id}")
    public Book find(@PathVariable long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book book) {
        Book saved = service.create(book);
        return ResponseEntity
                .created(URI.create("/api/legacy-books/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> replace(@PathVariable long id, @RequestBody Book book) {
        try {
            Book existing = service.findById(id);
            existing.setTitle(book.getTitle());
            existing.setAuthor(book.getAuthor());
            existing.setYear(book.getYear());
            existing.setPrice(book.getPrice());

            Book updated = service.create(existing);
            return ResponseEntity.ok(updated);
        } catch (BookNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (service.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}