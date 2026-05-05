package com.kampala.Controllers;

import com.kampala.App;
import com.kampala.DatabaseConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.sql.*;

public class AdminController {

    @FXML private TableView<ObservableList<String>> adminsTable;
    @FXML private TableColumn<ObservableList<String>, String> colId;
    @FXML private TableColumn<ObservableList<String>, String> colName;
    @FXML private TableColumn<ObservableList<String>, String> colUsername;
    @FXML private TableColumn<ObservableList<String>, String> colEmail;
    @FXML private TableColumn<ObservableList<String>, String> colRole;
    @FXML private TableColumn<ObservableList<String>, String> colZone;
    @FXML private TableColumn<ObservableList<String>, String> colStatus;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        setupColumns();
        loadAdmins();
    }

    private void setupColumns() {
        colId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(0)));
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(1)));
        colUsername.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(2)));
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(3)));
        colRole.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(4)));
        colZone.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(5)));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(6)));
    }

    private void loadAdmins() {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        String sql = "SELECT id, COALESCE(first_name || ' ' || last_name, username), username, " +
                     "COALESCE(email, 'N/A'), COALESCE(role, 'ADMINISTRATOR'), " +
                     "COALESCE(zone, 'All Zones'), COALESCE(status, 'ACTIVE') " +
                     "FROM users ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= 7; i++) row.add(rs.getString(i));
                data.add(row);
            }
            adminsTable.setItems(data);
            statusLabel.setText(data.size() + " administrator(s) found.");

        } catch (SQLException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdminClick(javafx.scene.input.MouseEvent event) {
        if (event.getClickCount() == 2) {
            ObservableList<String> selected = adminsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                AdminProfileController.selectedAdminId = selected.get(0);
                try { App.setRoot("adminProfile"); }
                catch (IOException e) { statusLabel.setText("Error: " + e.getMessage()); }
            }
        }
    }

    @FXML private void goToAddAdmin()   throws IOException { App.setRoot("addAdmin"); }
    @FXML private void goToDashboard()  throws IOException { App.setRoot("dashboard"); }
}