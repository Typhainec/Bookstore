package fr.simplon.bookstore.controller;

import fr.simplon.bookstore.dao.impl.BookRepository;
import fr.simplon.bookstore.dao.impl.CartRepository;
import fr.simplon.bookstore.dao.impl.UserRepository;
import fr.simplon.bookstore.entity.Book;
import fr.simplon.bookstore.entity.Cart;
import fr.simplon.bookstore.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Controller
public class BookController {

    private final BookRepository repo;
    private final CartRepository cartrepo;
    private UserRepository userrepo;

    @Autowired
    public BookController(BookRepository repo, CartRepository cartrepo, UserRepository userrepo) {
        this.repo = repo;
        this.cartrepo = cartrepo;
        this.userrepo = userrepo;
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

    @RequestMapping("/cart/{id}")
    public String cart(@PathVariable Long id, Model model) {
        Optional<Book> optionalBook = repo.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            model.addAttribute("book", book);
        }
        return "cart";
    }


    @PostMapping("/cart/{id}")
    public String addedToCart(@PathVariable("id") Long id, @RequestParam int quantity, Model model, Authentication authentication) {
        try {
            Optional<Book> optionalBook = repo.findById(id);

            if (optionalBook.isPresent()) {
                Book book = optionalBook.get();

                // Récupérer le nom d'utilisateur de l'utilisateur connecté
                String username = authentication.getName();

                // Rechercher l'utilisateur correspondant au nom d'utilisateur
                Users user = userrepo.findByUsername(username);

                // Vérifier si l'utilisateur existe
                if (user != null) {
                    // Créer un nouvel objet Cart avec le livre, la quantité et le nom d'utilisateur
                    Cart cart = new Cart(book, quantity, user.getUsername());

                    // Enregistrer le Cart dans la base de données
                    cartrepo.save(cart);

                    // Ajouter le Cart dans le modèle pour l'affichage dans la vue
                    model.addAttribute("cart", cart);
                }
            }
            return "yourcart";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Le site est en maintenance");
            return "errorPage"; // Remplacez "errorPage" par le nom de votre vue d'erreur
        }
    }


    @RequestMapping("/yourcart/{id}")
    public String yourcart(@PathVariable Long id, Model model) {
        Optional<Book> optionalBook = repo.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            model.addAttribute("book", book);
        }
        return "yourcart";
    }

    @DeleteMapping("/yourcart/{id}")
    public String deleteFromCart(@PathVariable("id") Long id) {
        cartrepo.deleteById(id);
        return "redirect:/index.html";
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
        postBook.setImage(Base64.getDecoder().decode(postBook.getImageBase64()));

        repo.save(postBook);
        Long id = postBook.getId();

        return "redirect:/book-sheet/" + id;
    }


    @GetMapping("/update-book/{id}")
    public String showUpdateBookForm(@PathVariable("id") Long id, Model model) {

        Book book = repo.findById(id).orElse(null);

        model.addAttribute("book", book);
        return "update-book";
    }

    @PostMapping("/update-book/{id}/submit")
    public String updateBook(Model model, @ModelAttribute("book") Book updateBook, @PathVariable("id") Long id) {
        Optional<Book> optionalBook = repo.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setTitle(updateBook.getTitle());
            book.setAuthor(updateBook.getAuthor());
            book.setDescription(updateBook.getDescription());
            String imageBase64 = updateBook.getImageBase64();
            if (imageBase64 != null) {
                byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
                book.setImage(imageBytes);
            }
            book.setPrice(updateBook.getPrice());
            repo.save(book);
        }
        return "redirect:/book-sheet/" + id;
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Book> bookOptional = repo.findById(id);

        if (bookOptional.isPresent()) {
            repo.delete(bookOptional.get());
        }

        return "redirect:/";
    }


}



