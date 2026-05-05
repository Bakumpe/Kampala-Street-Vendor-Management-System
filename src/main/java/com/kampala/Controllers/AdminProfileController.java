package com.kampala.Controllers;

import com.kampala.App;
import com.kampala.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.*;

public class AdminProfileController {

    public static String selectedAdminId;

    @FXML private Label fullNameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;
    @FXML private Label zoneLabel;
    @FXML private Label statusBadge;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField zoneField;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList("ADMINISTRATOR", "INSPECTOR"));
        loadProfile();
    }

    private void loadProfile() {
        String sql = "SELECT COALESCE(first_name || ' ' || last_name, username), username, " +
                     "COALESCE(email, 'N/A'), COALESCE(role, 'ADMINISTRATOR'), " +
                     "COALESCE(zone, 'All Zones'), COALESCE(status, 'ACTIVE') " +
                     "FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(selectedAdminId));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                fullNameLabel.setText(rs.getString(1));
                usernameLabel.setText("@" + rs.getString(2));
                emailLabel.setText(rs.getString(3));
                roleLabel.setText(rs.getString(4));
                zoneLabel.setText(rs.getString(5));

                String status = rs.getString(6);
                statusBadge.setText(status);
                statusBadge.setStyle("ACTIVE".equals(status)
                    ? "-fx-text-fill: #2ecc71; -fx-font-weight: bold;"
                    : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

                roleComboBox.setValue(rs.getString(4));
                zoneField.setText("All Zones".equals(rs.getString(5)) ? "" : rs.getString(5));
            }

        } catch (SQLException e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateRole() {
        String role = roleComboBox.getValue();
        String zone = zoneField.getText().trim();

        String sql = "UPDATE users SET role = ?, zone = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role);
            stmt.setString(2, zone.isEmpty() ? null : zone);
            stmt.setInt(3, Integer.parseInt(selectedAdminId));
            stmt.executeUpdate();

            setMessage("Role updated successfully.", true);
            loadProfile();

        } catch (SQLException e) {
            setMessage("Error: " + e.getMessage(), false);
        }
    }

    @FXML private void handleDeactivate() { updateStatus("INACTIVE"); }
    @FXML private void handleActivate()   { updateStatus("ACTIVE"); }

    private void updateStatus(String status) {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, Integer.parseInt(selectedAdminId));
            stmt.executeUpdate();
            setMessage("Status updated to " + status + ".", true);
            loadProfile();

        } catch (SQLException e) {
            setMessage("Error: " + e.getMessage(), false);
        }
    }

    private void setMessage(String msg, boolean success) {
        messageLabel.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        messageLabel.setText(msg);
    }

    @FXML private void goToAdmins() throws IOException { App.setRoot("admins"); }
}