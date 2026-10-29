package Controllers.Members;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import scene.SceneManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class MemberPageControllers implements Initializable {

    @FXML private Button btnAddBook, btnAddMember, btnBorrowingHistory, btnClear, btnIssuBooks, btnLogout, btnManagerMember, btnRegisterMember, btnReturnBooks, btnGenerateMemberId;
    @FXML private TextField txtAddress, txtEmail, txtFullName, txtMemberId, txtPhone;

    private Connection connection = DBConnection.getInstance().getConnection();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtMemberId.setText(generateMemberId());
    }

    private String generateMemberId() {
        String year = String.valueOf(LocalDate.now().getYear()).substring(2);
        String prefix = "MEM" + year;
        String sql = "SELECT member_id FROM members ORDER BY member_id DESC LIMIT 1";

        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int lastId = rs.getInt("member_id");
                return String.valueOf(lastId + 1);
            }
        } catch (SQLException e) {
            System.out.println("Error generating Member ID: " + e.getMessage());
        }
        return "1";
    }

    @FXML
    void btnRegisterMemberOnAction(ActionEvent event) {
        String name = txtFullName.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();
        String address = txtAddress.getText();

        if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty() || address == null || address.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please fill in all required fields!").show();
            return;
        }

        String sql = "INSERT INTO members (full_name, email, phone, address) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, name.trim());
            pst.setString(2, email.trim());
            pst.setString(3, phone.trim());
            pst.setString(4, address.trim());

            if (pst.executeUpdate() > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Member Registered Successfully!").show();
                btnClearOnAction(event);
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to register member.").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
        }
    }

    @FXML void btnGenerateMemberIdOnAction(ActionEvent event) { txtMemberId.setText(generateMemberId()); }
    @FXML void btnClearOnAction(ActionEvent event) { txtFullName.clear(); txtEmail.clear(); txtPhone.clear(); txtAddress.clear(); txtMemberId.setText(generateMemberId()); }
    @FXML void btnAddBookOnAction(ActionEvent event) { SceneManager.switchpage("/view/add_book.fxml"); }
    @FXML void btnViewBookOnAction(ActionEvent event) { SceneManager.switchpage("/view/view_add_book_page.fxml"); }
    @FXML void btnAddMemberOnAction(ActionEvent event) { SceneManager.switchpage("/view/add_member.fxml"); }
    @FXML void btnViewMembersOnAction(ActionEvent event) { SceneManager.switchpage("/view/view_members.fxml"); }
    @FXML void btnBorrowingHistoryOnAction(ActionEvent event) { SceneManager.switchpage("/view/borrowing_history.fxml"); }
    @FXML void btnIssuBookOnAction(ActionEvent event) { SceneManager.switchpage("/view/issu_book_view.fxml"); }
    @FXML void btnMangerMemebrOnAction(ActionEvent event) { SceneManager.switchpage("/view/view_members.fxml"); }
    @FXML void btnReturnBooksOnAction(ActionEvent event) { SceneManager.switchpage("/view/return_book.fxml"); }
    @FXML void btnPreviousOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/main_dashbord.fxml"); }
    @FXML void btnLogoutOnAction(ActionEvent actionEvent) { SceneManager.switchpage("/view/login_page.fxml"); }
}