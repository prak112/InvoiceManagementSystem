package com.example.invoicemanagementsystem;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO debug incomplete file generation. only InvoiceID column prints.

public class ReportGenerator {
    private static final String OUTPUT_DIR = "src/main/resources/output/";

    public static boolean convertCsvToPdf(CSVHandler csvHandler) {
        if (csvHandler == null) {
            throw new IllegalArgumentException("csvHandler must not be null");
        }

        List<Invoice> dataList = csvHandler.getInvoiceDataList();
        if (dataList == null || dataList.isEmpty()) {
            return false;
        }

        List<String[]> stringList = toPdfRows(dataList);
        String pdfFilePath = buildOutputFilePath(csvHandler);

        PDFGenerator pdfGenerator = new PDFGenerator(stringList, pdfFilePath);
        return pdfGenerator.generatePDF();
    }

    /**
     * Converts typed Invoice data to String rows expected by PDFGenerator.
     * Header names are aligned with COLUMN_WIDTHS in PDFGenerator.
     */
    private static List<String[]> toPdfRows(List<Invoice> invoices) {
        List<String[]> rows = new ArrayList<>();

        rows.add(new String[]{
                "invoiceId",
                "invoiceDate",
                "status",
                "clientName",
                "dueDate",
                "productName",
                "quantity",
                "unitPrice"
        });

        DecimalFormat priceFormat = new DecimalFormat("0.00");

        for (Invoice invoice : invoices) {
            if (invoice == null) {
                continue;
            }

            rows.add(new String[]{
                    safe(invoice.getInvoiceId()),
                    safe(invoice.getInvoiceDate()),      // Document getter returns String
                    invoice.getStatus() == null ? "" : invoice.getStatus().name(),
                    safe(invoice.getClientName()),
                    safe(invoice.getDueDate()),
                    safe(invoice.getProduct()),
                    String.valueOf(invoice.getQuantity()),
                    priceFormat.format(invoice.getPrice())
            });
        }

        return rows;
    }

    private static String buildOutputFilePath(CSVHandler csvHandler) {
        String baseName = "invoice_report";
        String csvPath = csvHandler.getFilename();

        if (csvPath != null && !csvPath.isBlank()) {
            File source = new File(csvPath);
            String name = source.getName();
            int dot = name.lastIndexOf('.');
            baseName = (dot > 0) ? name.substring(0, dot) : name;
        }

        return OUTPUT_DIR + baseName + ".pdf";
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}

class PDFGenerator {
    private List<String[]> csvData;
    private String outputPath;
    private List<Integer> objectOffsets;
    private StringBuilder pdfContent;

    // PDF Constants
    private static final String PDF_HEADER = "%PDF-1.4\n";
    private static final float PAGE_WIDTH = 612;
    private static final float PAGE_HEIGHT = 792;
    private static final float MARGIN = 20;
    private static final float FONT_SIZE = 10;
    private static final float ROW_HEIGHT = 20;
    private static final float HEADER_HEIGHT = 25;

    // Column widths (in points)
    private static final Map<String, Float> COLUMN_WIDTHS = new HashMap<>();

    static {
        COLUMN_WIDTHS.put("invoiceId", 50f);
        COLUMN_WIDTHS.put("invoiceDate", 70f);
        COLUMN_WIDTHS.put("status", 50f);
        COLUMN_WIDTHS.put("clientName", 80f);
        COLUMN_WIDTHS.put("dueDate", 70f);
        COLUMN_WIDTHS.put("productName", 120f);
        COLUMN_WIDTHS.put("quantity", 50f);
        COLUMN_WIDTHS.put("unitPrice", 60f);
    }

    public PDFGenerator(List<String[]> csvData, String outputPath) {
        this.csvData = csvData;
        this.outputPath = outputPath;
        this.objectOffsets = new ArrayList<>();
        this.pdfContent = new StringBuilder();
    }

    /**
     * Main method to orchestrate PDF generation
     */
    public boolean generatePDF() {
        try {
            pdfContent.append(PDF_HEADER);

            // Create objects and track offsets
            writeCatalogObject();           // Object 1
            writePagesObject();             // Object 2
            writePageObject();              // Object 3
            writeContentStreamObject();     // Object 4
            writeFontObject();              // Object 5
            writeResourcesObject();         // Object 6

            // Write cross-reference table and trailer
            int xrefOffset = pdfContent.length();
            writeXref();
            writeTrailer(xrefOffset);

            // Write to file
            writeToFile();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Object 1: Catalog (root object)
     */
    private void writeCatalogObject() {
        recordOffset();
        pdfContent.append("1 0 obj\n");
        pdfContent.append("<<\n");
        pdfContent.append("/Type /Catalog\n");
        pdfContent.append("/Pages 2 0 R\n");
        pdfContent.append(">>\n");
        pdfContent.append("endobj\n\n");
    }

    /**
     * Object 2: Pages (container for all pages)
     */
    private void writePagesObject() {
        recordOffset();
        pdfContent.append("2 0 obj\n");
        pdfContent.append("<<\n");
        pdfContent.append("/Type /Pages\n");
        pdfContent.append("/Kids [3 0 R]\n");
        pdfContent.append("/Count 1\n");
        pdfContent.append(">>\n");
        pdfContent.append("endobj\n\n");
    }

    /**
     * Object 3: Page (single page definition)
     */
    private void writePageObject() {
        recordOffset();
        pdfContent.append("3 0 obj\n");
        pdfContent.append("<<\n");
        pdfContent.append("/Type /Page\n");
        pdfContent.append("/Parent 2 0 R\n");
        pdfContent.append("/MediaBox [0 0 ").append((int) PAGE_WIDTH).append(" ").append((int) PAGE_HEIGHT).append("]\n");
        pdfContent.append("/Contents 4 0 R\n");
        pdfContent.append("/Resources 6 0 R\n");
        pdfContent.append(">>\n");
        pdfContent.append("endobj\n\n");
    }

    /**
     * Object 4: Content Stream (actual drawing commands for the table)
     */
    private void writeContentStreamObject() {
        recordOffset();

        // Generate content stream
        String contentStream = createContentStream();

        pdfContent.append("4 0 obj\n");
        pdfContent.append("<<\n");
        pdfContent.append("/Length ").append(contentStream.length()).append("\n");
        pdfContent.append(">>\n");
        pdfContent.append("stream\n");
        pdfContent.append(contentStream);
        pdfContent.append("\nendstream\n");
        pdfContent.append("endobj\n\n");
    }

    /**
     * Object 5: Font definition
     */
    private void writeFontObject() {
        recordOffset();
        pdfContent.append("5 0 obj\n");
        pdfContent.append("<<\n");
        pdfContent.append("/Type /Font\n");
        pdfContent.append("/Subtype /Type1\n");
        pdfContent.append("/BaseFont /Helvetica\n");
        pdfContent.append(">>\n");
        pdfContent.append("endobj\n\n");
    }

    /**
     * Object 6: Resources (references to fonts and other resources)
     */
    private void writeResourcesObject() {
        recordOffset();
        pdfContent.append("6 0 obj\n");
        pdfContent.append("<<\n");
        pdfContent.append("/Font <<\n");
        pdfContent.append("/F1 5 0 R\n");
        pdfContent.append(">>\n");
        pdfContent.append(">>\n");
        pdfContent.append("endobj\n\n");
    }

    /**
     * Generate the content stream with table drawing commands
     */
    private String createContentStream() {
        StringBuilder stream = new StringBuilder();

        if (csvData == null || csvData.isEmpty()) {
            return stream.toString();
        }

        // Get headers from first row
        String[] headers = csvData.get(0);

        // Calculate column positions
        List<Float> columnPositions = calculateColumnPositions(headers);

        // Start drawing
        float yPosition = PAGE_HEIGHT - MARGIN - HEADER_HEIGHT;

        // Draw header row
        drawTableRow(stream, headers, columnPositions, yPosition, true);
        yPosition -= HEADER_HEIGHT;

        // Draw data rows
        for (int i = 1; i < csvData.size(); i++) {
            String[] row = csvData.get(i);
            drawTableRow(stream, row, columnPositions, yPosition, false);
            yPosition -= ROW_HEIGHT;

            // Check if we need a new page (simple pagination)
            if (yPosition < MARGIN + ROW_HEIGHT) {
                // For now, we'll just stop. In production, create a new page.
                break;
            }
        }

        return stream.toString();
    }

    /**
     * Draw a single table row (header or data)
     */
    private void drawTableRow(StringBuilder stream, String[] rowData, List<Float> columnPositions,
                              float yPosition, boolean isHeader) {

        // Draw cell borders (rectangles)
        for (int i = 0; i < columnPositions.size() - 1; i++) {
            float x = columnPositions.get(i);
            float width = columnPositions.get(i + 1) - x;
            float height = isHeader ? HEADER_HEIGHT : ROW_HEIGHT;

            // Draw rectangle: x y width height re
            stream.append(x).append(" ").append(yPosition - height).append(" ")
                    .append(width).append(" ").append(height).append(" re\n");
            stream.append("S\n"); // Stroke (outline)
        }

        // Draw text
        stream.append("BT\n"); // Begin text
        stream.append("/F1 ").append((int) FONT_SIZE).append(" Tf\n"); // Set font

        for (int i = 0; i < rowData.length && i < columnPositions.size() - 1; i++) {
            float x = columnPositions.get(i) + 5; // 5pt padding
            float y = yPosition - (isHeader ? HEADER_HEIGHT : ROW_HEIGHT) + 7; // Vertical centering

            stream.append("1 0 0 1 ").append(x).append(" ").append(y).append(" Tm\n"); // Absolute text position

            String text = rowData[i];
            if (text == null) text = "";

            // Escape special characters in PDF
            text = escapePDFText(text);

            // Truncate if too long
            if (text.length() > 20) {
                text = text.substring(0, 17) + "...";
            }

            stream.append("(").append(text).append(") Tj\n"); // Show text
        }

        stream.append("ET\n"); // End text
    }

    /**
     * Calculate x-positions for each column based on headers
     */
    private List<Float> calculateColumnPositions(String[] headers) {
        List<Float> positions = new ArrayList<>();
        float currentX = MARGIN;
        positions.add(currentX);

        for (String header : headers) {
            float width = COLUMN_WIDTHS.getOrDefault(header, 60f);
            currentX += width;
            positions.add(currentX);
        }

        return positions;
    }

    /**
     * Escape special characters for PDF text
     */
    private String escapePDFText(String text) {
        return text.replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }

    /**
     * Record the current byte offset for cross-reference table
     */
    private void recordOffset() {
        objectOffsets.add(pdfContent.length());
    }

    /**
     * Write the cross-reference table
     */
    private void writeXref() {
        pdfContent.append("xref\n");
        pdfContent.append("0 ").append(objectOffsets.size() + 1).append("\n");
        pdfContent.append("0000000000 65535 f\n"); // Object 0 (always free)

        for (Integer offset : objectOffsets) {
            // Format: 10-digit offset, space, 5-digit generation, space, flag, CRLF
            pdfContent.append(String.format("%010d 00000 n\n", offset));
        }
    }

    /**
     * Write the trailer (points to root object and xref)
     */
    private void writeTrailer(int xrefOffset) {
        pdfContent.append("trailer\n");
        pdfContent.append("<<\n");
        pdfContent.append("/Size ").append(objectOffsets.size() + 1).append("\n");
        pdfContent.append("/Root 1 0 R\n");
        pdfContent.append(">>\n");
        pdfContent.append("startxref\n");
        pdfContent.append(xrefOffset).append("\n");
        pdfContent.append("%%EOF\n");
    }

    /**
     * Write PDF content to file
     */
    private void writeToFile() throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputPath);
             OutputStreamWriter writer = new OutputStreamWriter(fos)) {
            writer.write(pdfContent.toString());
            writer.flush();
        }
    }

    /**
     * Get file size (useful for debugging)
     */
    public long getFileSize() {
        File file = new File(outputPath);
        return file.exists() ? file.length() : 0;
    }
}
