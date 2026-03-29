## Milestone 0
- Plan for minimalistic features and UI activity. Diverge from actual [PLAN](PLAN.md) to minimize class structure and workflow if needed.
- Functionalities include:
    * `InvoiceManager` imports `Document`, `Product` classes for generating an overview.
    * Overview is provided for all the listed invoices in the local directory file system.
    * These invoices are visualized as a table with details about each invoice and an overall quick summary of paid, unpaid counts.
    * User can track their customer payments and generate PDF versions of the CSV invoices.

## Milestone 1
- List all AI generated CSV files as a dropdown list in the UI.

## Milestone 2
- Build and populate a table with invoice CSV file data selected from the dropdown list.

## Milestone 3
- Update the table dynamically without adding extra tables.
- Add a home page instead of displaying empty table when no invoice file is selected.

## Milestone 4
- Add a summary row instead of a `Dashboard`pane with total summaries for quantifiable categories.

## Milestone 5
- Figure out how convert tabulated CSV file into PDF without using external libraries.
- Use Copilot Agent to build the required library using PDF syntax to write data from CSV to PDF.

