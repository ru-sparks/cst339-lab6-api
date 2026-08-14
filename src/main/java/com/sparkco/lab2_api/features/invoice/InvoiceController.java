package com.sparkco.lab2_api.features.invoice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Integer invoiceId) {
        InvoiceDTO invoiceDTO = invoiceService.getInvoiceById(invoiceId);
        return invoiceDTO != null ? ResponseEntity.ok(invoiceDTO) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceDTO createInvoice(@RequestBody InvoiceDTO invoiceDTO) {
        return invoiceService.createInvoice(invoiceDTO);
    }

    @PutMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDTO> updateInvoice(@PathVariable Integer invoiceId,
            @RequestBody InvoiceDTO invoiceDTO) {
        InvoiceDTO updatedInvoice = invoiceService.updateInvoice(invoiceId, invoiceDTO);
        return updatedInvoice != null ? ResponseEntity.ok(updatedInvoice) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Integer invoiceId) {
        if (invoiceService.deleteInvoice(invoiceId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
