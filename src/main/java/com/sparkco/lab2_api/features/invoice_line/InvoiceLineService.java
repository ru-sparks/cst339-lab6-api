package com.sparkco.lab2_api.features.invoice_line;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceLineService {

    private final InvoiceLineRepository invoiceLineRepository;

    public InvoiceLineService(InvoiceLineRepository invoiceLineRepository) {
        this.invoiceLineRepository = invoiceLineRepository;
    }

    public List<InvoiceLineDTO> getAllInvoiceLines() {
        return invoiceLineRepository.findAll().stream()
                .map(InvoiceLineMapper::toDTO)
                .collect(Collectors.toList());
    }

    public InvoiceLineDTO getInvoiceLineById(Integer invoiceLineId) {
        Optional<InvoiceLine> invoiceLine = invoiceLineRepository.findById(invoiceLineId);
        return invoiceLine.map(InvoiceLineMapper::toDTO).orElse(null);
    }

    public InvoiceLineDTO createInvoiceLine(InvoiceLineDTO invoiceLineDTO) {
        InvoiceLine invoiceLine = InvoiceLineMapper.toEntity(invoiceLineDTO);
        InvoiceLine savedInvoiceLine = invoiceLineRepository.save(invoiceLine);
        return InvoiceLineMapper.toDTO(savedInvoiceLine);
    }

    public InvoiceLineDTO updateInvoiceLine(Integer invoiceLineId, InvoiceLineDTO invoiceLineDTO) {
        Optional<InvoiceLine> existingInvoiceLine = invoiceLineRepository.findById(invoiceLineId);
        if (existingInvoiceLine.isPresent()) {
            InvoiceLine invoiceLine = existingInvoiceLine.get();
            invoiceLine.setInvoiceId(invoiceLineDTO.invoiceId());
            invoiceLine.setTrackId(invoiceLineDTO.trackId());
            invoiceLine.setUnitPrice(invoiceLineDTO.unitPrice());
            invoiceLine.setQuantity(invoiceLineDTO.quantity());
            InvoiceLine updatedInvoiceLine = invoiceLineRepository.save(invoiceLine);
            return InvoiceLineMapper.toDTO(updatedInvoiceLine);
        }
        return null;
    }

    public boolean deleteInvoiceLine(Integer invoiceLineId) {
        if (invoiceLineRepository.existsById(invoiceLineId)) {
            invoiceLineRepository.deleteById(invoiceLineId);
            return true;
        }
        return false;
    }
}
