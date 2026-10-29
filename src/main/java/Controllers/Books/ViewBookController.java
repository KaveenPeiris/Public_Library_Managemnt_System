package Controllers.Books;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ViewBookController {

    Connection connection = DBConnection.getInstance().getConnection();

    public ObservableList<Map<String, String>> getAllBooks() {
        ObservableList<Map<String, String>> bookList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM books ORDER BY book_id ASC";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("book_id", resultSet.getString("book_id") != null ? resultSet.getString("book_id") : "");
                row.put("book_title", resultSet.getString("book_title") != null ? resultSet.getString("book_title") : "");
                row.put("author", resultSet.getString("author") != null ? resultSet.getString("author") : "");
                row.put("published_year", resultSet.getString("published_year") != null ? resultSet.getString("published_year") : "");

                int quantity = resultSet.getInt("quantity");
                row.put("quantity", String.valueOf(quantity));

                String category = "";
                try {
                    category = resultSet.getString("category");
                } catch (SQLException ignored) {
                }
                row.put("category", (category != null && !category.isEmpty()) ? category : "-");

                bookList.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching books: " + e.getMessage(), e);
        }
        return bookList;
    }

    public ObservableList<String> getCategory() {
        ObservableList<String> categoryList = FXCollections.observableArrayList();
        String sql = "SELECT DISTINCT category FROM category ORDER BY category ASC";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String category = resultSet.getString("category");
                if (category != null && !category.isEmpty()) {
                    categoryList.add(category);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching categories: " + e.getMessage(), e);
        }
        return categoryList;
    }
}