# Implementation Plan

- [x] 1. Fix JPA entity annotations and methods
  - Remove duplicate @Id annotation from Book.getId() method, keeping only the field annotation
  - Add getId() method to Author entity
  - Fix toString() methods in both entities to prevent infinite recursion by excluding collection fields
  - _Requirements: 1.1, 1.2, 1.3_

- [x] 2. Create Spring Data JPA repositories
  - Create AuthorRepository interface extending CrudRepository<Author, Long>
  - Create BookRepository interface extending CrudRepository<Book, Long>
  - Place repositories in a new 'repositories' package
  - _Requirements: 2.1, 2.2, 2.3_

- [ ] 3. Implement bootstrap data initialization
  - Create BootstrapData class implementing CommandLineRunner
  - Inject AuthorRepository and BookRepository
  - Create at least 2 authors and 2 books with bidirectional relationships
  - Log the count of created entities
  - _Requirements: 3.1, 3.2, 3.3, 3.4_

- [x] 4. Create REST controllers
  - Create BookController with GET /books endpoint
  - Create AuthorController with GET /authors endpoint
  - Add @JsonManagedReference and @JsonBackReference annotations to prevent JSON serialization infinite recursion
  - Place controllers in a new 'controllers' package
  - _Requirements: 4.1, 4.2, 4.3_

- [x] 5. Add JUnit-Quickcheck dependency for property-based testing
  - Add junit-quickcheck-core and junit-quickcheck-generators dependencies to pom.xml
  - Configure for use with JUnit 4 (compatible with Spring Boot 2.7.3)
  - _Requirements: 5.2, 5.5_

- [ ] 6. Write integration tests for repositories
- [ ] 6.1 Write integration test for repository context loading
  - Test that AuthorRepository and BookRepository beans are available
  - _Requirements: 2.1, 2.2, 5.1_

- [ ] 6.2 Write property test for repository save-retrieve round trip
  - **Property 1: Repository save-retrieve round trip**
  - **Validates: Requirements 2.3, 5.2**
  - Generate random Author and Book entities
  - Verify saving and retrieving maintains data integrity
  - Run 100 iterations minimum

- [ ] 6.3 Write property test for bidirectional relationship consistency
  - **Property 2: Bidirectional relationship consistency**
  - **Validates: Requirements 3.3, 5.5**
  - Generate random Books with Authors
  - Verify both sides of relationship are maintained after persistence
  - Run 100 iterations minimum

- [ ] 7. Write integration tests for REST endpoints
- [ ] 7.1 Write integration test for GET /books endpoint
  - Verify endpoint returns 200 OK status
  - Verify response is valid JSON
  - Verify response contains expected book data
  - _Requirements: 4.1, 5.3, 5.4_

- [ ] 7.2 Write integration test for GET /authors endpoint
  - Verify endpoint returns 200 OK status
  - Verify response is valid JSON
  - Verify response contains expected author data
  - _Requirements: 4.2, 5.3, 5.4_

- [ ] 7.3 Write integration test for JSON serialization
  - Verify that fetching books/authors doesn't cause infinite recursion
  - Verify JSON structure is correct
  - _Requirements: 4.3_

- [ ] 8. Write integration test for bootstrap data
  - Verify at least 2 authors are created on startup
  - Verify at least 2 books are created on startup
  - Verify relationships are established correctly
  - _Requirements: 3.1, 3.2, 3.3_

- [ ] 9. Write integration test for toString() methods
  - Create entities with bidirectional relationships
  - Call toString() on both Author and Book
  - Verify no StackOverflowError occurs
  - _Requirements: 1.3_

- [ ] 10. Final checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.
