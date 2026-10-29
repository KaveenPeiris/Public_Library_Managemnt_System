package Controllers.Books;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddBookPageController {
    Connection connection = DBConnection.getInstance().getConnection();

    public ObservableList<String> category() {
        ObservableList<String> categoryList = FXCollections.observableArrayList();

        String sql = "SELECT category_id, category_name FROM category ORDER BY category_id ASC";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String categoryId = resultSet.getString("category_id");
                String categoryName = resultSet.getString("category_name");
                categoryList.add(categoryId + " - " + categoryName);
            }
        } catch (SQLException e) {
            System.out.println("Could Not Find the Category in DB: " + e.getMessage());
        }

        if (categoryList.isEmpty()) {
            categoryList.addAll("Romance", "Fantasy", "Science Fiction", "Mystery & Crime",
                    "Thriller & Suspense", "Horror", "Historical Fiction",
                    "Literary Fiction", "Young Adult (YA) & Children’s");
        }
        return categoryList;
    }

    public String generateBookId(String category) {

        String cleanCat = (category != null) ? category.replaceAll("[^a-zA-Z]", "") : "";
        String catPrefix = cleanCat.length() >= 3
                ? cleanCat.substring(0, 3).toUpperCase()
                : "BOK";

        String year = String.valueOf(LocalDate.now().getYear()).substring(2);
        String prefix = catPrefix + year;

        String sql = "SELECT book_id FROM books WHERE book_id LIKE ? ORDER BY book_id DESC LIMIT 1";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, prefix + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String lastBookId = resultSet.getString("book_id");
                if (lastBookId != null && lastBookId.length() > prefix.length()) {
                    String lastNumberStr = lastBookId.substring(prefix.length());
                    try {
                        int nextNumber = Integer.parseInt(lastNumberStr) + 1;
                        return prefix + String.format("%03d", nextNumber);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error generating Book ID: " + e.getMessage());
        }

        return prefix + "001";
    }
    public boolean addBook(String bookId, String bookTitle, String author, String category, int publishYear, int quantity) {
        Connection connection1 = DBConnection.getInstance().getConnection();
        String sql = "INSERT INTO books (book_id, title, author, category, published_year, quantity) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection1.prepareStatement(sql);
            preparedStatement.setString(1, bookId);
            preparedStatement.setString(2, bookTitle);
            preparedStatement.setString(3, author);
            preparedStatement.setString(4, category);
            preparedStatement.setInt(5, publishYear);
            preparedStatement.setInt(6, quantity); // Fixed from index 7 to 6

            return preparedStatement.executeUpdate() > 0;


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}