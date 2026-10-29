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

public class BrrowingHistoryPageController implements Initializable {

    @FXML private Button btnAddBookNav;
    @FXML private Button btnAddMember;
    @FXML private Button btnBrrowingHistory;
    @FXML private Button btnIssueBook;
    @FXML private Button btnLogout;
    @FXML private Button btnManageMember;
    @FXML private Button btnPrevious;
    @FXML private Button btnReturnBook;

    @FXML private TableView<BorrowingModel> historyTable;
    @FXML private TableColumn<BorrowingModel, String> colMemberId;
    @FXML private TableColumn<BorrowingModel, String> colBookTitle;
    @FXML private TableColumn<BorrowingModel, String> colIssueDate;
    @FXML private TableColumn<BorrowingModel, String> colDueDate;
    @FXML private TableColumn<BorrowingModel, String> colReturnDate;
    @FXML private TableColumn<BorrowingModel, String> colStatus;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusComboBox;

    private Connection connection = DBConnection.getInstance().getConnection();
    private ObservableList<BorrowingModel> historyList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setup Table Columns
        colMemberId.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        statusComboBox.setItems(FXCollections.observableArrayList("All", "Issued", "Returned", "Overdue"));
        statusComboBox.setValue("All");

        loadBorrowingHistory("");

        // Search listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadBorrowingHistory(newValue);
        });

        statusComboBox.setOnAction(e -> loadBorrowingHistory(searchField.getText()));
    }

    private void loadBorrowingHistory(String searchText) {
        historyList.clear();
        String statusFilter = statusComboBox.getValue();

        StringBuilder sql = new StringBuilder("SELECT member_id, book_title, issue_date, due_date, return_date, status FROM issued_books WHERE (member_id LIKE ? OR book_title LIKE ?)");

        if (statusFilter != null && !statusFilter.equals("All")) {
            sql.append(" AND status = ?");
        }

        try {
            PreparedStatement pst = connection.prepareStatement(sql.toString());
            pst.setString(1, "%" + searchText + "%");
            pst.setString(2, "%" + searchText + "%");

            if (statusFilter != null && !statusFilter.equals("All")) {
                pst.setString(3, statusFilter);
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                historyList.add(new BorrowingModel(
                        rs.getString("member_id"),
                        rs.getString("book_title"),
                        rs.getString("issue_date"),
                        rs.getString("due_date"),
                        rs.getString("return_date") != null ? rs.getString("return_date") : "N/A",
                        rs.getString("status")
                ));
            }
            historyTable.setItems(historyList);
        } catch (SQLException e) {
            System.out.println("Error loading borrowing history: " + e.getMessage());
        }
    }

    @FXML void btnAddBookNavOnAction(ActionEvent event) { SceneManager.switchpage("/view/add_book.fxml"); }
    @FXML void btnAddMemberOnAction(ActionEvent event) { SceneManager.switchpage("/view/add_member.fxml"); }
    @FXML void btnBrrowingHistoryOnAction(ActionEvent event) { SceneManager.switchpage("/view/borrowing_history.fxml"); }
    @FXML void btnIssueBookOnAction(ActionEvent event) { SceneManager.switchpage("/view/issu_book_view.fxml"); }
    @FXML void btnLogoutOnAction(ActionEvent event) { SceneManager.switchpage("/view/login_page.fxml"); }
    @FXML void btnManageMemberOnAction(ActionEvent event) { SceneManager.switchpage("/view/view_members.fxml"); }
    @FXML void btnPreviousOnAction(ActionEvent event) { SceneManager.switchpage("/view/issu_book_view.fxml"); }
    @FXML void btnReturnBookOnAction(ActionEvent event) { SceneManager.switchpage("/view/return_book.fxml"); }
    public void btnViewBookOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/view_add_book_page.fxml"); }
    public void btnViewMembersOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/view_members.fxml"); }

    // Inner Model Class for TableView
    public static class BorrowingModel {
        private String memberId, bookTitle, issueDate, dueDate, returnDate, status;

        public BorrowingModel(String memberId, String bookTitle, String issueDate, String dueDate, String returnDate, String status) {
            this.memberId = memberId;
            this.bookTitle = bookTitle;
            this.issueDate = issueDate;
            this.dueDate = dueDate;
            this.returnDate = returnDate;
            this.status = status;
        }

        public String getMemberId() { return memberId; }
        public String getBookTitle() { return bookTitle; }
        public String getIssueDate() { return issueDate; }
        public String getDueDate() { return dueDate; }
        public String getReturnDate() { return returnDate; }
        public String getStatus() { return status; }
    }
}