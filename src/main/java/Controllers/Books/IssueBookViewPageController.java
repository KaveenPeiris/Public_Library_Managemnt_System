package Controllers.Books;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import scene.SceneManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class IssueBookViewPageController implements Initializable {

    @FXML private Button btnAddBookNav, btnAddMember, btnBrrowingHistory, btnIssueBookNav, btnLogout, btnManageMember, btnPrevious, btnReturnBookNav, btnViewBook, btnViewMembers;

    @FXML private TableView<IssuedBookModel> issueTable;
    @FXML private TableColumn<IssuedBookModel, String> colIssueId;
    @FXML private TableColumn<IssuedBookModel, String> colBookId;
    @FXML private TableColumn<IssuedBookModel, String> colBookTitle;
    @FXML private TableColumn<IssuedBookModel, String> colMemberId;
    @FXML private TableColumn<IssuedBookModel, String> colMemberName;
    @FXML private TableColumn<IssuedBookModel, String> colIssueDate;
    @FXML private TableColumn<IssuedBookModel, String> colDueDate;
    @FXML private TableColumn<IssuedBookModel, String> colStatus;

    @FXML private TextField searchField;

    private Connection connection = DBConnection.getInstance().getConnection();
    private ObservableList<IssuedBookModel> issuedList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colIssueId.setCellValueFactory(new PropertyValueFactory<>("issueId"));
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colMemberId.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        colMemberName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadIssuedBooks("");

        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> loadIssuedBooks(newValue));
        }
    }

    private void loadIssuedBooks(String searchText) {
        issuedList.clear();
        String sql = "SELECT i.issue_id, i.book_id, i.book_title, i.member_id, m.full_name AS member_name, i.issue_date, i.due_date, i.status " +
                "FROM issued_books i JOIN members m ON i.member_id = m.member_id " +
                "WHERE i.book_title LIKE ? OR i.member_id LIKE ? OR m.full_name LIKE ?";

        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, "%" + searchText + "%");
            pst.setString(2, "%" + searchText + "%");
            pst.setString(3, "%" + searchText + "%");

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                issuedList.add(new IssuedBookModel(
                        rs.getString("issue_id"),
                        rs.getString("book_id"),
                        rs.getString("book_title"),
                        rs.getString("member_id"),
                        rs.getString("member_name"),
                        rs.getString("issue_date"),
                        rs.getString("due_date"),
                        rs.getString("status")
                ));
            }
            if (issueTable != null) {
                issueTable.setItems(issuedList);
            }
        } catch (SQLException e) {
            System.out.println("Error loading issued books: " + e.getMessage());
        }
    }

    @FXML void btnAddBookNavOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/add_book.fxml"); }
    @FXML void btnViewBookOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/view_add_book_page.fxml"); }
    @FXML void btnAddMemberOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/add_member.fxml"); }
    @FXML void btnViewMembersOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/view_members.fxml"); }
    @FXML void btnManageMemberOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/view_members.fxml"); }
    @FXML void btnIssueBookNavOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/issu_book.fxml"); }
    @FXML void btnReturnBookOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/return_book.fxml"); }
    @FXML void btnBrrowingHistoryOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/borrowing_history.fxml"); }
    @FXML void btnPreviousOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/main_dashbord.fxml"); }
    @FXML void btnLogoutOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/login_page.fxml"); }
    @FXML void btnLogOutOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/login_page.fxml"); } // Added matching FXML binding

    public static class IssuedBookModel {
        private String issueId, bookId, bookTitle, memberId, memberName, issueDate, dueDate, status;

        public IssuedBookModel(String issueId, String bookId, String bookTitle, String memberId, String memberName, String issueDate, String dueDate, String status) {
            this.issueId = issueId;
            this.bookId = bookId;
            this.bookTitle = bookTitle;
            this.memberId = memberId;
            this.memberName = memberName;
            this.issueDate = issueDate;
            this.dueDate = dueDate;
            this.status = status;
        }

        public String getIssueId() { return issueId; }
        public String getBookId() { return bookId; }
        public String getBookTitle() { return bookTitle; }
        public String getMemberId() { return memberId; }
        public String getMemberName() { return memberName; }
        public String getIssueDate() { return issueDate; }
        public String getDueDate() { return dueDate; }
        public String getStatus() { return status; }
    }
}