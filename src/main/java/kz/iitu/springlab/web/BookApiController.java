package kz.iitu.springlab.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kz.iitu.springlab.catalog.*;
import kz.iitu.springlab.web.dto.BookMapper;
import kz.iitu.springlab.web.dto.BookRequest;
import kz.iitu.springlab.web.dto.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Books", description = "The catalogue of books")
@RestController
@RequestMapping("/api/books")
public class BookApiController {

    private final BookService service;
    private final BookMapper mapper;
    private final AuthorRepository authorRepository;

    public BookApiController(BookService service, BookMapper mapper, AuthorRepository authorRepository) {
        this.service = service;
        this.mapper = mapper;
        this.authorRepository = authorRepository;
    }

    // === PAGED LIST (Task 4) ===
    @GetMapping
    public Page<BookResponse> list(
            @RequestParam(required = false) Genre genre,
            @PageableDefault(size = 5, sort = "title") Pageable pageable) {
        Page<Book> books = (genre != null)
                ? service.findByGenre(genre, pageable)
                : service.findAll(pageable);
        return books.map(mapper::toResponse);
    }

    @Operation(summary = "Find a book by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The book was found"),
            @ApiResponse(responseCode = "404", description = "No book with this id")
    })
    @GetMapping("/{id}")
    public BookResponse find(
            @Parameter(description = "Identifier of the book", example = "42")
            @PathVariable long id) {
        Book book = service.findById(id);
        return mapper.toResponse(book);
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request) {
        Book book = mapper.toDomain(request);
        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() -> new AuthorNotFoundException(request.authorId()));
        book.setAuthor(author);

        Book saved = service.create(book);
        return ResponseEntity
                .created(URI.create("/api/books/" + saved.getId()))
                .body(mapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    public BookResponse update(@PathVariable long id, @Valid @RequestBody BookRequest request) {
        Book existing = service.findById(id);
        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() -> new AuthorNotFoundException(request.authorId()));
        existing.setTitle(request.title());
        existing.setAuthor(author);
        existing.setYear(request.year());
        existing.setPrice(request.price());

        Book updated = service.create(existing);
        return mapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.findById(id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}