package com.kampala.Controllers;

import com.kampala.App;
import com.kampala.DatabaseConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarketController {

    @FXML
    private TableView<ObservableList<String>> marketsTable;
    @FXML
    private TableColumn<ObservableList<String>, String> colId;
    @FXML
    private TableColumn<ObservableList<String>, String> colName;
    @FXML
    private TableColumn<ObservableList<String>, String> colLocation;
    @FXML
    private TableColumn<ObservableList<String>, String> colZone;
    @FXML
    private TableColumn<ObservableList<String>, String> colRent;
    @FXML
    private TableColumn<ObservableList<String>, String> colOccupancy;
    @FXML
    private TableColumn<ObservableList<String>, String> colAssignedVendor;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> occupancyFilter;
    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        setupColumns();
        occupancyFilter.setItems(FXCollections.observableArrayList("All", "Available", "Occupied"));
        occupancyFilter.setValue("All");
        loadMarkets();
    }

    private void setupColumns() {
        colId.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(0)));
        colName.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(1)));
        colLocation.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(2)));
        colZone.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(3)));
        colRent.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(4)));
        colOccupancy.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(5)));
        colAssignedVendor.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().get(6)));
    }

    @FXML
    private void loadMarkets() {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        String search = searchField.getText().trim();
        String occupancy = occupancyFilter.getValue();

        StringBuilder sql = new StringBuilder(
                "SELECT id, market_name, location, zone, rent, " +
                        "CASE WHEN is_occupied THEN 'Occupied' ELSE 'Available' END, " +
                        "COALESCE(assigned_vendor_id::text, 'None') FROM markets WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (!search.isEmpty()) {
            sql.append(" AND (market_name ILIKE ? OR location ILIKE ? OR zone ILIKE ?)");
            String pattern = "%" + search + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        if (occupancy != null && !occupancy.equals("All")) {
            sql.append(" AND is_occupied = ?");
            params.add("Occupied".equals(occupancy));
        }

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                } else if (param instanceof Boolean) {
                    stmt.setBoolean(i + 1, (Boolean) param);
                } else {
                    stmt.setObject(i + 1, param);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(rs.getString(1));
                    row.add(rs.getString(2));
                    row.add(rs.getString(3));
                    row.add(rs.getString(4));
                    BigDecimal rent = rs.getBigDecimal(5);
                    row.add(rent != null ? rent.toPlainString() : "0.00");
                    row.add(rs.getString(6));
                    row.add(rs.getString(7));
                    data.add(row);
                }
            }

            marketsTable.setItems(data);
            statusLabel.setText(data.size() + " market(s) found.");

        } catch (SQLException e) {
            statusLabel.setText("Error loading markets: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        loadMarkets();
    }

    @FXML
    private void handleFilter() {
        loadMarkets();
    }

    @FXML
    private void goToDashboard() throws IOException {
        App.setRoot("dashboard");
    }

    @FXML
    private void goToAddMarket() throws IOException {
        App.setRoot("addMarket");
    }

    @FXML
    private void goToVendors() throws IOException {
        App.setRoot("vendors");
    }
}