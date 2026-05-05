package com.kampala.Controllers;

import com.kampala.App;
import com.kampala.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.*;

public class AddAdminController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> roleBox;
    @FXML
    private TextField zoneField;
    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        roleBox.setItems(FXCollections.observableArrayList("ADMINISTRATOR", "INSPECTOR"));
        roleBox.setValue("ADMINISTRATOR");
    }

    @FXML
    private void handleAddAdmin() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleBox.getValue();
        String zone = zoneField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || role == null) {
            setMessage("Please fill in all required fields.", false);
            return;
        }

        String sql = "INSERT INTO users (first_name, last_name, email, username, password, role, zone, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email.isEmpty() ? null : email);
            stmt.setString(4, username);
            stmt.setString(5, password);
            stmt.setString(6, role);
            stmt.setString(7, zone.isEmpty() ? null : zone);
            stmt.executeUpdate();

            setMessage("Administrator '" + username + "' created successfully!", true);
            clearFields();

        } catch (SQLException e) {
            if (e.getMessage().contains("unique") || e.getMessage().contains("duplicate")) {
                setMessage("Username already exists.", false);
            } else {
                setMessage("Error: " + e.getMessage(), false);
            }
        }
    }

    private void setMessage(String msg, boolean success) {
        messageLabel.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        messageLabel.setText(msg);
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        usernameField.clear();
        passwordField.clear();
        zoneField.clear();
        roleBox.setValue("ADMINISTRATOR");
    }

    @FXML
    private void goToAdmins() throws IOException {
        App.setRoot("admins");
    }

    @FXML
    private void showDashboard() throws IOException {
        App.setRoot("dashboard");
    }

    @FXML
    private void showVendors() throws IOException {
        App.setRoot("vendors");
    }

    @FXML
    private void showMarkets() throws IOException {
        App.setRoot("markets");
    }

    @FXML
    private void showAdmins() throws IOException {
        App.setRoot("admins");
    }

    @FXML
    private void showUnassigned() throws IOException {
        App.setRoot("unassigned");
    }
}