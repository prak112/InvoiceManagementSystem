package com.example.invoicemanagementsystem;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

enum PaymentStatus {
    PAID, UNPAID;
}
public abstract class Document {
    // data fields
    private String invoiceId;
    private LocalDate invoiceDate;
    private PaymentStatus status;

    // constructor
    protected Document(String invoiceId, LocalDate invoiceDate, PaymentStatus status){
        this.invoiceId = invoiceId;
        this.invoiceDate = invoiceDate;
        this.status = status;
    }
    // Mutators
    public void setInvoiceId(String invoiceId){ this.invoiceId = invoiceId; }
    public void setInvoiceDate(LocalDate invoiceDate){ this.invoiceDate = invoiceDate; }
    public void setStatus(PaymentStatus status){ this.status = status; }
    // Accessors
    public String getInvoiceId(){ return this.invoiceId; }
    public String getInvoiceDate(){ return this.invoiceDate.toString(); }
    public PaymentStatus getStatus(){ return this.status; }

    // Abstract Methods
    public abstract String getSummary();
}

class Receipt extends Document {
    protected Receipt(String id, LocalDate date, PaymentStatus status) {
        super(id, date, status);
    }

    // TODO future development - PayableInterface.markAsPaid
    public String getSummary(){
        String summaryText = "";
        return summaryText;
    }
}

class CreditNote extends Document {
    protected CreditNote(String id, LocalDate date, PaymentStatus status) {
        super(id, date, status);
    }

    // TODO future development - PayableInterface.manageRefund
    public String getSummary(){
        String summaryText = "";
        return summaryText;
    }
}