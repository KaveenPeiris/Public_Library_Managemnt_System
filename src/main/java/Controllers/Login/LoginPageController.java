package Controllers.Login;

import scene.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginPageController {

    LoginController loginController = new LoginController();

    @FXML
    private Button btnLogin;

    @FXML
    private Label errorLabel;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtUsername;

    @FXML
    void btnloginonaction(ActionEvent event) {


    }

    public void txtPasswordOnAction(ActionEvent actionEvent) {

    }

    public void btnLoginbutton(ActionEvent actionEvent) {
        if(loginController.CheckUserNameAndPassword(txtUsername.getText(),txtPassword.getText())){

            SceneManager.switchpage("/view/main_dashbord.fxml");
        }
    }
}


