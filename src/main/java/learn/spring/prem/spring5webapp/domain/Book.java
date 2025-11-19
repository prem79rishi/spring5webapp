package learn.spring.prem.spring5webapp.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "authors")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;
    
    private String title;
    private String isbn;
    
    @ManyToMany
    @JoinTable(name = "author_book", 
               joinColumns = @JoinColumn(name = "book_id"), 
               inverseJoinColumns = @JoinColumn(name = "auth_id"))
    @JsonManagedReference
    private Set<Author> authors;
}
