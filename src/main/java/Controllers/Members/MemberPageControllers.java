package Controllers.Members;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import scene.SceneManager;

public class MemberPageControllers {

    @FXML
    private Button btnAddBook;

    @FXML
    private Button btnAddMember;

    @FXML
    private Button btnBorrowingHistory;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnIssuBooks;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnManagerMember;

    @FXML
    private Button btnRegisterMember;

    @FXML
    private Button btnReturnBooks;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtFullName;

    @FXML
    private TextField txtMemberId;

    @FXML
    private TextField txtPhone;

    @FXML
    void btnAddBookOnAction(ActionEvent event) {
    }

    @FXML
    void btnAddMemberOnAction(ActionEvent event) {
    }

    @FXML
    void btnBorrowingHistoryOnAction(ActionEvent event) {
        SceneManager.switchpage("/view/borrowing_history.fxml");
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {

    }

    @FXML
    void btnIssuBookOnAction(ActionEvent event) {
        SceneManager.switchpage("/view/issu_book_view.fxml");
    }

    @FXML
    void btnLogOutOnAction(ActionEvent event) {
        SceneManager.switchpage("/view/login_page.fxml");
    }

    @FXML
    void btnMangerMemebrOnAction(ActionEvent event) {

    }

    @FXML
    void btnRegisterMemberOnAction(ActionEvent event) {

    }

    @FXML
    void btnReturnBooksOnAction(ActionEvent event) {
        SceneManager.switchpage("/view/return_book.fxml");
    }

    @FXML
    public void btnPreviousOnAction(ActionEvent actionEvent) {
        SceneManager.switchpage("/view/main_dashbord.fxml");
    }

    @FXML
    public void btnLogoutOnAction(ActionEvent actionEvent) {
        SceneManager.switchpage("/view/login_page.fxml");
    }
}