package Controllers.Books;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import scene.SceneManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddBookPageController implements Initializable {

    @FXML private Button btnAddBookNav;
    @FXML private Button btnAddMember;
    @FXML private Button btnAutoGenerate;
    @FXML private Button btnBorrowingHistory;
    @FXML private Button btnClear;
    @FXML private Button btnDashboard;
    @FXML private Button btnIssuBooks;
    @FXML private Button btnLogout;
    @FXML private Button btnManageMembers;
    @FXML private Button btnPrevious;
    @FXML private Button btnReturnBooks;
    @FXML private Button btnSaveBook;
    @FXML private Button btnViewBook;
    @FXML private Button btnViewMembers;

    @FXML private ComboBox<String> cmbCategory;

    @FXML private TextField txtAuthor;
    @FXML private TextField txtBookId;
    @FXML private TextField txtQuantity;
    @FXML private TextField txtTitle;
    @FXML private TextField txtYear;

    private Connection connection = DBConnection.getInstance().getConnection();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load Categories into ComboBox on startup
        cmbCategory.setItems(loadCategoriesFromDB());
    }

    private ObservableList<String> loadCategoriesFromDB() {
        ObservableList<String> categoryList = FXCollections.observableArrayList();
        String sql = "SELECT DISTINCT category FROM books";
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String cat = rs.getString("category");
                if (cat != null && !cat.trim().isEmpty()) {
                    categoryList.add(cat);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error loading categories from DB: " + e.getMessage());
        }

        // Fallback default categories if database is empty
        if (categoryList.isEmpty()) {
            categoryList.addAll("Romance", "Fantasy", "Science Fiction", "Mystery & Crime",
                    "Thriller & Suspense", "Horror", "Historical Fiction",
                    "Literary Fiction", "Educational / Academic");
        }
        return categoryList;
    }

    @FXML
    void btnAutoGenerateOnAction(ActionEvent event) {
        String selectedCategory = cmbCategory.getValue();
        if (selectedCategory == null || selectedCategory.trim().isEmpty()) {
            selectedCategory = "General";
        }

        String cleanCat = selectedCategory.replaceAll("[^a-zA-Z]", "");
        String catPrefix = cleanCat.length() >= 3 ? cleanCat.substring(0, 3).toUpperCase() : "BOK";

        String year = String.valueOf(LocalDate.now().getYear()).substring(2);
        String prefix = catPrefix + year;

        String sql = "SELECT book_id FROM books WHERE book_id LIKE ? ORDER BY book_id DESC LIMIT 1";

        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, prefix + "%");
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String lastBookId = rs.getString("book_id");
                if (lastBookId != null && lastBookId.length() > prefix.length()) {
                    String lastNumberStr = lastBookId.substring(prefix.length());
                    try {
                        int nextNumber = Integer.parseInt(lastNumberStr) + 1;
                        txtBookId.setText(prefix + String.format("%03d", nextNumber));
                        return;
                    } catch (NumberFormatException ignored) {}
                }
            }
            txtBookId.setText(prefix + "001");
        } catch (SQLException e) {
            txtBookId.setText(prefix + "001");
            System.out.println("Error generating Book ID: " + e.getMessage());
        }
    }

    @FXML
    void btnSaveBookOnAction(ActionEvent event) {
        String bookId = txtBookId.getText().trim();
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String category = cmbCategory.getValue();
        String yearStr = txtYear.getText().trim();
        String qtyStr = txtQuantity.getText().trim();

        if (bookId.isEmpty() || title.isEmpty() || author.isEmpty() || category == null || yearStr.isEmpty() || qtyStr.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please fill in all required fields!").show();
            return;
        }

        try {
            int publishYear = Integer.parseInt(yearStr);
            int quantity = Integer.parseInt(qtyStr);

            String sql = "INSERT INTO books (book_id, title, author, publish_year, quantity, category) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, bookId);
            pst.setString(2, title);
            pst.setString(3, author);
            pst.setInt(4, publishYear);
            pst.setInt(5, quantity);
            pst.setString(6, category);

            if (pst.executeUpdate() > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Book added successfully to catalog!").show();
                btnClearOnAction(event);
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Publish Year and Quantity must be valid numbers!").show();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        txtBookId.clear();
        txtTitle.clear();
        txtAuthor.clear();
        txtYear.clear();
        txtQuantity.clear();
        cmbCategory.getSelectionModel().clearSelection();
    }

    // Navigation Action Methods
    @FXML void btnPreviousOnAction(ActionEvent event) { SceneManager.switchpage("/view/main_dashbord.fxml"); }
    @FXML void btnAddBookOnAction(ActionEvent event) { SceneManager.switchpage("/view/add_book.fxml"); }
    @FXML void btnAddMemberOnAction(ActionEvent event) { SceneManager.switchpage("/view/add_member.fxml"); }
    @FXML void btnViewBookOnAction(ActionEvent event) { SceneManager.switchpage("/view/view_add_book_page.fxml"); }
    @FXML void btnViewMembersOnAction(ActionEvent event) { SceneManager.switchpage("/view/view_members.fxml"); }
    @FXML void btnManageMembersOnAction(ActionEvent event) { SceneManager.switchpage("/view/manage_members.fxml"); }
    @FXML void btnIssuBooksOnAction(ActionEvent event) { SceneManager.switchpage("/view/issu_book.fxml"); }
    @FXML void btnReturnBooksOnAction(ActionEvent event) { SceneManager.switchpage("/view/return_book.fxml"); }
    @FXML void btnBorrowingHistoryOnAction(ActionEvent event) { SceneManager.switchpage("/view/borrowing_history.fxml"); }
    @FXML void btnLogoutOnAction(ActionEvent event) { SceneManager.switchpage("/view/login_page.fxml"); }
}