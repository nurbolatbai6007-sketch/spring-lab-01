package kz.iitu.springlab.catalog;

public class AuthorNotFoundException extends RuntimeException {

    public AuthorNotFoundException(Long id) {
        super("Author not found: " + id);
    }
}