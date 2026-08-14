package com.sparkco.lab2_api.features.invoice_line;

public class InvoiceLineMapper {

    public static InvoiceLineDTO toDTO(InvoiceLine invoiceLine) {
        if (invoiceLine == null) {
            return null;
        }
        return new InvoiceLineDTO(invoiceLine.getInvoiceLineId(), invoiceLine.getInvoiceId(), invoiceLine.getTrackId(),
                invoiceLine.getUnitPrice(), invoiceLine.getQuantity());
    }

    public static InvoiceLine toEntity(InvoiceLineDTO invoiceLineDTO) {
        if (invoiceLineDTO == null) {
            return null;
        }
        InvoiceLine invoiceLine = new InvoiceLine();
        if (invoiceLineDTO.invoiceLineId() != null) {
            invoiceLine.setInvoiceLineId(invoiceLineDTO.invoiceLineId());
        }
        invoiceLine.setInvoiceId(invoiceLineDTO.invoiceId());
        invoiceLine.setTrackId(invoiceLineDTO.trackId());
        invoiceLine.setUnitPrice(invoiceLineDTO.unitPrice());
        invoiceLine.setQuantity(invoiceLineDTO.quantity());
        return invoiceLine;
    }
}
