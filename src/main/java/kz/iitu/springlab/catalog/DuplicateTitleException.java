package kz.iitu.springlab.catalog;

public class DuplicateTitleException extends RuntimeException {

    private final String title;

    public DuplicateTitleException(String title) {
        super("A book with this title already exists: " + title);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}