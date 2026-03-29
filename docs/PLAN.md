
# Invoice Management System

- The Invoice Management System is designed to help small businesses manage their invoices efficiently. This system 
allows users to edit, and delete invoices, as well as generate reports.

- The original source of truth to the entire system are the CSV files. Data from these files are processed, visualized 
and handled in the system using CSV file read/write operations.
- The invoice files will be a dropdown list. A file is chosen and this displays the invoice information saved in that 
file. The system will allow user to edit, delete, update and visualize the information. 
- The system will give a general overview of how many invoices are paid and unpaid.

- The files are stored locally since this is a JavaFX frontend demo project.
- Java OOP principles, specifically inheritance, polymorphism, and interfaces are used.
- CSV files are auto-generated using AI for random information.

## Preliminary Plan for Class Structure
- `Document`, its subclasses (`Invoice`, `Receipt`, `CreditNote`) and `Product` are Data Models or the _Data Layer_
- `InvoiceManager`, `DashboardPane`, `ReportGenerator` and `CSVHandler` are the _Functional Layer_.

```mermaidjs
flowchart TD
    A[CSV file] <-->|CSVHandler reads and writes, to and from| B(Data Models)
    B <--> C[Functional Layer]
    C <--> |Updates| D[fa:fa-display UI] 
    D --> E[Invoice]
    D --> F[Dashboard]
```


### Superclass:

`Document` - consist of common fields like `invoiceId`, `invoiceDate`, `status` are used across all kinds of documents used for invoicing. And also consists of abstract/non-abstract methods like getSummary.


### Subclasses extending Document:

- `Invoice` - adds client name, list of Product objects, due invoiceDate
- `Receipt` - generated when an invoice is marked paid, adds payment invoiceDate and method
- `CreditNote` - represents a refund or adjustment on an invoice



### Interface Classes:

`Payable` - implemented by Invoice and Receipt which could both consist of methods like `calculateTotal`, `markAsPaid`, etc.



### Standalone Supporting Classes:

- `Product` - Data (name, quantity, price) about each item listed in Invoice.
- `InvoiceManager` - Manages the collection, handles serialization/deserialization.
- `DashboardPane` - Builds monthly revenue BarChart and/or paid-unpaid PieCharts according to Documents data 
  retrieved from InvoiceManager.
- `ReportGenerator` which consists of modules like `PDFGenerator` and its related methods for building a PDF from CSV.
- `CSVHandler` - Processes files in local directory for display and updates.


## Data Handling

ArrayLists store Document objects data such as `Invoices`, `Receipt`, `CreditNote`. This allows for generating a report or a summary later when needed. ArrayLists will be converted to and from .dat files  will use built-in serialization/deserialization (ObjectOutputStream / ObjectInputStream) for saving and loading .dat files from and to ArrayLists. This allows data to retain between sessions.



## UI Features that will be implemented or at least attempted

- For creating UI controls like buttons, text fields, and tables (`javafx.scene.control`).
- For arranging UI components using layouts like VBox, HBox, and GridPane (`javafx.scene.layout`).
- For generating charts and visuals in the dashboard (`javafx.scene.chart`).

