package Controllers.Books;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import scene.SceneManager;

public class BrrowingHistoryPageController {

    @FXML
    private Button btnAddBookNav;

    @FXML
    private Button btnAddMember;

    @FXML
    private Button btnBrrowingHistory;

    @FXML
    private Button btnIssueBook;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnManageMember;

    @FXML
    private Button btnPrevious;

    @FXML
    private Button btnReturnBook;

    @FXML
    private TableColumn<?, ?> colBookTitle;

    @FXML
    private TableColumn<?, ?> colDueDate;

    @FXML
    private TableColumn<?, ?> colIssueDate;

    @FXML
    private TableColumn<?, ?> colMemberId;

    @FXML
    private TableColumn<?, ?> colReturnDate;

    @FXML
    private TableColumn<?, ?> colStatus;

    @FXML
    private TableView<?> historyTable;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<?> statusComboBox;

    @FXML
    void btnAddBookNavOnAction(ActionEvent event) {
        SceneManager.switchpage("/view/add_book.fxml");

    }

    @FXML
    void btnAddMemberOnAction(ActionEvent event) {
        SceneManager.switchpage("/view/add_member.fxml");

    }

    @FXML
    void btnBrrowingHistoryOnAction(ActionEvent event) {
        SceneManager.switchpage("/view/borrowing_history.fxml");

    }

    @FXML
    void btnIssueBookOnAction(ActionEvent event) {
        SceneManager.switchpage("/view/issu_book_view.fxml");

    }

    @FXML
    void btnLogoutOnAction(ActionEvent event) {
        SceneManager.switchpage("/view/login_page.fxml");

    }

    @FXML
    void btnManageMemberOnAction(ActionEvent event) {

        SceneManager.switchpage("/view/view_add_book_page.fxml");
    }

    @FXML
    void btnPreviousOnAction(ActionEvent event) {
        SceneManager.switchpage("/view/issu_book_view.fxml");

    }

    @FXML
    void btnReturnBookOnAction(ActionEvent event) {
        SceneManager.switchpage("/view/return_book.fxml");
    }

    public void btnViewBookOnAction(ActionEvent actionEvent) {
        SceneManager.switchpage("/view/view_add_book_page.fxml");
    }

    public void btnViewMembersOnAction(ActionEvent actionEvent) {
        SceneManager.switchpage("/view/view_members.fxml");

    }
}
