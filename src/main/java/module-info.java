module com.example.invoicemanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;

    opens com.example.invoicemanagementsystem to javafx.fxml;
    exports com.example.invoicemanagementsystem;
}