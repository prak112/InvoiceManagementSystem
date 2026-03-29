package com.example.invoicemanagementsystem;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Invoice extends Document{
    private SimpleStringProperty clientName;
    private SimpleStringProperty product;
    private SimpleIntegerProperty quantity;
    private SimpleDoubleProperty price;
    private LocalDate dueDate;

    public Invoice(String invoiceId, LocalDate date, PaymentStatus status, String clientName, String product,
                   int quantity, double price, LocalDate dueDate) {
        super(invoiceId, date, status);
        this.clientName = new SimpleStringProperty(clientName);
        this.product = new SimpleStringProperty(product);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
        this.dueDate = dueDate;
    }

    // Getters (required for PropertyValueFactory)
    public String getClientName() { return clientName.get(); }
    public String getProduct() { return product.get(); }
    public int getQuantity() { return quantity.get(); }
    public double getPrice() { return price.get(); }
    public String getDueDate() { return dueDate.toString(); }

    // Property getters (for binding)
    public SimpleStringProperty clientNameProperty() { return clientName; }
    public SimpleStringProperty productProperty() { return product; }
    public SimpleIntegerProperty quantityProperty() { return quantity; }
    public SimpleDoubleProperty priceProperty() { return price; }
    public SimpleStringProperty dueDateProperty() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        SimpleStringProperty stringProperty = new SimpleStringProperty(formatter.format(dueDate));
        return stringProperty;
    }

    // TODO future development - Abstract method implementation
    public String getSummary(){
        String summaryText = "";
        return summaryText;
    }
}
