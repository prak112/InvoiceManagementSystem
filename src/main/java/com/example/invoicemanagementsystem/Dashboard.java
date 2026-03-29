package com.example.invoicemanagementsystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class Dashboard {
// TODO read invoice data from InvoiceManager to visualize paidCount and unpaidCount

    private int paidCount = 0;
    private int unpaidCout = 0;
    Dashboard(){ }

    public GridPane getStatusPane() {
        // statusPane and its children panes
        GridPane statusPane = getGridPane();
        statusPane.setAlignment(Pos.TOP_LEFT);
        GridPane paidPane = getGridPane();
        paidPane.setStyle("-fx-border-color: black; -fx-border-radius: 5");
        GridPane unpaidPane = getGridPane();
        unpaidPane.setStyle("-fx-border-color: black; -fx-border-radius: 5");

        // elements inside statusPane
        Label paidLabel = new Label("Paid");
        Label unpaidLabel = new Label("Unpaid");
        Text paidCount = new Text("0");
        Text unpaidCount = new Text("0");
        ImageView okCheckbox = new ImageView("src/main/resources/images/ok-checkbox.png");
        okCheckbox.setFitWidth(25);
        okCheckbox.setFitHeight(25);
        ImageView crossCheckbox = new ImageView("src/main/resources/images/cross-checkbox.png");
        crossCheckbox.setFitWidth(25);
        crossCheckbox.setFitHeight(25);

        // add elements to statusPane and its children panes
        paidPane.add(okCheckbox, 0, 0);
        paidPane.add(paidLabel, 1, 0);
        paidPane.add(paidCount, 4, 0);
        unpaidPane.add(crossCheckbox, 0, 0);
        unpaidPane.add(unpaidLabel, 1, 0);
        unpaidPane.add(unpaidCount, 4, 0);
        statusPane.add(paidPane, 0, 0);
        statusPane.add(unpaidPane, 1, 0);

        // TODO update count labels based on invoices viewed in the table from getInvoiceData

        return statusPane;
    }

    private GridPane getGridPane(){
        GridPane gp = new GridPane(10, 10);
        gp.setPadding(new Insets(10, 10, 10, 10));
        gp.setStyle("-fx-background-color: whitesmoke; " +
                "-fx-border-color: black; " +
                "-fx-text-fill: black;");
        gp.setPrefSize(100, 75);
        return gp;
    }
}
