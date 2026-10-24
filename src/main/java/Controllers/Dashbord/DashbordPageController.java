package Controllers.Dashbord;

import javafx.event.ActionEvent;
import scene.SceneManager;

public class DashbordPageController {

        public void AddBookOnAction(ActionEvent actionEvent) {

                SceneManager.switchpage("/view/add_book.fxml");

                }

        public void btnAddMemberOnAction(ActionEvent actionEvent) {
                SceneManager.switchpage("/view/add_member_page.fxml");
        }

        public void btnManageMembersOnAction(ActionEvent actionEvent) {

                SceneManager.switchpage("/view/view_members.fxml");

        }

        public void BtnIssuBooksOnAction(ActionEvent actionEvent) {

                SceneManager.switchpage("/view/issu_book.fxml");
        }

        public void BtnReturnBookOnAction(ActionEvent actionEvent) {

                SceneManager.switchpage("view/return_book.fxml");

        }

        public void BtnBorrowingHistoryOnAction(ActionEvent actionEvent) {

                SceneManager.switchpage("/view/borrowing_history.fxml");

        }

        public void BtnLogOutOnAction(ActionEvent actionEvent) {

                SceneManager.switchpage("/view/login_page.fxml");
        }

    public void ViewMembersOnAction(ActionEvent actionEvent) {

                SceneManager.switchpage("/view/view_members.fxml");

    }

        public void ViewBookOnAction(ActionEvent actionEvent) {

                SceneManager.switchpage("/view/view_add_book_page.fxml");

        }
}




