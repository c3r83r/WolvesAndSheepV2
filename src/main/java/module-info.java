module com.example.sheeps {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.jakmit.sheeps to javafx.fxml;
    exports com.jakmit.sheeps;
}