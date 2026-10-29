package Controllers.Books;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Stage;
import scene.SceneManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ViewBookPageController implements Initializable {

    @FXML private TableView<Map<String, String>> bookTable;
    @FXML private TableColumn<Map, String> colBookId;
    @FXML private TableColumn<Map, String> colTitle;
    @FXML private TableColumn<Map, String> colAuthor;
    @FXML private TableColumn<Map, String> colYear;
    @FXML private TableColumn<Map, String> colQuantity;
    @FXML private TableColumn<Map, String> colCategory;

    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField searchField;

    @FXML private Button btnAddBook;
    @FXML private Button btnBorrowingHistory;
    @FXML private Button btnDelete;
    @FXML private Button btnIssuBooks;
    @FXML private Button btnLogout;
    @FXML private Button btnManageMembers;
    @FXML private Button btnPrevious;
    @FXML private Button btnReturnBooks;
    @FXML private Button btnUpdate;

    Connection connection = DBConnection.getInstance().getConnection();
    private ObservableList<Map<String, String>> bookList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colBookId.setCellValueFactory(new MapValueFactory<>("book_id"));
        colTitle.setCellValueFactory(new MapValueFactory<>("book_title"));
        colAuthor.setCellValueFactory(new MapValueFactory<>("author"));
        colYear.setCellValueFactory(new MapValueFactory<>("published_year"));
        colQuantity.setCellValueFactory(new MapValueFactory<>("quantity"));
        colCategory.setCellValueFactory(new MapValueFactory<>("category"));

        loadCategories();
        loadBooks();
        setupSearchAndFilter();
    }

    public void loadCategories() {
        ObservableList<String> categoryList = FXCollections.observableArrayList();
        String sql = "SELECT category_id, category_name FROM category ORDER BY category_id ASC";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            categoryList.add("All Categories");
            while (resultSet.next()) {
                String categoryId = resultSet.getString("category_id");
                String categoryName = resultSet.getString("category_name");
                categoryList.add(categoryId + " - " + categoryName);
            }
        } catch (SQLException e) {
            System.out.println("Could Not Find the Category in DB: " + e.getMessage());
        }

        if (categoryList.size() <= 1) {
            categoryList.addAll("Romance", "Fantasy", "Science Fiction", "Mystery & Crime",
                    "Thriller & Suspense", "Horror", "Historical Fiction",
                    "Literary Fiction", "Young Adult (YA) & Children’s");
        }
        categoryComboBox.setItems(categoryList);
        categoryComboBox.getSelectionModel().selectFirst();
    }

    public void loadBooks() {
        bookList.clear();
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
                row.put("quantity", String.valueOf(resultSet.getInt("quantity")));

                String category = resultSet.getString("category");
                row.put("category", (category != null && !category.isEmpty()) ? category : "-");

                bookList.add(row);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error loading books: " + e.getMessage()).show();
        }
        bookTable.setItems(bookList);
    }

    private void setupSearchAndFilter() {
        FilteredList<Map<String, String>> filteredData = new FilteredList<>(bookList, b -> true);


        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(book -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase().trim();

                return book.get("book_id").toLowerCase().contains(lowerCaseFilter) ||
                        book.get("book_title").toLowerCase().contains(lowerCaseFilter) ||
                        book.get("author").toLowerCase().contains(lowerCaseFilter) ||
                        book.get("category").toLowerCase().contains(lowerCaseFilter);
            });
        });


        categoryComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(book -> {
                if (newValue == null || newValue.equals("All Categories")) {
                    return true;
                }
                String selectedCat = newValue.contains(" - ") ? newValue.split(" - ")[1].trim() : newValue.trim();
                String bookCat = book.get("category");
                return bookCat.equalsIgnoreCase(selectedCat) || bookCat.equalsIgnoreCase(newValue);
            });
        });

        SortedList<Map<String, String>> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(bookTable.comparatorProperty());
        bookTable.setItems(sortedData);
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        Map<String, String> selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a book to delete!").show();
            return;
        }

        String bookId = selectedBook.get("book_id");
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete Book ID: " + bookId + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            String sql = "DELETE FROM books WHERE book_id = ?";
            try {
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, bookId);
                if (pstmt.executeUpdate() > 0) {
                    new Alert(Alert.AlertType.INFORMATION, "Book deleted successfully!").show();
                    loadBooks();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete book.").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
            }
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        Map<String, String> selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a book to update!").show();
            return;
        }
        new Alert(Alert.AlertType.INFORMATION, "Update selected for Book ID: " + selectedBook.get("book_id")).show();
    }

    @FXML void btnAddBookOnAction(ActionEvent event) {
        loadScene(event, "/views/Books/add_book.fxml"); }
    @FXML void btnBorrowingHistoryOnAction(ActionEvent event) {
        loadScene(event,"/view/borrowing_history.fxml");
    }
    @FXML void btnIssuBooksOnAction(ActionEvent event) {
        SceneManager.switchpage("/view/issu_book_view.fxml");
    }
    @FXML void btnLogoutOnAction(ActionEvent event) {
       SceneManager.switchpage( "/views/login.fxml");
    }
    @FXML void btnManageMembersOnAction(ActionEvent event) {

        SceneManager.switchpage("/view/view_members.fxml");
    }
    @FXML void btnPreviousOnAction(ActionEvent event) {

        loadScene(event, "/Views/Books/add_book.fxml"); }

    @FXML void btnReturnBooksOnAction(ActionEvent event) {
        SceneManager.switchpage("/view/return_book.fxml");
    }

    private void loadScene(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Could not load scene: " + e.getMessage()).show();
        }
    }

    public void btnViewBookOnAction(ActionEvent actionEvent) {
        SceneManager.switchpage("/view/view_add_book_page.fxml");
    }

    public void btnViewMembersOnAction(ActionEvent actionEvent) {
        SceneManager.switchpage("/view/view_members.fxml");
    }

    public void categoryComboBoxOnAction(ActionEvent actionEvent) {

    }

    public void searchFieldOnAction(ActionEvent actionEvent) {
    }
}