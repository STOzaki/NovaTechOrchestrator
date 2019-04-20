package com.sst.nt.lms.orch.controller;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.sst.nt.lms.orch.model.Branch;
import com.sst.nt.lms.orch.model.BranchCopies;
import com.sst.nt.lms.orch.model.Book;

/**
 * Controller for Librarian Services.
 * 
 * @author Al-amine AHMED MOUSSA
 * @author Salem (integrating Al-amine's code into this project)
 */
@RestController
public class LibrarianController {


	@Autowired
	RestTemplate restTemplate;

	/**
	 * Helper method to reduce the amount of repetitive code required for "get-all"
	 * methods.
	 * 
	 * @param url the URL to send the REST request to
	 * @param     <T> the type we expect
	 * @return the response the server sent
	 */
	private <T> ResponseEntity<T> doProcess(final String url, HttpMethod method) {
		return restTemplate.exchange(url, method, null, new ParameterizedTypeReference<T>() {
		});
	}

	@RequestMapping({ "/branches", "/branches/" })
	public ResponseEntity<List<Branch>> getbranchs() {
		return this.<List<Branch>>doProcess("http://librarian-service/librarian/branches", HttpMethod.GET);

	}

	@RequestMapping({ "/books", "/books/" })
	public ResponseEntity<List<Book>> getBooks() {
		return this.<List<Book>>doProcess("http://librarian-service/librarian/books", HttpMethod.GET);

	}

	@RequestMapping(path = { "/branch/{branchId}", "/branch/{branchId}/" }, method = RequestMethod.GET)
	public Branch getBranch(@PathVariable("branchId") int branchId) {
		return restTemplate.getForEntity("http://librarian-service/librarian/branch/" + branchId, Branch.class)
				.getBody();
	}

	@RequestMapping({ "/book/{bookId}", "/book/{bookId}/" })
	public Book getBook(@PathVariable("bookId") int bookId) {
		return restTemplate.getForEntity("http://librarian-service/librarian/book/" + bookId, Book.class).getBody();
	}

	@RequestMapping(path = { "/branch/{branchId}", "/branch/{branchId}/" }, method = RequestMethod.PUT)
	public ResponseEntity<Branch> updateBranch(@PathVariable("branchId") final int branchId,
			@RequestBody Branch input) {

		return this.<Branch> doProcess("http://librarian-service/librarian/branch/" + branchId, HttpMethod.PUT);

	}

	@RequestMapping(path = { "/branch/{branchId}/book/{bookId}",
			"/branch/{branchId}/book/{bookId}/" }, method = RequestMethod.PUT)
	public ResponseEntity<BranchCopies> setBranchCopies(@PathVariable("branchId") int branchId,
			@PathVariable("bookId") int bookId, @RequestParam("noOfCopies") int copies) {

		return this.<BranchCopies> doProcess(
				"http://librarian-service/librarian/branch/" + branchId + "/book/" + bookId + "?noOfCopies=" + copies,
				HttpMethod.PUT);
	}

	@RequestMapping(path = { "/branch/{branchId}/book/{bookId}",
			"/branch/{branchId}/book/{bookId}" }, method = RequestMethod.GET)
	public ResponseEntity<BranchCopies> getBranchCopies(@PathVariable("branchId") int branchId,
			@PathVariable("bookId") int bookId) {

		return this.<BranchCopies> doProcess("http://librarian-service/librarian/branch/" + branchId + "/book/" + bookId,
				HttpMethod.GET);
	}

	@RequestMapping({ "/branches/books/copies", "/branches/books/copies/" })
	public ResponseEntity<Map<Branch, Map<Book, Integer>>> getAllCopies() {

		return this.<Map<Branch, Map<Book, Integer>>> doProcess(
				"http://librarian-service/librarian/branches/books/copies", HttpMethod.GET);

	}
}
