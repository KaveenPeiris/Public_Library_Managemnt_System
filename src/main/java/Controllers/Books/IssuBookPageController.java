package Controllers.Books;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import scene.SceneManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class IssuBookPageController implements Initializable {

    @FXML private Button btnAddBookNav, btnAddMember, btnBrrowingHistory, btnClear, btnIssuBooks, btnIssueBook, btnLogout, btnManagerMember, btnPrevious, btnReturnBook;
    @FXML private ComboBox<String> cmbBook;
    @FXML private ComboBox<String> cmbMember;
    @FXML private DatePicker dateDue;
    @FXML private DatePicker dateIssue;

    private Connection connection = DBConnection.getInstance().getConnection();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadBooks();
        loadMembers();
        dateIssue.setValue(LocalDate.now());
        dateDue.setValue(LocalDate.now().plusDays(14)); // Standard 2-week loan period
    }

    private void loadBooks() {
        ObservableList<String> bookList = FXCollections.observableArrayList();
        String sql = "SELECT book_id, book_title FROM books WHERE quantity > 0";
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                bookList.add(rs.getString("book_id") + " - " + rs.getString("book_title"));
            }
            cmbBook.setItems(bookList);
        } catch (SQLException e) {
            System.out.println("Error loading books: " + e.getMessage());
        }
    }

    private void loadMembers() {
        ObservableList<String> memberList = FXCollections.observableArrayList();
        String sql = "SELECT member_id, name FROM members";
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                memberList.add(rs.getString("member_id") + " - " + rs.getString("name"));
            }
            cmbMember.setItems(memberList);
        } catch (SQLException e) {
            System.out.println("Error loading members: " + e.getMessage());
        }
    }

    @FXML
    void btnIssuBooksOnAction(ActionEvent event) {
        String selectedBook = cmbBook.getValue();
        String selectedMember = cmbMember.getValue();
        LocalDate issueDate = dateIssue.getValue();
        LocalDate dueDate = dateDue.getValue();

        if (selectedBook == null || selectedMember == null || issueDate == null || dueDate == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a book, member, and dates!").show();
            return;
        }

        String bookId = selectedBook.split(" - ")[0];
        String bookTitle = selectedBook.substring(selectedBook.indexOf(" - ") + 3);
        String memberId = selectedMember.split(" - ")[0];

        try {
            connection.setAutoCommit(false);

            // 1. Insert into issued_books
            String insertSql = "INSERT INTO issued_books (book_id, book_title, member_id, issue_date, due_date, status) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstInsert = connection.prepareStatement(insertSql);
            pstInsert.setString(1, bookId);
            pstInsert.setString(2, bookTitle);
            pstInsert.setString(3, memberId);
            pstInsert.setString(4, issueDate.toString());
            pstInsert.setString(5, dueDate.toString());
            pstInsert.setString(6, "Issued");
            int added = pstInsert.executeUpdate();

            // 2. Reduce book quantity
            String updateSql = "UPDATE books SET quantity = quantity - 1 WHERE book_id = ?";
            PreparedStatement pstUpdate = connection.prepareStatement(updateSql);
            pstUpdate.setString(1, bookId);
            int updated = pstUpdate.executeUpdate();

            if (added > 0 && updated > 0) {
                connection.commit();
                new Alert(Alert.AlertType.INFORMATION, "Book Issued Successfully!").show();
                btnClearOnAction(event);
                loadBooks(); // Refresh book list quantities
            } else {
                connection.rollback();
                new Alert(Alert.AlertType.ERROR, "Failed to Issue Book.").show();
            }
            connection.setAutoCommit(true);

        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
        }
    }

    @FXML void btnClearOnAction(ActionEvent event) {
        cmbBook.getSelectionModel().clearSelection();
        cmbMember.getSelectionModel().clearSelection();
        dateIssue.setValue(LocalDate.now());
        dateDue.setValue(LocalDate.now().plusDays(14));
    }

    @FXML void btnAddBookNavOnAction(ActionEvent event) { SceneManager.switchpage("/view/add_book.fxml"); }
    @FXML void btnAddMemberOnAction(ActionEvent event) { SceneManager.switchpage("/view/add_member.fxml"); }
    @FXML void btnBrrowingHistoryOnAction(ActionEvent event) { SceneManager.switchpage("/view/borrowing_history.fxml"); }
    @FXML void btnIssueBookOnAction(ActionEvent event) { SceneManager.switchpage("/view/issu_book_view.fxml"); }
    @FXML void btnLogoutOnAction(ActionEvent event) { SceneManager.switchpage("/view/login_page.fxml"); }
    @FXML void btnManageMemberOnAction(ActionEvent event) { SceneManager.switchpage("/view/view_members.fxml"); }
    @FXML void btnPreviousOnAction(ActionEvent event) { SceneManager.switchpage("/view/main_dashbord.fxml"); }
    @FXML void btnReturnBookOnAction(ActionEvent event) { SceneManager.switchpage("/view/return_book.fxml"); }
    @FXML void cmbBookOnAction(ActionEvent event) {}
    @FXML void cmbMemebrOnAction(ActionEvent event) {}
}