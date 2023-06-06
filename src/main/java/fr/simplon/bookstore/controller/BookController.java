package fr.simplon.bookstore.controller;

import fr.simplon.bookstore.dao.impl.BookRepository;
import fr.simplon.bookstore.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
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


    @PostMapping("/form-add-book")  // Utilisez @PostMapping au lieu de @PutMapping pour les formulaires HTML
    public String updateBook(@PathVariable Long id, Book updatedBook) {  // Supprimez l'annotation @RequestBody car elle est utilisée pour les requêtes JSON
        Optional<Book> optionalBook = repo.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setTitle(updatedBook.getTitle());
            book.setAuthor(updatedBook.getAuthor());
            book.setDescription(updatedBook.getDescription());
            book.setImage(updatedBook.getImage());
            book.setPrice(updatedBook.getPrice());
            repo.save(book);
        }
        return "redirect:/book-sheet/" + id;
    }




}