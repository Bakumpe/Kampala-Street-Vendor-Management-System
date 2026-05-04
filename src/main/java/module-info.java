module com.kampala {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;

    opens com.kampala to javafx.fxml;
    opens com.kampala.Controllers to javafx.fxml;

    exports com.kampala;
}