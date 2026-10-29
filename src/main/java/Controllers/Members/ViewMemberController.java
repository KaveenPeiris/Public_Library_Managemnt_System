package Controllers.Members;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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

public class ViewMemberController implements Initializable {

    @FXML private TableView<MemberModel> memberTable;
    @FXML private TableColumn<MemberModel, String> colMemberId, colFullName, colEmail, colPhone, colAddress;
    @FXML private TextField searchField;

    private Connection connection = DBConnection.getInstance().getConnection();
    private ObservableList<MemberModel> memberList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colMemberId.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        loadMembers();
        setupSearch();
    }

    private void loadMembers() {
        memberList.clear();
        String sql = "SELECT * FROM members ORDER BY member_id ASC";
        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                memberList.add(new MemberModel(
                        rs.getString("member_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                ));
            }
            memberTable.setItems(memberList);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error loading members: " + e.getMessage()).show();
        }
    }

    private void setupSearch() {
        FilteredList<MemberModel> filteredData = new FilteredList<>(memberList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(member -> {
                if (newValue == null || newValue.trim().isEmpty()) return true;
                String lowerFilter = newValue.toLowerCase().trim();
                return member.getMemberId().toLowerCase().contains(lowerFilter) ||
                        member.getFullName().toLowerCase().contains(lowerFilter) ||
                        member.getEmail().toLowerCase().contains(lowerFilter);
            });
        });
        SortedList<MemberModel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(memberTable.comparatorProperty());
        memberTable.setItems(sortedData);
    }

    @FXML void btnDeleteOnAction(ActionEvent event) {
        MemberModel selected = memberTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a member to delete.").show();
            return;
        }
        try {
            String sql = "DELETE FROM members WHERE member_id = ?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, selected.getMemberId());
            if (pst.executeUpdate() > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Member deleted successfully!").show();
                loadMembers();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
        }
    }

    @FXML void btnUpdateOnAction(ActionEvent event) { /* Update handling */ }
    @FXML void btnAddBookOnAction(ActionEvent event) { SceneManager.switchpage("/view/add_book.fxml"); }
    @FXML void btnViewBookOnAction(ActionEvent event) { SceneManager.switchpage("/view/view_add_book_page.fxml"); }
    @FXML void btnAddMemberOnAction(ActionEvent event) { SceneManager.switchpage("/view/add_member.fxml"); }
    @FXML void btnViewMembersOnAction(ActionEvent event) { SceneManager.switchpage("/view/view_members.fxml"); }
    @FXML void btnManageMembersOnAction(ActionEvent event) { SceneManager.switchpage("/view/view_members.fxml"); }
    @FXML void btnIssuBooksOnAction(ActionEvent event) { SceneManager.switchpage("/view/issu_book_view.fxml"); }
    @FXML void btnReturnBooksOnAction(ActionEvent event) { SceneManager.switchpage("/view/return_book.fxml"); }
    @FXML void btnBorrowingHistoryOnAction(ActionEvent event) { SceneManager.switchpage("/view/borrowing_history.fxml"); }
    @FXML void btnPreviousOnAction(ActionEvent event) { SceneManager.switchpage("/view/main_dashbord.fxml"); }
    @FXML void btnLogoutOnAction(ActionEvent event) { SceneManager.switchpage("/view/login_page.fxml"); }

    public static class MemberModel {
        private String memberId, fullName, email, phone, address;

        public MemberModel(String memberId, String fullName, String email, String phone, String address) {
            this.memberId = memberId;
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.address = address;
        }

        public String getMemberId() { return memberId; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getAddress() { return address; }
    }
}