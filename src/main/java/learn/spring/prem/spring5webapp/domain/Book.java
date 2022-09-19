package learn.spring.prem.spring5webapp.domain;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Book {

    private String title;
    private String isbn;
    @ManyToMany
    @JoinTable(name="author_book",joinColumns = @JoinColumn(name = "book_id"),inverseJoinColumns = @JoinColumn(name = "auth_id"))
    private Set<Author> authors;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Book() {
    }

    public Book(String title, String isbn, Set<Author> authors) {
        this.title = title;
        this.isbn = isbn;
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authorList) {
        this.authors = authorList;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}
