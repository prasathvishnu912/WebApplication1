package com.practice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JDBCOperation {

	private Connection connection;

	private void dbConInit() {
		String url = "jdbc:mysql://localhost:3306/wep_app";
		String username = "root";
		String password = "Password@123";
		try {
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public User loginAuth(User user) {
		dbConInit();
		User matchedUser = null;
		String sqlQuery = "SELECT * FROM login_cred WHERE name = ? AND password = ? AND email = ?";
		try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

			preparedStatement.setString(1, user.getUsername());
			preparedStatement.setString(2, user.getPassword());
			preparedStatement.setString(3, user.getEmail());
		
			try (ResultSet result = preparedStatement.executeQuery()) {
				if (result.next()) {
					matchedUser = new User(result.getString("name"), result.getString("password"),
							result.getString("email"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return matchedUser;
	}

	public List<BookDetails> getBooks(String username) {
		String sqlQuery = "SELECT * FROM books_details WHERE username=?;";

		List<BookDetails> booksList = new ArrayList<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

			preparedStatement.setString(1, username);

			try (ResultSet result = preparedStatement.executeQuery()) {
				while (result.next()) {
					String bookName = result.getString(2);
					String author = result.getString(3);
					String image = result.getString(4);

					BookDetails book = new BookDetails(bookName, author, image);
					booksList.add(book);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return booksList;
	}

}
