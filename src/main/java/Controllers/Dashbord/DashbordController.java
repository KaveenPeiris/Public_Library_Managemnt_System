package Controllers.Dashbord;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import scene.SceneManager;

public class DashbordController {




        @FXML
        private Button btnAddBook;

        @FXML
        private Button btnAddMember;

        @FXML
        private Button btnBorrowingHistory;

        @FXML
        private Button btnIssuBooks;

        @FXML
        private Button btnLogOut;

        @FXML
        private Button btnManageMembers;

        @FXML
        private Button btnReturnBooks;

        @FXML
        private StackPane contentArea;

        @FXML
        private Label lblBorrowedBooks;

        @FXML
        private Label lblOverdueBooks;

        @FXML
        private Label lblTotalBooks;

        @FXML
        private Label lblTotalMembers;

        @FXML
        void AddBookOnAction(ActionEvent event) {

        }

        @FXML
        void BtnBorrowingHistoryOnAction(ActionEvent event) {
            SceneManager.switchpage("");
        }

        @FXML
        void BtnIssuBooksOnAction(ActionEvent event) {

        }

        @FXML
        void BtnLogOutOnAction(ActionEvent event) {

        }

        @FXML
        void BtnReturnBookOnAction(ActionEvent event) {

        }

        @FXML
        void btnAddMemberOnAction(ActionEvent event) {

        }

        @FXML
        void btnManageMembersOnAction(ActionEvent event) {

        }

    }







