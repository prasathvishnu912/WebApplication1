package com.practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

class User {
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private String username;
	private String email;
	private String password;

	public User(String username, String password, String email) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

}

class ThrottleDetails {
	private long lastAttemptTime;
	private int attempts;

	public ThrottleDetails(long lastAttemptTime, int attempts) {
		this.lastAttemptTime = lastAttemptTime;
		this.attempts = attempts;
	}

	public long getLastAttemptTime() {
		return lastAttemptTime;
	}

	public int getAttempts() {
		return attempts;
	}

	public void incrementAttempts() {
		this.attempts++;
	}
}

class BookDetails {

	public BookDetails(String bookName, String authorName, String imageUrl) {
		super();
		this.bookName = bookName;
		this.authorName = authorName;
		this.imageUrl = imageUrl;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	private String bookName;
	private String authorName;
	private String imageUrl;

}

@WebServlet("/Login")
public class ServletPractice extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private JDBCOperation jdbc = new JDBCOperation();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String username = request.getHeader("Authorization");

		List<BookDetails> bookList = jdbc.getBooks(username);

		Gson gson = new Gson();
		String bookListJson = gson.toJson(bookList);
		response.setContentType("application/json");

		PrintWriter out = response.getWriter();
		out.print(bookListJson);
		out.flush();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		BufferedReader reader = request.getReader();

		Gson gson = new Gson();
		User loggedUser = gson.fromJson(reader.readLine(), User.class);

		User matchedUser = jdbc.loginAuth(loggedUser);

		int maxAttempts = 3;
		long duration = 60000;
		String userKey = loggedUser.getUsername();

		HttpSession session = request.getSession();
		long currentTime = System.currentTimeMillis();
		
		ThrottleDetails throttleDet = (ThrottleDetails) session.getAttribute(userKey);

		if (throttleDet == null) {
			throttleDet = new ThrottleDetails(currentTime, 1);
			session.setAttribute(userKey, throttleDet);
		} else {
			long balanceTime = currentTime - throttleDet.getLastAttemptTime();

			if (balanceTime < duration && throttleDet.getAttempts() >= maxAttempts) {
				response.setStatus(429);
				session.invalidate();
			}

			throttleDet.incrementAttempts();
		}

		boolean isAuthenticated = false;

		if (matchedUser != null && loggedUser.getUsername().equals(matchedUser.getUsername())
				&& loggedUser.getPassword().equals(matchedUser.getPassword())) {
			isAuthenticated = true;
		}

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.print(isAuthenticated);
		out.flush();
	}

}