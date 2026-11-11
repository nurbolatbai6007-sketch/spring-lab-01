package kz.iitu.springlab.catalog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    // 5 derived queries
    List<Book> findByGenre(Genre genre);
    List<Book> findByTitleContainingIgnoreCase(String fragment);
    List<Book> findByYearBetweenOrderByYearDesc(int from, int to);
    List<Book> findTop3ByGenreOrderByYearDesc(Genre genre);
    long countByAuthorName(String name);

    // 2 @Query
    @Query("select b from Book b where b.genre = :genre and b.year > :year order by b.title")
    List<Book> search(@Param("genre") Genre genre, @Param("year") int year);

    @Modifying(clearAutomatically = true)
    @Query("update Book b set b.archived = true where b.year < :year")
    int archiveOlderThan(@Param("year") int year);

    // N+1 fix
    @Query("select b from Book b join fetch b.author")
    List<Book> findAllWithAuthor();

    // === Pagination (Task 4) ===
    Page<Book> findAll(Pageable pageable);
    Page<Book> findByGenre(Genre genre, Pageable pageable);
}