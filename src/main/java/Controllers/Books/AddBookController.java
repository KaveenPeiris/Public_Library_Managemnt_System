package Controllers.Books;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import scene.SceneManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddBookController implements Initializable {

    @FXML private ComboBox<String> txtCategory;
    @FXML private DatePicker datepublishyear;
    @FXML private Button btnAddBook;
    @FXML private Button btnClear;
    @FXML private TextField txtAuthorName;
    @FXML private TextField txtBookId;
    @FXML private TextField txtBookTitle;
    @FXML private TextField txtQuantity;

    Connection connection = DBConnection.getInstance().getConnection();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        txtCategory.setItems(categoryList());

        txtCategory.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txtBookId.setText(generateBookId(newValue));
            }
        });


    }

    public ObservableList<String> categoryList() {
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

    @FXML
    void btnAddBookOnAction(ActionEvent event) {
        String bookId = txtBookId.getText();
        String title = txtBookTitle.getText();
        String author = txtAuthorName.getText();
        String category = txtCategory.getValue();
        LocalDate publishYear = datepublishyear.getValue();
        String quantityStr = txtQuantity.getText();

        if (title == null || title.trim().isEmpty() ||
                author == null || author.trim().isEmpty() ||
                category == null || category.trim().isEmpty() ||
                publishYear == null ||
                quantityStr == null || quantityStr.trim().isEmpty()) {

            new Alert(Alert.AlertType.WARNING, "Please fill in all required fields!").show();
            return;
        }

        if (bookId == null || bookId.trim().isEmpty()) {
            bookId = generateBookId(category);
            txtBookId.setText(bookId);
        }

        try {
            int quantity = Integer.parseInt(quantityStr.trim());
            int publishedYear = publishYear.getYear();

            boolean isAdded = addBook(
                    bookId.trim(),
                    title.trim(),
                    author.trim(),
                    category.trim(),
                    publishedYear,
                    quantity
            );

            if (isAdded) {
                new Alert(Alert.AlertType.INFORMATION, "Book Added Successfully!\nBook ID: " + bookId).show();
                SceneManager.switchpage("/view/view_add_book_page.fxml");
                btnClearOnAction(event);
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to Add Book!").show();
            }

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Quantity must be a valid number!").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        txtBookId.clear();
        txtBookTitle.clear();
        txtAuthorName.clear();
        txtCategory.getSelectionModel().clearSelection();
        datepublishyear.setValue(null);
        txtQuantity.clear();
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

    public boolean addBook(String bookId, String title, String author, String category, int publishedYear, int quantity) {
        String SQL = "INSERT INTO books (book_id, book_title, author, category, published_year, quantity) VALUES (?, ?, ?, ?, ?, ?)";
        try {

            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, bookId);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, author);
            preparedStatement.setString(4, category);
            preparedStatement.setInt(5, publishedYear);
            preparedStatement.setInt(6, quantity);

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnGenerateIdOnAction(ActionEvent actionEvent) {
    }

    public void ViewBookOnAction(ActionEvent actionEvent) {
    }

    public void ViewMembersOnAction(ActionEvent actionEvent) {
    }

    public void btnAddMemberOnAction(ActionEvent actionEvent) {
        SceneManager.switchpage("/view/add_member_page.fxml");
    }

    public void btnViewBookOnAction(ActionEvent actionEvent) {
        SceneManager.switchpage("/view/view_add_book_page.fxml");
    }

    public void btnViewMembersOnAction(ActionEvent actionEvent) {
        SceneManager.switchpage("/view/view_members.fxml");

    }

    public void btnManageMembersOnAction(ActionEvent actionEvent) {

        SceneManager.switchpage("/view/view_members.fxml");
    }

    public void btnIssuBooksOnAction(ActionEvent actionEvent) {

        SceneManager.switchpage("/view/issu_book_view.fxml");
    }

    public void btnReturnBooksOnAction(ActionEvent actionEvent) {

        SceneManager.switchpage("/view/return_book.fxml");

    }

    public void btnBorrowingHistoryOnAction(ActionEvent actionEvent) {

        SceneManager.switchpage("/view/borrowing_history.fxml");
    }

    public void btnPreviousOnAction(ActionEvent actionEvent) {
        SceneManager.switchpage("/view/main_dashbord.fxml");
    }

    public void btnLogoutOnAction(ActionEvent actionEvent) {

        SceneManager.switchpage("/view/login_page.fxml");
    }
}