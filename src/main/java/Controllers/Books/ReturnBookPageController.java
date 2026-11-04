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
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ReturnBookPageController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TableView<ReturnModel> returnTable;
    @FXML private TableColumn<ReturnModel, String> colIssueId;
    @FXML private TableColumn<ReturnModel, String> colMemberId;
    @FXML private TableColumn<ReturnModel, String> colMemberName;
    @FXML private TableColumn<ReturnModel, String> colBookId;
    @FXML private TableColumn<ReturnModel, String> colBookTitle;
    @FXML private TableColumn<ReturnModel, String> colIssueDate;
    @FXML private TableColumn<ReturnModel, String> colDueDate;
    @FXML private DatePicker dateReturn;

    @FXML private Button btnDashboard;
    @FXML private Button btnAddBookNav;
    @FXML private Button btnAddMember;
    @FXML private Button btnViewBook;
    @FXML private Button btnViewMembers;
    @FXML private Button btnManageMember;
    @FXML private Button btnIssueBookNav;
    @FXML private Button btnReturnBookNav;
    @FXML private Button btnBrrowingHistory;
    @FXML private Button btnLogout;
    @FXML private Button btnPrevious;
    @FXML private Button btnProcessReturn;

    private Connection connection = DBConnection.getInstance().getConnection();
    private ObservableList<ReturnModel> returnList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colIssueId.setCellValueFactory(new PropertyValueFactory<>("issueId"));
        colMemberId.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        colMemberName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        if (dateReturn != null) {
            dateReturn.setValue(LocalDate.now());
        }

        loadPendingReturns("");

        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> loadPendingReturns(newValue));
        }
    }

    private void loadPendingReturns(String searchText) {
        returnList.clear();
        String sql = "SELECT i.issue_id, i.member_id, m.full_name AS member_name, i.book_id, i.book_title, i.issue_date, i.due_date " +
                "FROM issued_books i JOIN members m ON i.member_id = m.member_id " +
                "WHERE i.status = 'Issued' AND (i.member_id LIKE ? OR i.book_id LIKE ? OR m.full_name LIKE ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, "%" + searchText + "%");
            pst.setString(2, "%" + searchText + "%");
            pst.setString(3, "%" + searchText + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                returnList.add(new ReturnModel(
                        rs.getString("issue_id"),
                        rs.getString("member_id"),
                        rs.getString("member_name"),
                        rs.getString("book_id"),
                        rs.getString("book_title"),
                        rs.getString("issue_date"),
                        rs.getString("due_date")
                ));
            }
            if (returnTable != null) {
                returnTable.setItems(returnList);
            }
        } catch (SQLException e) {
            System.out.println("Error loading pending returns: " + e.getMessage());
        }
    }

    @FXML
    public void btnProcessReturnOnAction(ActionEvent actionEvent) {
        ReturnModel selected = returnTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select an issued book record from the table to return!").show();
            return;
        }

        try {
            connection.setAutoCommit(false);

            String updateIssue = "UPDATE issued_books SET status = 'Returned', return_date = ? WHERE issue_id = ?";
            PreparedStatement pst1 = connection.prepareStatement(updateIssue);
            pst1.setString(1, (dateReturn.getValue() != null ? dateReturn.getValue() : LocalDate.now()).toString());
            pst1.setString(2, selected.getIssueId());
            int res1 = pst1.executeUpdate();

            String updateBook = "UPDATE books SET quantity = quantity + 1 WHERE book_id = ?";
            PreparedStatement pst2 = connection.prepareStatement(updateBook);
            pst2.setString(1, selected.getBookId());
            int res2 = pst2.executeUpdate();

            if (res1 > 0 && res2 > 0) {
                connection.commit();
                new Alert(Alert.AlertType.INFORMATION, "Book Return Processed Successfully!").show();
                loadPendingReturns("");
            } else {
                connection.rollback();
                new Alert(Alert.AlertType.ERROR, "Failed to process return.").show();
            }
            connection.setAutoCommit(true);

        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ignored) {}
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
        }
    }

    // Navigation Handlers
    @FXML public void btnAddBookNavOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/add_book.fxml"); }
    @FXML public void btnViewBookOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/view_add_book_page.fxml"); }
    @FXML public void btnViewMembersOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/view_members.fxml"); }
    @FXML public void btnIssueBookNavOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/issu_book.fxml"); }
    @FXML public void btnReturnBookOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/return_book.fxml"); }
    @FXML public void btnPreviousOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/main_dashbord.fxml"); }
    @FXML public void btnLogoutOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/login_page.fxml"); }
    @FXML public void btnLogOutOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/login_page.fxml"); }
    @FXML public void btnAddMemberOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/add_member.fxml"); }
    @FXML public void btnManageMemberOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/manage_members.fxml"); }
    @FXML public void btnBrrowingHistoryOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/borrowing_history.fxml"); }

    public static class ReturnModel {
        private String issueId, memberId, memberName, bookId, bookTitle, issueDate, dueDate;

        public ReturnModel(String issueId, String memberId, String memberName, String bookId, String bookTitle, String issueDate, String dueDate) {
            this.issueId = issueId;
            this.memberId = memberId;
            this.memberName = memberName;
            this.bookId = bookId;
            this.bookTitle = bookTitle;
            this.issueDate = issueDate;
            this.dueDate = dueDate;
        }

        public String getIssueId() { return issueId; }
        public String getMemberId() { return memberId; }
        public String getMemberName() { return memberName; }
        public String getBookId() { return bookId; }
        public String getBookTitle() { return bookTitle; }
        public String getIssueDate() { return issueDate; }
        public String getDueDate() { return dueDate; }
    }
}