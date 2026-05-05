package com.kampala.Controllers;

import com.kampala.App;
import com.kampala.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;

public class AddMarketController {

    @FXML
    private TextField marketNameField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField zoneField;
    @FXML
    private TextField rentField;
    @FXML
    private Label statusLabel;

    @FXML
    private void handleAddMarket() {
        String marketName = marketNameField.getText().trim();
        String location = locationField.getText().trim();
        String zone = zoneField.getText().trim();
        String rentText = rentField.getText().trim();

        // Validation
        if (marketName.isEmpty() || location.isEmpty() || zone.isEmpty() || rentText.isEmpty()) {
            setStatus("Please fill in all fields.", false);
            return;
        }

        BigDecimal rent;
        try {
            rent = new BigDecimal(rentText);
            if (rent.compareTo(BigDecimal.ZERO) < 0) {
                setStatus("Rent must be a positive number.", false);
                return;
            }
        } catch (NumberFormatException e) {
            setStatus("Rent must be a valid number (e.g. 150000.00).", false);
            return;
        }

        String sql = "INSERT INTO markets (market_name, location, zone, rent, is_occupied) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, marketName);
            pstmt.setString(2, location);
            pstmt.setString(3, zone);
            pstmt.setBigDecimal(4, rent);
            pstmt.setBoolean(5, false); // new markets start unoccupied

            pstmt.executeUpdate();

            setStatus("Market added successfully!", true);
            clearFields();

        } catch (SQLException e) {
            setStatus("Error adding market: " + e.getMessage(), false);
        }
    }

    private void setStatus(String message, boolean success) {
        statusLabel.setStyle(success
                ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;"
                : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        statusLabel.setText(message);
    }

    private void clearFields() {
        marketNameField.clear();
        locationField.clear();
        zoneField.clear();
        rentField.clear();
    }

    @FXML
    private void goToMarkets() throws IOException {
        App.setRoot("markets");
    }

    @FXML
    private void goToDashboard() throws IOException {
        App.setRoot("dashboard");
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