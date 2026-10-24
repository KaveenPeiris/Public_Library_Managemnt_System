package Controllers.Books;

import javafx.event.ActionEvent;
import scene.SceneManager;

public class ReturnBookPageController {

    public void btnAddBookNavOnAction(ActionEvent actionEvent) {
        SceneManager.switchpage("/view/add_book.fxml");
    }

    public void btnViewBookOnAction(ActionEvent actionEvent) {
        SceneManager.switchpage("/view/view_add_book_page.fxml");
    }

    public void btnViewMembersOnAction(ActionEvent actionEvent) {
        SceneManager.switchpage("/view/view_members.fxml");
    }

    public void btnIssueBookNavOnAction(ActionEvent actionEvent) {
        SceneManager.switchpage("/view/issu_book.fxml");
    }

    public void btnReturnBookNavOnAction(ActionEvent actionEvent) {
        SceneManager.switchpage("/view/return_book.fxml");
    }

    public void btnLogoutOnAction(ActionEvent actionEvent) {
        SceneManager.switchpage("/view/main_dashbord.fxml");
    }

    public void btnProcessReturnOnAction(ActionEvent actionEvent) {

    }
}
