package com.kampala.Controllers;

import com.kampala.App;
import com.kampala.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class AddVendorController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> genderBox;
    @FXML private ComboBox<String> dependantsBox;
    @FXML private TextField residenceField;
    @FXML private ComboBox<String> vendorTypeBox;
    @FXML private ComboBox<String> marketBox;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        loadMarkets();
    }

    private void loadMarkets() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT market_name FROM markets")) {

            java.util.List<String> markets = new java.util.ArrayList<>();
            markets.add("Unassigned");
            while (rs.next()) markets.add(rs.getString(1));
            marketBox.setItems(FXCollections.observableArrayList(markets));

        } catch (SQLException e) {
            messageLabel.setText("Could not load markets: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddVendor() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String gender = genderBox.getValue();
        String dependants = dependantsBox.getValue();
        String residence = residenceField.getText().trim();
        String vendorType = vendorTypeBox.getValue();
        String market = marketBox.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || gender == null || vendorType == null) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Please fill in all required fields.");
            return;
        }

        int currentYear = LocalDate.now().getYear();
        String licenceNumber = "KCCA-" + System.currentTimeMillis() + "-" + currentYear;
        String assignedMarket = (market == null || market.equals("Unassigned")) ? null : market;
        boolean hasDependants = "Yes".equals(dependants);

        String sql = "INSERT INTO vendors (first_name, last_name, email, gender, has_dependants, " +
                     "place_of_residence, vendor_type, assigned_market, licence_number, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, gender);
            stmt.setBoolean(5, hasDependants);
            stmt.setString(6, residence);
            stmt.setString(7, vendorType);
            stmt.setString(8, assignedMarket);
            stmt.setString(9, licenceNumber);
            stmt.executeUpdate();

            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Vendor registered successfully! Licence: " + licenceNumber);
            clearFields();

        } catch (SQLException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    private void clearFields() {
        firstNameField.clear(); lastNameField.clear();
        emailField.clear(); residenceField.clear();
        genderBox.setValue(null); dependantsBox.setValue(null);
        vendorTypeBox.setValue(null); marketBox.setValue(null);
    }

    @FXML private void goToVendors() throws IOException { App.setRoot("vendors"); }
}