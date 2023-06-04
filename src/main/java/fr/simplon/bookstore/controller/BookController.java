package fr.simplon.bookstore.controller;

import fr.simplon.bookstore.dao.impl.BookRepository;
import fr.simplon.bookstore.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
public class BookController {

    private final BookRepository repo;

    public BookController(BookRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/")
    public List<Book> getBooks() {
        return repo.findAll();
    }

    @GetMapping("/book-sheet")
    public Book getBookById(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    @GetMapping("/cart")

    @PutMapping("/form-add-book")
    public Book updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        Book book = repo.findById(id).orElse(null);
        if (book != null) {
            book.setTitle(updatedBook.getTitle());
            book.setAuthor(updatedBook.getAuthor());
            book.setDescription(updatedBook.getDescription());
            book.setImage(updatedBook.getImage());
            book.setPrice(updatedBook.getPrice());
            repo.save(book);
        }
        return book;
    }

    @Autowired
    private BookRepository bookRepository;

    public ModelAndView books() {
        ModelAndView mav = new ModelAndView("booklist");
        List<Book> books = bookRepository.findAll();
        mav.addObject("book", books);
        return mav;
    }
}