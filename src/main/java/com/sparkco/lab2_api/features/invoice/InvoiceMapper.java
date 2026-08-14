package com.sparkco.lab2_api.features.invoice;

public class InvoiceMapper {

    public static InvoiceDTO toDTO(Invoice invoice) {
        if (invoice == null) {
            return null;
        }
        return new InvoiceDTO(invoice.getInvoiceId(), invoice.getCustomerId(), invoice.getInvoiceDate(),
                invoice.getBillingAddress(), invoice.getBillingCity(), invoice.getBillingState(),
                invoice.getBillingCountry(), invoice.getBillingPostalCode(), invoice.getTotal());
    }

    public static Invoice toEntity(InvoiceDTO invoiceDTO) {
        if (invoiceDTO == null) {
            return null;
        }
        Invoice invoice = new Invoice();
        if (invoiceDTO.invoiceId() != null) {
            invoice.setInvoiceId(invoiceDTO.invoiceId());
        }
        invoice.setCustomerId(invoiceDTO.customerId());
        invoice.setInvoiceDate(invoiceDTO.invoiceDate());
        invoice.setBillingAddress(invoiceDTO.billingAddress());
        invoice.setBillingCity(invoiceDTO.billingCity());
        invoice.setBillingState(invoiceDTO.billingState());
        invoice.setBillingCountry(invoiceDTO.billingCountry());
        invoice.setBillingPostalCode(invoiceDTO.billingPostalCode());
        invoice.setTotal(invoiceDTO.total());
        return invoice;
    }
}
