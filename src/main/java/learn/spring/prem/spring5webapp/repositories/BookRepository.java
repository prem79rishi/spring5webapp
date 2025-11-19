package learn.spring.prem.spring5webapp.repositories;

import learn.spring.prem.spring5webapp.domain.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, Long> {
}
