package com.sparkco.lab2_api.features.invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvoiceDTO(Integer invoiceId, Integer customerId, LocalDateTime invoiceDate, String billingAddress,
        String billingCity, String billingState, String billingCountry, String billingPostalCode, BigDecimal total) {
}
