package com.example.invoicemanagementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

public class CSVHandler {
    private String filename;
    private List<Invoice> invoiceDataList;

    CSVHandler(){ this.filename = "src/main/resources/files/invoice_001.csv"; }
    CSVHandler(String filename){
        this.filename = filename;
    }

    // Getters and Setters
    public void setFilename(String filename){ this.filename = filename; }
    public String getFilename(){ return this.filename; }
    public void setInvoiceDataList(List<Invoice> dataList){ this.invoiceDataList = dataList; }
    public List<Invoice> getInvoiceDataList(){ return this.invoiceDataList; }

    // TableView construction and data population
    public TableView getInvoiceData(){
        // create and set table behavior properties
        TableView<Invoice> table = new TableView<>();
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.prefHeight(7);
        invoiceDataList = new ArrayList<>();

        // Create columns
        TableColumn<Invoice, String> invoiceIdCol = new TableColumn<>("Invoice ID");
        invoiceIdCol.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));

        TableColumn<Invoice, LocalDate> dateCol = new TableColumn<>("Invoice Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("invoiceDate"));

        TableColumn<Invoice, String> clientNameCol = new TableColumn<>("Client Name");
        clientNameCol.setCellValueFactory(cellData -> cellData.getValue().clientNameProperty());
        clientNameCol.setPrefWidth(120);

        TableColumn<Invoice, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(cellData -> cellData.getValue().productProperty());

        TableColumn<Invoice, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());

        TableColumn<Invoice, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());

        TableColumn<Invoice, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(cellData -> cellData.getValue().dueDateProperty());

        TableColumn<Invoice, PaymentStatus> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(ComboBoxTableCell.forTableColumn(
                FXCollections.observableArrayList(PaymentStatus.PAID, PaymentStatus.UNPAID)
        ));
        statusCol.setEditable(true);

        table.getColumns().addAll(invoiceIdCol, dateCol, clientNameCol, productCol, quantityCol, priceCol,
                dueDateCol, statusCol);

        try {
            invoiceDataList = parseCSVToList();  // parsed and accumulated row data
            ObservableList<Invoice> data = FXCollections.observableList(invoiceDataList);
            table.setItems(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return table;
    }

    // Invoice Data Model population from CSV file
    public List<Invoice> parseCSVToList(){
        // Retrieve information from CSV file and populate table
        // Parse CSV file and allocate data specific to its array data types
        final int TOTAL_ROWS = 5;   // TODO need dynamic approach to identify total rows in file
        File readFile = new File(filename);
        try {
            Scanner scanner = new Scanner(readFile);
            String _ = scanner.nextLine();  // skips header row

            // Assign arrays specific to attribute data types of Document and its subclass
            String[] invoiceId = new String[TOTAL_ROWS];
            LocalDate[] invoiceDates = new LocalDate[TOTAL_ROWS];   //String type converted to Date
            String[] clientNames = new String[TOTAL_ROWS];
            String[] productNames = new String[TOTAL_ROWS];
            int[] productQuantity = new int[TOTAL_ROWS];
            double[] productPrices = new double[TOTAL_ROWS];
            LocalDate[] dueDates = new LocalDate[TOTAL_ROWS];
            PaymentStatus[] status = new PaymentStatus[TOTAL_ROWS];
            int row = 0;
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] splitLine = line.split(",");
                invoiceId[row] = splitLine[0];
                invoiceDates[row] = LocalDate.parse(splitLine[1]);
                clientNames[row] = splitLine[3];
                productNames[row] = splitLine[5];
                productQuantity[row] = Integer.parseInt(splitLine[6]);
                productPrices[row] = Double.parseDouble(splitLine[7]);
                dueDates[row] = LocalDate.parse(splitLine[4]);
                status[row] = PaymentStatus.valueOf(splitLine[2]);
                // add parsed variables as a row to invoiceDataList
                invoiceDataList.add(
                        new Invoice(invoiceId[row], invoiceDates[row], status[row], clientNames[row], productNames[row],
                                productQuantity[row], productPrices[row], dueDates[row])
                );
                row++; // increment row count to update object arrays indices
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return invoiceDataList;
    }
}
