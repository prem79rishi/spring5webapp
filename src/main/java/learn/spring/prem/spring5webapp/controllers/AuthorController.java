package learn.spring.prem.spring5webapp.controllers;

import learn.spring.prem.spring5webapp.domain.Author;
import learn.spring.prem.spring5webapp.repositories.AuthorRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorController {

    private final AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @GetMapping("/authors")
    public Iterable<Author> getAuthors() {
        return authorRepository.findAll();
    }
}
