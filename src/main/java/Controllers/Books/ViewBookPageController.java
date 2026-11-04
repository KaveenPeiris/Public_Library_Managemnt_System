package Controllers.Books;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
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

    Connection connection = DBConnection.getInstance().getConnection();
    private ObservableList<Map<String, String>> bookList = FXCollections.observableArrayList();
    private FilteredList<Map<String, String>> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colBookId.setCellValueFactory(new MapValueFactory<>("book_id"));
        colTitle.setCellValueFactory(new MapValueFactory<>("book_title"));
        colAuthor.setCellValueFactory(new MapValueFactory<>("author"));
        colYear.setCellValueFactory(new MapValueFactory<>("published_year")); // Fixed to match database column name
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
                categoryList.add(resultSet.getString("category_id") + " - " + resultSet.getString("category_name"));
            }
        } catch (SQLException e) {
            System.out.println("Could Not Find Categories in DB: " + e.getMessage());
        }

        if (categoryList.size() <= 1) {
            categoryList.addAll("Romance", "Fantasy", "Science Fiction", "Mystery & Crime", "Thriller & Suspense");
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
                row.put("book_id", resultSet.getString("book_id"));
                row.put("book_title", resultSet.getString("book_title"));
                row.put("author", resultSet.getString("author"));
                row.put("published_year", resultSet.getString("published_year")); // Fixed to match database column name
                row.put("quantity", String.valueOf(resultSet.getInt("quantity")));
                row.put("category", resultSet.getString("category"));

                bookList.add(row);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error loading books: " + e.getMessage()).show();
        }

        if (filteredData != null) {
            filteredData.setPredicate(b -> true);
        }
    }

    private void setupSearchAndFilter() {
        filteredData = new FilteredList<>(bookList, b -> true);

        Runnable updateFilter = () -> {
            filteredData.setPredicate(book -> {
                String searchText = searchField.getText();
                String selectedCategory = categoryComboBox.getValue();

                boolean matchesSearch = true;
                if (searchText != null && !searchText.trim().isEmpty()) {
                    String lowerFilter = searchText.toLowerCase().trim();
                    matchesSearch = book.get("book_id").toLowerCase().contains(lowerFilter) ||
                            book.get("book_title").toLowerCase().contains(lowerFilter) ||
                            book.get("author").toLowerCase().contains(lowerFilter) ||
                            book.get("category").toLowerCase().contains(lowerFilter);
                }

                boolean matchesCategory = true;
                if (selectedCategory != null && !selectedCategory.equals("All Categories")) {
                    String selectedCat = selectedCategory.contains(" - ") ? selectedCategory.split(" - ")[1].trim() : selectedCategory.trim();
                    String bookCat = book.get("category");
                    matchesCategory = bookCat.equalsIgnoreCase(selectedCat) || bookCat.equalsIgnoreCase(selectedCategory);
                }

                return matchesSearch && matchesCategory;
            });
        };

        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateFilter.run());
        categoryComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateFilter.run());

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
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete Book ID: " + bookId + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            try {
                PreparedStatement pstmt = connection.prepareStatement("DELETE FROM books WHERE book_id = ?");
                pstmt.setString(1, bookId);
                if (pstmt.executeUpdate() > 0) {
                    new Alert(Alert.AlertType.INFORMATION, "Book deleted successfully!").show();
                    loadBooks();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
            }
        }
    }

    @FXML void btnUpdateOnAction(ActionEvent event) { SceneManager.switchpage( "/view/update_book.fxml"); }
    @FXML void btnAddBookOnAction(ActionEvent event) { SceneManager.switchpage("/view/add_book.fxml"); }
    @FXML void btnViewBookOnAction(ActionEvent event) { SceneManager.switchpage("/view/view_add_book_page.fxml"); }
    @FXML void btnAddMemberOnAction(ActionEvent event) { SceneManager.switchpage("/view/add_member.fxml"); }
    @FXML void btnViewMembersOnAction(ActionEvent event) { SceneManager.switchpage("/view/view_members.fxml"); }
    @FXML void btnManageMembersOnAction(ActionEvent event) { SceneManager.switchpage("/view/view_members.fxml"); }
    @FXML void btnIssuBooksOnAction(ActionEvent event) { SceneManager.switchpage("/view/issu_book_view.fxml"); }
    @FXML void btnReturnBooksOnAction(ActionEvent event) { SceneManager.switchpage("/view/return_book.fxml"); }
    @FXML void btnBorrowingHistoryOnAction(ActionEvent event) { SceneManager.switchpage("/view/borrowing_history.fxml"); }
    @FXML void btnPreviousOnAction(ActionEvent event) { SceneManager.switchpage("/view/add_book.fxml"); }
    @FXML void btnLogoutOnAction(ActionEvent event) { SceneManager.switchpage("/view/login_page.fxml"); }
    public void categoryComboBoxOnAction(ActionEvent actionEvent) {}
    public void searchFieldOnAction(ActionEvent actionEvent) {}
}