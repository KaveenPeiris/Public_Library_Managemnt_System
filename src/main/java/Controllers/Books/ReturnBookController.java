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

public class ReturnBookController {

    @FXML private TextField txtIssueId;

    private Connection connection = DBConnection.getInstance().getConnection();

    @FXML
    public void btnProcessReturnOnAction(ActionEvent actionEvent) {
        String issueId = txtIssueId.getText();

        if (issueId == null || issueId.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter the Issue ID.").show();
            return;
        }

        try {
            connection.setAutoCommit(false);

            String selectSql = "SELECT book_id, status FROM issued_books WHERE issue_id = ?";
            PreparedStatement pstSelect = connection.prepareStatement(selectSql);
            pstSelect.setString(1, issueId.trim());
            ResultSet rs = pstSelect.executeQuery();

            if (rs.next()) {
                String bookId = rs.getString("book_id");
                String status = rs.getString("status");

                if ("Returned".equals(status)) {
                    new Alert(Alert.AlertType.WARNING, "Book is already returned.").show();
                    connection.rollback();
                    return;
                }

                String updateIssue = "UPDATE issued_books SET status = 'Returned', return_date = ? WHERE issue_id = ?";
                PreparedStatement pstUpdateIssue = connection.prepareStatement(updateIssue);
                pstUpdateIssue.setString(1, LocalDate.now().toString());
                pstUpdateIssue.setString(2, issueId.trim());
                int res1 = pstUpdateIssue.executeUpdate();

                String updateBook = "UPDATE books SET quantity = quantity + 1 WHERE book_id = ?";
                PreparedStatement pstUpdateBook = connection.prepareStatement(updateBook);
                pstUpdateBook.setString(1, bookId);
                int res2 = pstUpdateBook.executeUpdate();

                if (res1 > 0 && res2 > 0) {
                    connection.commit();
                    new Alert(Alert.AlertType.INFORMATION, "Return Processed Successfully!").show();
                    txtIssueId.clear();
                } else {
                    connection.rollback();
                    new Alert(Alert.AlertType.ERROR, "Failed to process return.").show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Issue record not found.").show();
            }
            connection.setAutoCommit(true);

        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ignored) {}
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
        }
    }

    public void btnAddBookNavOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/add_book.fxml"); }
    public void btnViewBookOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/view_add_book_page.fxml"); }
    public void btnViewMembersOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/view_members.fxml"); }
    public void btnIssueBookNavOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/issu_book.fxml"); }
    public void btnReturnBookNavOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/return_book.fxml"); }
    public void btnLogoutOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/main_dashbord.fxml"); }
}