package kz.iitu.springlab.config;

import kz.iitu.springlab.catalog.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final TagRepository tagRepository;

    public DataSeeder(AuthorRepository authorRepository,
                      BookRepository bookRepository,
                      TagRepository tagRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public void run(String... args) {
        if (bookRepository.count() > 0) {
            System.out.println("DataSeeder: database already has data, skipping.");
            return;
        }

        Author bloch = authorRepository.save(new Author("Joshua Bloch", "USA"));
        Author martin = authorRepository.save(new Author("Robert Martin", "USA"));
        Author walls = authorRepository.save(new Author("Craig Walls", "USA"));

        Tag java = tagRepository.save(new Tag("java"));
        Tag spring = tagRepository.save(new Tag("spring"));
        Tag bestPractice = tagRepository.save(new Tag("best-practice"));
        Tag classic = tagRepository.save(new Tag("classic"));

        saveBook("Effective Java", Genre.NON_FICTION, 2018, "45.99", bloch, Set.of(java, bestPractice, classic));
        saveBook("Java Puzzlers", Genre.SCIENCE, 2005, "35.00", bloch, Set.of(java));
        saveBook("Java Concurrency in Practice", Genre.SCIENCE, 2006, "55.00", bloch, Set.of(java, bestPractice));

        saveBook("Clean Code", Genre.NON_FICTION, 2008, "39.50", martin, Set.of(bestPractice, classic));
        saveBook("Clean Architecture", Genre.NON_FICTION, 2017, "42.00", martin, Set.of(bestPractice));
        saveBook("The Clean Coder", Genre.NON_FICTION, 2011, "38.00", martin, Set.of(bestPractice, classic));

        saveBook("Spring in Action", Genre.FICTION, 2022, "49.00", walls, Set.of(spring, java));
        saveBook("Spring Boot in Action", Genre.FICTION, 2015, "44.00", walls, Set.of(spring));

        System.out.println("DataSeeder: inserted 3 authors, 8 books, 4 tags.");
    }

    private void saveBook(String title, Genre genre, int year, String amount, Author author, Set<Tag> tags) {
        Book b = new Book(title, genre, year, new Money(new BigDecimal(amount), "USD"));
        b.setAuthor(author);
        b.setTags(tags);
        bookRepository.save(b);
    }
}