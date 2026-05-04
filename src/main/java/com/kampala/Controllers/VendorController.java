package com.kampala.Controllers;

import com.kampala.App;
import com.kampala.DatabaseConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.sql.*;

public class VendorController {

    @FXML private TableView<ObservableList<String>> vendorsTable;
    @FXML private TableColumn<ObservableList<String>, String> colId;
    @FXML private TableColumn<ObservableList<String>, String> colName;
    @FXML private TableColumn<ObservableList<String>, String> colType;
    @FXML private TableColumn<ObservableList<String>, String> colMarket;
    @FXML private TableColumn<ObservableList<String>, String> colLicence;
    @FXML private TableColumn<ObservableList<String>, String> colStatus;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        setupColumns();
        statusFilter.setItems(FXCollections.observableArrayList("All", "ACTIVE", "INACTIVE"));
        statusFilter.setValue("All");
        loadVendors();
    }

    private void setupColumns() {
        colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(0)));
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(1)));
        colType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(2)));
        colMarket.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(3)));
        colLicence.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(4)));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(5)));
    }

    private void loadVendors() {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        String search = searchField.getText().trim();
        String status = statusFilter.getValue();

        String sql = "SELECT id, first_name || ' ' || last_name, vendor_type, " +
                     "COALESCE(assigned_market, 'Unassigned'), " +
                     "COALESCE(licence_number, 'N/A'), status FROM vendors WHERE 1=1";

        if (!search.isEmpty())
            sql += " AND (first_name ILIKE '%" + search + "%' OR last_name ILIKE '%" + search + "%')";
        if (status != null && !status.equals("All"))
            sql += " AND status = '" + status + "'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= 6; i++) {
                    String val = rs.getString(i);
                    row.add(val != null ? val : "N/A");
                }
                data.add(row);
            }
            vendorsTable.setItems(data);
            statusLabel.setText(data.size() + " vendor(s) found.");

        } catch (SQLException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() { loadVendors(); }

    @FXML
    private void handleVendorClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            ObservableList<String> selected = vendorsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                VendorProfileController.selectedVendorId = selected.get(0);
                try {
                    App.setRoot("vendorProfile");
                } catch (IOException e) {
                    statusLabel.setText("Error: " + e.getMessage());
                }
            }
        }
    }

    @FXML private void goToAddVendor() throws IOException { App.setRoot("addVendor"); }
    @FXML private void goToDashboard() throws IOException { App.setRoot("dashboard"); }
}