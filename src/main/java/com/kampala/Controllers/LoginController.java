package com.kampala.Controllers;
import com.kampala.App;
import java.io.IOException;
import com.kampala.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleLogin()  throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Login successful!");
                try {
                    App.setRoot("dashboard");
                } catch (IOException e) {
                    messageLabel.setText("Error navigating to dashboard.");
                }
            } else {
                messageLabel.setText("Invalid username or password.");
            }

        } catch (SQLException e) {
            messageLabel.setText("Database error: " + e.getMessage());
        }
    }
}