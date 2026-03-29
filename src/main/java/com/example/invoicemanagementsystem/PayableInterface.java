package com.example.invoicemanagementsystem;

// Not implemented but will be used in the future development and implementation in Receipt and CreditNote.

interface PayableInterface {
    // TODO define parameters for methods
    double calculateTotals();
    boolean markAsPaid();
    double manageRefund();
}
