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

        }

        public void BtnIssuBooksOnAction(ActionEvent actionEvent) {
        }

        public void BtnReturnBookOnAction(ActionEvent actionEvent) {
        }

        public void BtnBorrowingHistoryOnAction(ActionEvent actionEvent) {
        }

        public void BtnLogOutOnAction(ActionEvent actionEvent) {
                SceneManager.switchpage("/view/login_page.fxml");
        }

    public void ViewMembersOnAction(ActionEvent actionEvent) {
    }

        public void ViewBookOnAction(ActionEvent actionEvent) {
        }
}




