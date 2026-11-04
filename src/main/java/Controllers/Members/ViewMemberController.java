package Controllers.Members;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import scene.SceneManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ViewMemberController implements Initializable {

    @FXML private TableView<MemberModel> memberTable;
    @FXML private TableColumn<MemberModel, String> colMemberId;
    @FXML private TableColumn<MemberModel, String> colFullName;
    @FXML private TableColumn<MemberModel, String> colEmail;
    @FXML private TableColumn<MemberModel, String> colPhone;
    @FXML private TableColumn<MemberModel, String> colAddress;

    @FXML private TextField searchField;

    @FXML private Button btnAddBook;
    @FXML private Button btnAddMember;
    @FXML private Button btnBorrowingHistory;
    @FXML private Button btnDelete;
    @FXML private Button btnIssuBooks;
    @FXML private Button btnLogout;
    @FXML private Button btnManageMembers;
    @FXML private Button btnPrevious;
    @FXML private Button btnReturnBooks;
    @FXML private Button btnUpdate;
    @FXML private Button btnViewBook;
    @FXML private Button btnViewMembers;

    private Connection connection = DBConnection.getInstance().getConnection();
    private ObservableList<MemberModel> memberList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 1. Setup Table Columns Cell Value Factories
        colMemberId.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        // 2. Load Members and Setup Live Search
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
                        member.getEmail().toLowerCase().contains(lowerFilter) ||
                        member.getPhone().toLowerCase().contains(lowerFilter);
            });
        });

        SortedList<MemberModel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(memberTable.comparatorProperty());
        memberTable.setItems(sortedData);
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        MemberModel selected = memberTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a member to delete.").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete member: " + selected.getFullName() + "?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Deletion");
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
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
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        MemberModel selected = memberTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a member to update!").show();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Update Member Details");
        dialog.setHeaderText("Edit details for Member ID: " + selected.getMemberId());

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nameField = new TextField(selected.getFullName());
        TextField emailField = new TextField(selected.getEmail());
        TextField phoneField = new TextField(selected.getPhone());
        TextField addressField = new TextField(selected.getAddress());

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 10, 10));

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Address:"), 0, 3);
        grid.add(addressField, 1, 3);

        dialogPane.setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String updatedName = nameField.getText().trim();
            String updatedEmail = emailField.getText().trim();
            String updatedPhone = phoneField.getText().trim();
            String updatedAddress = addressField.getText().trim();

            if (updatedName.isEmpty() || updatedEmail.isEmpty() || updatedPhone.isEmpty() || updatedAddress.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "All fields must be filled out!").show();
                return;
            }

            try {
                String sql = "UPDATE members SET full_name = ?, email = ?, phone = ?, address = ? WHERE member_id = ?";
                PreparedStatement pst = connection.prepareStatement(sql);
                pst.setString(1, updatedName);
                pst.setString(2, updatedEmail);
                pst.setString(3, updatedPhone);
                pst.setString(4, updatedAddress);
                pst.setString(5, selected.getMemberId());

                if (pst.executeUpdate() > 0) {
                    new Alert(Alert.AlertType.INFORMATION, "Member updated successfully!").show();
                    loadMembers();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
            }
        }
    }

    // Navigation Action Methods
    @FXML void btnAddBookOnAction(ActionEvent event) { SceneManager.switchpage("/view/add_book.fxml"); }
    @FXML void btnViewBookOnAction(ActionEvent event) { SceneManager.switchpage("/view/view_add_book_page.fxml"); }
    @FXML void btnAddMemberOnAction(ActionEvent event) { SceneManager.switchpage("/view/add_member.fxml"); }
    @FXML void btnViewMembersOnAction(ActionEvent event) { SceneManager.switchpage("/view/view_members.fxml"); }
    @FXML void btnManageMembersOnAction(ActionEvent event) { SceneManager.switchpage("/view/view_members.fxml"); }
    @FXML void btnIssuBooksOnAction(ActionEvent event) { SceneManager.switchpage("/view/issu_book.fxml"); }
    @FXML void btnReturnBooksOnAction(ActionEvent event) { SceneManager.switchpage("/view/return_book.fxml"); }
    @FXML void btnBorrowingHistoryOnAction(ActionEvent event) { SceneManager.switchpage("/view/borrowing_history.fxml"); }
    @FXML void btnPreviousOnAction(ActionEvent event) { SceneManager.switchpage("/view/main_dashbord.fxml"); }
    @FXML void btnLogoutOnAction(ActionEvent event) { SceneManager.switchpage("/view/login_page.fxml"); }

    // MemberModel Inner Class
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