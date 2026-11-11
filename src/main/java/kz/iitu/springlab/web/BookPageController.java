package kz.iitu.springlab.web;

import jakarta.validation.Valid;
import kz.iitu.springlab.catalog.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/books")
public class BookPageController {

    private final BookService service;
    private final AuthorRepository authorRepository;

    public BookPageController(BookService service, AuthorRepository authorRepository) {
        this.service = service;
        this.authorRepository = authorRepository;
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new BookForm());
        model.addAttribute("authors", authorRepository.findAll());
        return "books/form";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model) {
        Book book = service.findById(id);
        model.addAttribute("book", book);
        return "books/show";
    }

    @GetMapping
    public String list(@RequestParam(required = false) String author, Model model) {
        model.addAttribute("books", service.findAll(author));
        model.addAttribute("author", author);
        model.addAttribute("title", "Catalogue");
        return "books/list";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") BookForm form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            model.addAttribute("authors", authorRepository.findAll());
            return "books/form";
        }

        Author author = authorRepository.findAll().stream()
                .filter(a -> a.getName().equalsIgnoreCase(form.getAuthor()))
                .findFirst()
                .orElseThrow(() -> new AuthorNotFoundException(-1L));

        BigDecimal priceAmount = form.getPrice() != null ? form.getPrice() : BigDecimal.ZERO;
        Money price = new Money(priceAmount, "USD");

        Book book = new Book(
                form.getTitle(),
                Genre.NON_FICTION,
                form.getYear(),
                price
        );
        book.setAuthor(author);

        Book saved = service.create(book);

        redirect.addFlashAttribute("message",
                "Book \"" + saved.getTitle() + "\" has been added");
        return "redirect:/books";
    }
}