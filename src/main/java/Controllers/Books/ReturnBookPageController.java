package Controllers.Books;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import scene.SceneManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReturnBookPageController {

    @FXML private TextField txtIssueId; // Or search field for active issues

    private Connection connection = DBConnection.getInstance().getConnection();

    @FXML
    public void btnProcessReturnOnAction(ActionEvent actionEvent) {
        String issueIdStr = txtIssueId.getText();

        if (issueIdStr == null || issueIdStr.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter a valid Issue ID or Transaction ID!").show();
            return;
        }

        try {
            connection.setAutoCommit(false);

            // 1. Get book_id from issued_books
            String getBookSql = "SELECT book_id, status FROM issued_books WHERE issue_id = ?";
            PreparedStatement pstGet = connection.prepareStatement(getBookSql);
            pstGet.setString(1, issueIdStr.trim());
            ResultSet rs = pstGet.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                String bookId = rs.getString("book_id");

                if ("Returned".equals(status)) {
                    new Alert(Alert.AlertType.WARNING, "This book has already been returned!").show();
                    connection.rollback();
                    return;
                }

                // 2. Update issued_books status and return_date
                String updateIssueSql = "UPDATE issued_books SET status = 'Returned', return_date = ? WHERE issue_id = ?";
                PreparedStatement pstUpdateIssue = connection.prepareStatement(updateIssueSql);
                pstUpdateIssue.setString(1, LocalDate.now().toString());
                pstUpdateIssue.setString(2, issueIdStr.trim());
                int updatedIssue = pstUpdateIssue.executeUpdate();

                // 3. Increment book quantity in books table
                String updateBookSql = "UPDATE books SET quantity = quantity + 1 WHERE book_id = ?";
                PreparedStatement pstUpdateBook = connection.prepareStatement(updateBookSql);
                pstUpdateBook.setString(1, bookId);
                int updatedBook = pstUpdateBook.executeUpdate();

                if (updatedIssue > 0 && updatedBook > 0) {
                    connection.commit();
                    new Alert(Alert.AlertType.INFORMATION, "Book Return Processed Successfully!").show();
                    txtIssueId.clear();
                } else {
                    connection.rollback();
                    new Alert(Alert.AlertType.ERROR, "Failed to process return.").show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Issue record not found!").show();
            }
            connection.setAutoCommit(true);

        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
        }
    }

    @FXML void btnAddBookNavOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/add_book.fxml"); }
    @FXML void btnViewBookOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/view_add_book_page.fxml"); }
    @FXML void btnViewMembersOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/view_members.fxml"); }
    @FXML void btnIssueBookNavOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/issu_book.fxml"); }
    @FXML void btnReturnBookNavOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/return_book.fxml"); }
    @FXML void btnLogoutOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/main_dashbord.fxml"); }
}