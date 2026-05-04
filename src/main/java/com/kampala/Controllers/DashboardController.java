package com.kampala.Controllers;

import com.kampala.App;
import com.kampala.DatabaseConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DashboardController {

    @FXML private Label totalVendorsLabel;
    @FXML private Label activeVendorsLabel;
    @FXML private Label totalMarketsLabel;
    @FXML private Label unassignedLabel;
    @FXML private Label statusLabel;

    @FXML private TableView<ObservableList<String>> vendorsTable;
    @FXML private TableColumn<ObservableList<String>, String> colName;
    @FXML private TableColumn<ObservableList<String>, String> colVendorType;
    @FXML private TableColumn<ObservableList<String>, String> colMarket;
    @FXML private TableColumn<ObservableList<String>, String> colLicence;
    @FXML private TableColumn<ObservableList<String>, String> colStatus;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadStats();
        loadRecentVendors();
    }

    private void setupTableColumns() {
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        colVendorType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        colMarket.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        colLicence.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));
    }

    private void loadStats() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Total vendors
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM vendors");
            if (rs.next()) totalVendorsLabel.setText(String.valueOf(rs.getInt(1)));

            // Active vendors
            rs = stmt.executeQuery("SELECT COUNT(*) FROM vendors WHERE status = 'ACTIVE'");
            if (rs.next()) activeVendorsLabel.setText(String.valueOf(rs.getInt(1)));

            // Total markets
            rs = stmt.executeQuery("SELECT COUNT(*) FROM markets");
            if (rs.next()) totalMarketsLabel.setText(String.valueOf(rs.getInt(1)));

            // Unassigned vendors
            rs = stmt.executeQuery("SELECT COUNT(*) FROM vendors WHERE assigned_market IS NULL");
            if (rs.next()) unassignedLabel.setText(String.valueOf(rs.getInt(1)));

        } catch (SQLException e) {
            statusLabel.setText("Error loading stats: " + e.getMessage());
        }
    }

    private void loadRecentVendors() {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(
                "SELECT first_name || ' ' || last_name, vendor_type, assigned_market, licence_number, status " +
                "FROM vendors ORDER BY id DESC LIMIT 10"
            );

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= 5; i++) {
                    String val = rs.getString(i);
                    row.add(val != null ? val : "N/A");
                }
                data.add(row);
            }

            vendorsTable.setItems(data);

        } catch (SQLException e) {
            statusLabel.setText("Error loading vendors: " + e.getMessage());
        }
    }

    @FXML private void showDashboard() { statusLabel.setText("Dashboard"); }
    @FXML private void showVendors() throws IOException { App.setRoot("vendors"); }
    @FXML private void showMarkets() throws IOException { App.setRoot("markets"); }
    @FXML private void showAdmins() { statusLabel.setText("Administrators — coming soon"); }
    @FXML private void showUnassigned() { statusLabel.setText("Unassigned Vendors — coming soon"); }

    @FXML
    private void handleLogout() {
        try {
            App.setRoot("login");
        } catch (IOException e) {
            statusLabel.setText("Logout error: " + e.getMessage());
        }
    }
}