package kz.iitu.springlab.config;

import kz.iitu.springlab.catalog.Book;
import kz.iitu.springlab.catalog.BookRepository;
import kz.iitu.springlab.catalog.Genre;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class QueryDemoRunner implements CommandLineRunner {

    private final BookRepository bookRepository;

    public QueryDemoRunner(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (bookRepository.count() == 0) {
            System.out.println("QueryDemoRunner: no data, skipping.");
            return;
        }

        System.out.println("\n========== DERIVED QUERY DEMOS ==========\n");

        System.out.println(">>> findByGenre(NON_FICTION):");
        List<Book> byGenre = bookRepository.findByGenre(Genre.NON_FICTION);
        System.out.println("    rows = " + byGenre.size());

        System.out.println(">>> findByTitleContainingIgnoreCase(\"clean\"):");
        List<Book> byTitle = bookRepository.findByTitleContainingIgnoreCase("clean");
        System.out.println("    rows = " + byTitle.size());

        System.out.println(">>> findByYearBetweenOrderByYearDesc(2000, 2020):");
        List<Book> byYear = bookRepository.findByYearBetweenOrderByYearDesc(2000, 2020);
        System.out.println("    rows = " + byYear.size());

        System.out.println(">>> findTop3ByGenreOrderByYearDesc(NON_FICTION):");
        List<Book> top3 = bookRepository.findTop3ByGenreOrderByYearDesc(Genre.NON_FICTION);
        System.out.println("    rows = " + top3.size());

        System.out.println(">>> countByAuthorName(\"Joshua Bloch\"):");
        long count = bookRepository.countByAuthorName("Joshua Bloch");
        System.out.println("    count = " + count);

        System.out.println("\n========== JPQL @Query DEMOS ==========\n");

        System.out.println(">>> search(NON_FICTION, 2010):");
        List<Book> searchResults = bookRepository.search(Genre.NON_FICTION, 2010);
        System.out.println("    rows = " + searchResults.size());

        System.out.println(">>> archiveOlderThan(2010):");
        int archived = bookRepository.archiveOlderThan(2010);
        System.out.println("    updated rows = " + archived);

        System.out.println("\n========== N+1 EXERCISE (before) ==========\n");
        System.out.println(">>> findAll() and print author names:");
        List<Book> allBooks = bookRepository.findAll();
        for (Book b : allBooks) {
            System.out.println("    " + b.getTitle() + " by " + b.getAuthor().getName());
        }
        System.out.println("    (count the SQL SELECT statements above — should be 1 + N)");

        System.out.println("\n========== N+1 EXERCISE (after JOIN FETCH) ==========\n");
        System.out.println(">>> findAllWithAuthor() and print author names:");
        List<Book> allBooksFetch = bookRepository.findAllWithAuthor();
        for (Book b : allBooksFetch) {
            System.out.println("    " + b.getTitle() + " by " + b.getAuthor().getName());
        }
        System.out.println("    (count the SQL SELECT statements above — should be 1)");
    }
}