package com.sparkco.lab2_api.features.invoice_line;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/invoice-lines")
public class InvoiceLineController {

    private final InvoiceLineService invoiceLineService;

    public InvoiceLineController(InvoiceLineService invoiceLineService) {
        this.invoiceLineService = invoiceLineService;
    }

    @GetMapping
    public List<InvoiceLineDTO> getAllInvoiceLines() {
        return invoiceLineService.getAllInvoiceLines();
    }

    @GetMapping("/{invoiceLineId}")
    public ResponseEntity<InvoiceLineDTO> getInvoiceLineById(@PathVariable Integer invoiceLineId) {
        InvoiceLineDTO invoiceLineDTO = invoiceLineService.getInvoiceLineById(invoiceLineId);
        return invoiceLineDTO != null ? ResponseEntity.ok(invoiceLineDTO) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceLineDTO createInvoiceLine(@RequestBody InvoiceLineDTO invoiceLineDTO) {
        return invoiceLineService.createInvoiceLine(invoiceLineDTO);
    }

    @PutMapping("/{invoiceLineId}")
    public ResponseEntity<InvoiceLineDTO> updateInvoiceLine(@PathVariable Integer invoiceLineId,
            @RequestBody InvoiceLineDTO invoiceLineDTO) {
        InvoiceLineDTO updatedInvoiceLine = invoiceLineService.updateInvoiceLine(invoiceLineId, invoiceLineDTO);
        return updatedInvoiceLine != null ? ResponseEntity.ok(updatedInvoiceLine) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{invoiceLineId}")
    public ResponseEntity<Void> deleteInvoiceLine(@PathVariable Integer invoiceLineId) {
        if (invoiceLineService.deleteInvoiceLine(invoiceLineId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
