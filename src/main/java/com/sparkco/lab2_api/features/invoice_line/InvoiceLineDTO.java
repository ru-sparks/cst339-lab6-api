package com.sparkco.lab2_api.features.invoice_line;

import java.math.BigDecimal;

public record InvoiceLineDTO(Integer invoiceLineId, Integer invoiceId, Integer trackId, BigDecimal unitPrice,
        Integer quantity) {
}
