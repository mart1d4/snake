module com.upec {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    opens com.upec to javafx.fxml;

    exports com.upec;
}
