package Controllers.Dashbord;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import scene.SceneManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DashbordController implements Initializable {

        @FXML private Button btnAddBook;
        @FXML private Button btnAddMember;
        @FXML private Button btnBorrowingHistory;
        @FXML private Button btnIssuBooks;
        @FXML private Button btnLogOut;
        @FXML private Button btnManageMembers;
        @FXML private Button btnReturnBooks;
        @FXML private StackPane contentArea;
        @FXML private Label lblBorrowedBooks;
        @FXML private Label lblOverdueBooks;
        @FXML private Label lblTotalBooks;
        @FXML private Label lblTotalMembers;

        private Connection connection = DBConnection.getInstance().getConnection();

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
                loadDashboardStatistics();
        }

        private void loadDashboardStatistics() {
                try {
                        // 1. Total Books Count
                        String bookSql = "SELECT COUNT(*) FROM books";
                        PreparedStatement pstBooks = connection.prepareStatement(bookSql);
                        ResultSet rsBooks = pstBooks.executeQuery();
                        if (rsBooks.next() && lblTotalBooks != null) {
                                lblTotalBooks.setText(String.valueOf(rsBooks.getInt(1)));
                        }

                        // 2. Total Members Count
                        String memberSql = "SELECT COUNT(*) FROM members";
                        PreparedStatement pstMembers = connection.prepareStatement(memberSql);
                        ResultSet rsMembers = pstMembers.executeQuery();
                        if (rsMembers.next() && lblTotalMembers != null) {
                                lblTotalMembers.setText(String.valueOf(rsMembers.getInt(1)));
                        }

                        // 3. Borrowed Books Count (Status = 'Issued')
                        String borrowedSql = "SELECT COUNT(*) FROM issued_books WHERE status = 'Issued'";
                        PreparedStatement pstBorrowed = connection.prepareStatement(borrowedSql);
                        ResultSet rsBorrowed = pstBorrowed.executeQuery();
                        if (rsBorrowed.next() && lblBorrowedBooks != null) {
                                lblBorrowedBooks.setText(String.valueOf(rsBorrowed.getInt(1)));
                        }

                        // 4. Overdue Books Count (Due date before today and status = 'Issued')
                        String overdueSql = "SELECT COUNT(*) FROM issued_books WHERE status = 'Issued' AND due_date < CURDATE()";
                        PreparedStatement pstOverdue = connection.prepareStatement(overdueSql);
                        ResultSet rsOverdue = pstOverdue.executeQuery();
                        if (rsOverdue.next() && lblOverdueBooks != null) {
                                lblOverdueBooks.setText(String.valueOf(rsOverdue.getInt(1)));
                        }

                } catch (SQLException e) {
                        System.out.println("Error loading dashboard statistics: " + e.getMessage());
                }
        }

        @FXML
        void AddBookOnAction(ActionEvent event) {
                SceneManager.switchpage("/view/add_book.fxml");
        }

        @FXML
        void btnAddMemberOnAction(ActionEvent event) {
                SceneManager.switchpage("/view/add_member.fxml");
        }

        @FXML
        void btnManageMembersOnAction(ActionEvent event) {
                SceneManager.switchpage("/view/view_members.fxml");
        }

        @FXML
        void BtnIssuBooksOnAction(ActionEvent event) {
                SceneManager.switchpage("/view/issu_book.fxml");
        }

        @FXML
        void BtnReturnBookOnAction(ActionEvent event) {
                SceneManager.switchpage("/view/return_book.fxml"); // Fixed missing leading slash[cite: 24]
        }

        @FXML
        void BtnBorrowingHistoryOnAction(ActionEvent event) {
                SceneManager.switchpage("/view/borrowing_history.fxml");
        }

        @FXML
        void BtnLogOutOnAction(ActionEvent event) {
                SceneManager.switchpage("/view/login_page.fxml");
        }

        @FXML
        void ViewMembersOnAction(ActionEvent event) {
                SceneManager.switchpage("/view/view_members.fxml");
        }

        @FXML
        void ViewBookOnAction(ActionEvent event) {
                SceneManager.switchpage("/view/view_add_book_page.fxml");
        }
}