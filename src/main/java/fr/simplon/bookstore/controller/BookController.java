package fr.simplon.bookstore.controller;

import fr.simplon.bookstore.dao.impl.BookRepository;
import fr.simplon.bookstore.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class BookController {

    private final BookRepository repo;

    @Autowired
    public BookController(BookRepository repo) {
        this.repo = repo;
    }


    @GetMapping("/")
    public String getBooks(Model model) {
        List<Book> books = repo.findAll();
        model.addAttribute("books", books);
        return "index";
    }

    @GetMapping("/rgpd")
    public String getRGPDPage(Model model) {
        return "rgpd";
    }

    @GetMapping("/contact")
    public String getContactPage(Model model) {
        return "contact";
    }

    @RequestMapping("/cart")
    public String cart(Model model) {
        model.addAttribute("book", new Book());
        return "cart";
    }



    @GetMapping("/book-sheet/{id}")
    public String getBookById(@PathVariable Long id, Model model) {
        Optional<Book> optionalBook = repo.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            model.addAttribute("book", book);
        }
        return "book-sheet";
    }

    @RequestMapping("/form-add-book")
    public String addBook(Model model) {
        model.addAttribute("book", new Book());
        return "form-add-book";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/form-add-book")
    public String postBook(@ModelAttribute("postBook") Book postBook) {
        repo.save(postBook);
        Long id = postBook.getId();

        return "redirect:/book-sheet/" + id;
    }


}