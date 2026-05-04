package com.kampala.Controllers;

import com.kampala.App;
import com.kampala.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.io.IOException;
import java.sql.*;

public class VendorProfileController {

    public static String selectedVendorId;

    @FXML private Label fullNameLabel;
    @FXML private Label licenceLabel;
    @FXML private Label statusBadge;
    @FXML private Label emailLabel;
    @FXML private Label genderLabel;
    @FXML private Label dependantsLabel;
    @FXML private Label residenceLabel;
    @FXML private Label vendorTypeLabel;
    @FXML private Label marketLabel;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        loadVendorProfile();
    }

    private void loadVendorProfile() {
        String sql = "SELECT first_name, last_name, email, gender, has_dependants, " +
                     "place_of_residence, vendor_type, assigned_market, licence_number, status " +
                     "FROM vendors WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(selectedVendorId));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                fullNameLabel.setText(rs.getString("first_name") + " " + rs.getString("last_name"));
                licenceLabel.setText("Licence: " + rs.getString("licence_number"));
                emailLabel.setText(rs.getString("email"));
                genderLabel.setText(rs.getString("gender"));
                dependantsLabel.setText(rs.getBoolean("has_dependants") ? "Yes" : "No");
                residenceLabel.setText(rs.getString("place_of_residence"));
                vendorTypeLabel.setText(rs.getString("vendor_type"));
                String market = rs.getString("assigned_market");
                marketLabel.setText(market != null ? market : "Unassigned");
                String status = rs.getString("status");
                statusBadge.setText(status);
                statusBadge.setStyle(
                    "ACTIVE".equals(status)
                    ? "-fx-text-fill: #2ecc71; -fx-font-weight: bold;"
                    : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;"
                );
            }

        } catch (SQLException e) {
            messageLabel.setText("Error loading profile: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeactivate() {
        updateStatus("INACTIVE");
    }

    @FXML
    private void handleActivate() {
        updateStatus("ACTIVE");
    }

    private void updateStatus(String status) {
        String sql = "UPDATE vendors SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, Integer.parseInt(selectedVendorId));
            stmt.executeUpdate();
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Status updated to " + status);
            loadVendorProfile();

        } catch (SQLException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML private void goToVendors() throws IOException { App.setRoot("vendors"); }
}