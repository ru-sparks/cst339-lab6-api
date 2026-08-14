package com.sparkco.lab2_api.features.invoice_line;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_line")
public class InvoiceLine {

    @Id
    @Column(name = "invoice_line_id")
    private Integer invoiceLineId;
    @Column(name = "invoice_id")
    private Integer invoiceId;
    @Column(name = "track_id")
    private Integer trackId;
    @Column(name = "unit_price")
    private BigDecimal unitPrice;
    @Column(name = "quantity")
    private Integer quantity;

    public Integer getInvoiceLineId() {
        return this.invoiceLineId;
    }

    public void setInvoiceLineId(Integer invoiceLineId) {
        this.invoiceLineId = invoiceLineId;
    }

    public Integer getInvoiceId() {
        return this.invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Integer getTrackId() {
        return this.trackId;
    }

    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "InvoiceLine{" +
                "invoiceLineId=" + invoiceLineId +
                ", invoiceId=" + invoiceId +
                ", trackId=" + trackId +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
                '}';
    }
}
