package learn.spring.prem.spring5webapp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "books")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;
    
    private String firstname;
    private String lastname;

    @ManyToMany(mappedBy = "authors")
    @JsonBackReference
    private Set<Book> books;
}
