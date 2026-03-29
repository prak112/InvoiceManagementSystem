package com.example.invoicemanagementsystem;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/*
* InvoiceManager imports Document, Dashboard, Product classes data for generating the overview.
* Overview is provided for all the listed invoices in the local directory file system.
* These invoices are visualized as a table with details about each invoice and an overall quick summary of paid, unpaid counts.

* * TO BE NOTED:
* Some features are yet to be added -
    * Create/Edit/Delete invoice,
    * CreditNote, Receipt,
    * Dashboard with charts.
* No payment gateway APIs or CRM APIs are used for actual customer payments tracking.
* All invoice data is AI-generated and read from csv files.
* */

public class InvoiceManager extends Application {

    private CSVHandler invoiceFile;
    @Override
    public void start(Stage stage) {
        // Collect invoice filenames from local directory
        final ArrayList<String> filesList = new ArrayList<>();
        final File dirPath = new File("src/main/resources/files");
        File[] filenames = dirPath.listFiles();
        try {
            for(int i = 0; i < filenames.length; i++){
                //System.out.println(filenames[i].getName());
                filesList.add(filenames[i].getName().split("\\.")[0]);
            }
        } catch (NullPointerException npe) {
            System.out.println(npe);
        }

        // setup table data population and UI elements
        invoiceFile = new CSVHandler();
        TableView<Document> invoiceTable = invoiceFile.getInvoiceData();

        // invoice filenames retrieved from local storage to display as a dropdown list
        ComboBox<String> invoiceFilesCbo = new ComboBox<>();
        invoiceFilesCbo.setItems(FXCollections.observableArrayList(filesList));
        invoiceFilesCbo.setStyle("-fx-font-size: 12; -fx-padding: 10;");
        invoiceFilesCbo.setValue("Select Invoice");

        // setup home page
        Label welcomeLabel = new Label("Welcome to simplified-Invoice Management System :)");
        welcomeLabel.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");
        Text welcomeNote = new Text("Create, edit, and manage invoices with ease\nTo start, select a file from " +
                "dropdown list");
        welcomeNote.setFont(Font.font ("Verdana", 20));
        welcomeNote.setFill(Color.DARKSEAGREEN);
        welcomeNote.setStyle("-fx-font-size: 14; -fx-text-fill: #bdc3c7;");
        VBox mainBox = new VBox(10, invoiceFilesCbo, welcomeLabel, welcomeNote);//statusPane
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setStyle("-fx-background-color: #2c3e50; -fx-padding: 30;");

        // Report generator button
        Button reportGenBtn = new Button("Generate PDF");

        // invoice summary pane at the bottom
        HBox summaryBox = new HBox(20);
        summaryBox.setAlignment(Pos.CENTER);
        summaryBox.setStyle("-fx-background-color: #34495e; -fx-padding: 20; -fx-border-color: #2c3e50; -fx-border-width: 2 0 0 0;");
        Label totalQtyLabel = new Label("Total Qty: 0");
        totalQtyLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #ecf0f1; -fx-font-weight: bold;");
        Label totalPriceLabel = new Label("Total Price: €0.00");
        totalPriceLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #ecf0f1; -fx-font-weight: bold;");
        Label totalPaidLabel = new Label("Total Paid: €0.00");
        totalPaidLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
        Label totalUnpaidLabel = new Label("Total Unpaid: €0.00");
        totalUnpaidLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        summaryBox.getChildren().addAll(totalQtyLabel, totalPriceLabel, totalPaidLabel, totalUnpaidLabel);


        // trigger CSVHandler when invoice is selected from invoiceFilesCbo
        // Update Invoice table summary when a new invoice is read from invoiceFilesCbo
        invoiceFilesCbo.setOnAction(_ -> {
            String filename = dirPath+"\\"+(invoiceFilesCbo.getValue())+(".csv");
            System.out.println(filename);
            invoiceFile.setFilename(filename);
            invoiceTable.setItems(invoiceFile.getInvoiceData().getItems());
            List<Invoice> dataList = invoiceFile.getInvoiceDataList();
            updateSummary(dataList, totalQtyLabel, totalPriceLabel, totalPaidLabel, totalUnpaidLabel);
            mainBox.getChildren().clear();
            mainBox.getChildren().addAll(reportGenBtn, invoiceFilesCbo, invoiceTable, summaryBox);
        });

        // trigger reportGenBtn to create a PDF report of the CSV file
        reportGenBtn.setOnAction(_ -> {
            String filename = dirPath+"\\"+(invoiceFilesCbo.getValue())+(".csv");
            System.out.println("File to be converted to PDF: "+filename);
            invoiceFile.setFilename(filename);
            if(ReportGenerator.convertCsvToPdf(invoiceFile)){
                Label successLabel = new Label("File conversion success!\n\nCheck file in 'output' directory.");
                successLabel.setStyle("-fx-font-size: 20; -fx-text-fill: #27ae60; " +
                        "-fx-font-weight: bold;");
                mainBox.getChildren().clear();
                mainBox.getChildren().addAll(successLabel, invoiceFilesCbo, welcomeLabel, welcomeNote);
            }
            else{
                Label failureLabel = new Label("File conversion failed! Check logs.");
                failureLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                mainBox.getChildren().clear();
                mainBox.getChildren().addAll(failureLabel, invoiceFilesCbo, welcomeLabel, welcomeNote);
            }
        });

        // Set stage and scene
        Scene scene = new Scene(mainBox, 900, 650);
        stage.setTitle("Invoice Management System");
        stage.setScene(scene);
        stage.show();
    }

    // Summary calculations for numeric values-Quantity, Price, total Paid and Unpaid
    private void updateSummary(List<Invoice> invoiceDataList, Label totalQtyLabel,
                               Label totalPriceLabel, Label totalPaidLabel, Label totalUnpaidLabel) {
        // quantity summary
        int totalQty = invoiceDataList.stream().mapToInt(Invoice::getQuantity).sum();
        totalQtyLabel.setText("Total Quantity: "+totalQty);
        // price summary
        double totalPrice = invoiceDataList.stream().mapToDouble(Invoice::getPrice).sum();
        totalPriceLabel.setText(String.format("Total Price: €%.2f", totalPrice));

        // paid summary
        double totalPaid = invoiceDataList.stream()
                .filter(inv -> inv.getStatus() == PaymentStatus.PAID)
                .mapToDouble(Invoice::getPrice)
                .sum();
        totalPaidLabel.setText(String.format("Total Paid: €%.2f", totalPaid));

        // unpaid summary
        double totalUnpaid = invoiceDataList.stream()
                .filter(inv -> inv.getStatus() == PaymentStatus.UNPAID)
                .mapToDouble(Invoice::getPrice)
                .sum();
        totalUnpaidLabel.setText(String.format("Total Unpaid: €%.2f", totalUnpaid));
    }
}