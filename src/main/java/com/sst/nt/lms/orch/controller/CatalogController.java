package com.sst.nt.lms.orch.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.sst.nt.lms.orch.model.Author;
import com.sst.nt.lms.orch.model.Book;
import com.sst.nt.lms.orch.model.Publisher;
import com.sst.nt.lms.orch.util.MapBuilder;

/**
 * Controller for cataloging administrators.
 *
 * <p>FIXME: Limit access to most of these endpoints to authorized users
 * @author Jonathan Lovelace
 */
@RestController
public final class CatalogController {
	/**
	 * REST delegate helper.
	 */
	@Autowired
	private RestTemplate delegate;

	/**
	 * Helper method to reduce the amount of repetitive code required for "get-all"
	 * methods.
	 * @param url the URL to send the REST request to
	 * @param <T> the type we expect
	 * @return the response the server sent
	 */
	private <T> ResponseEntity<T> getAll(final String url) {
		return delegate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<T>() {});
	}

	/**
	 * Get all authors from the administrator service.
	 * @return the list of all authors, or other response.
	 */
	@GetMapping({"/authors", "/authors/"})
	public ResponseEntity<List<Author>> getAuthors() {
		return this.<List<Author>>getAll("http://admin/authors/");
	}

	/**
	 * Get all books from the administrator service.
	 * @return the list of all books, or other response
	 */
	 @GetMapping({"/books", "/books/"}) // conflicts with other controller's route
	public ResponseEntity<List<Book>> getBooks() {
		return this.<List<Book>>getAll("http://admin/books/");
	}

	/**
	 * Get all publishers from the administrator service.
	 * @return the list of all publishers, or other response
	 */
	@GetMapping({"/publishers","/publishers/"})
	public ResponseEntity<List<Publisher>> getPublishers() {
		return this.<List<Publisher>>getAll("http://admin/publishers/");
	}

	/**
	 * Get an author by its ID number from the administrator service.
	 * @param authorId the ID number of the author
	 * @return the author, or other response
	 * @throws TransactionException if author not found, or on internal error
	 */
	@GetMapping({"/author/{authorId}", "/author/{authorId}/"})
	public ResponseEntity<Author> getAuthor(@PathVariable("authorId") final int authorId) {
		return delegate.getForEntity("http://admin/author/" + authorId, Author.class);
	}

	/**
	 * Get a book by its ID number from the administrator service.
	 * @param bookId the ID number of the book
	 * @return the book, or other response
	 */
	 @GetMapping({"/book/{bookId}","/book/{bookId}/"}) // conflicts with other controller's route
	public ResponseEntity<Book> getBook(@PathVariable("bookId") final int bookId) {
		return delegate.getForEntity("http://admin/book/" + bookId, Book.class);
	}

	/**
	 * Get a publisher by ID number from the administrator service.
	 * @param publisherId the ID number of the publisher
	 * @return the publisher, or other response
	 */
	@GetMapping({"/publisher/{publisherId}", "/publisher/{publisherId}/"})
	public ResponseEntity<Publisher> getPublisher(
			@PathVariable("publisherId") final int publisherId) {
		return delegate.getForEntity("http://admin/publisher/" + publisherId,
				Publisher.class);
	}

	/**
	 * Update an author by ID number in the administrator service.
	 * @param authorId the ID number of the author to update
	 * @param input the author data to update.
	 * @return the updated author, or other response
	 */
	@PutMapping({ "/author/{authorId}", "/author/{authorId}/" })
	public ResponseEntity<Author> updateAuthor(
			@PathVariable("authorId") final int authorId,
			@RequestBody final Author input) {
		return delegate.exchange("http://admin/author/" + authorId, HttpMethod.PUT,
				new HttpEntity<>(input), Author.class);
	}

	/**
	 * Update a publisher by ID number in the administrator service.
	 * @param publisherId the ID number of the publisher to update
	 * @param input the publisher data to update.
	 * @return the updated publisher, or other response
	 */
	@PutMapping({ "/publisher/{publisherId}", "/publisher/{publisherId}/" })
	public ResponseEntity<Publisher> updatePublisher(
			@PathVariable("publisherId") final int publisherId,
			@RequestBody final Publisher input) {
		return delegate.exchange("http://admin/publisher/" + publisherId,
				HttpMethod.PUT, new HttpEntity<>(input), Publisher.class);
	}

	/**
	 * Update a book by ID number in the administrator service. If author or
	 * publisher is null in the supplied data, the existing author or publisher is
	 * left alone. If the author or publisher has an ID that is not found in the
	 * database, an error is returned; otherwise, the existing author and publisher
	 * are used (changes to that data in the input are ignored).
	 *
	 * @param bookId the ID number of the book to update
	 * @param input  the book data to update.
	 * @return the updated book, or other response
	 */
	@PutMapping({"/book/{bookId}", "/book/{bookId}/"})
	public ResponseEntity<Book> updateBook(@PathVariable("bookId") final int bookId,
			@RequestBody final Book input) {
		return delegate.exchange("http://admin/book/" + bookId, HttpMethod.PUT,
				new HttpEntity<>(input), Book.class);
	}

	/**
	 * Create an author with the given name in the administrator service.
	 * @param body the request body, which must contain a 'name' field.
	 * @return the created author, or other response
	 */
	@PostMapping({ "/author", "/author/" })
	public ResponseEntity<Author> createAuthor(
			@RequestBody final Map<String, String> body) {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return delegate.postForEntity("http://admin/author",
				new HttpEntity(body, headers), Author.class);
	}

	/**
	 * Create a publisher with the specified parameters in the administrator
	 * service.
	 *
	 * @param body the request body, which must contain a 'name' field;
	 *             'address' and 'phone' fields are also recognized.
	 * @return the created publisher, or other response.
	 */
	@PostMapping({"/publisher", "/publisher/"})
	public ResponseEntity<Publisher> createPublisher(
			@RequestBody final Map<String, String> body) {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return delegate.postForEntity("http://admin/publisher",
				new HttpEntity(body, headers), Publisher.class);
	}

	/**
	 * Create a book with the specified parameters in the administrator service.
	 *
	 * @param body the request body. It must have a title, its ID is
	 *             ignored, and its author and publisher state other than
	 *             ID are ignored unless none with the specified IDs exist,
	 *             in which case they are added to the database and the
	 *             provided IDs are ignored.
	 * @return the created book, or other response
	 */
	@PostMapping({"/book", "/book/"})
	public ResponseEntity<Book> createBook(@RequestBody final Book body) {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return delegate.postForEntity("http://admin/book",
				new HttpEntity(body, headers), Book.class);
	}

	/**
	 * Delete the author with the given ID in the administrator service.
	 * @param authorId the ID of the author to delete.
	 */
	@DeleteMapping({"/author/{authorId}", "/author/{authorId}/"})
	public void deleteAuthor(@PathVariable("authorId") final int authorId) {
		delegate.delete("http://admin/author/" + authorId);
	}

	/**
	 * Delete the publisher with the given ID in the administrator service.
	 * @param publisherId the ID of the publisher to delete
	 */
	@DeleteMapping({ "/publisher/{publisherId}", "/publisher/{publisherId}/" })
	public void deletePublisher(@PathVariable("publisherId") final int publisherId) {
		delegate.delete("http://admin/publisher/" + publisherId);
	}

	/**
	 * Delete the book with the given ID in the administrator service.
	 * @param bookId the ID of the book to delete
	 */
	@DeleteMapping({ "/book/{bookId}", "/book/{bookId}/" })
	public void deleteBook(@PathVariable("bookId") final int bookId) {
		delegate.delete("http://admin/book/" + bookId);
	}
}
