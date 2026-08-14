package com.sparkco.lab2_api.features.invoice;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(InvoiceMapper::toDTO)
                .collect(Collectors.toList());
    }

    public InvoiceDTO getInvoiceById(Integer invoiceId) {
        Optional<Invoice> invoice = invoiceRepository.findById(invoiceId);
        return invoice.map(InvoiceMapper::toDTO).orElse(null);
    }

    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {
        Invoice invoice = InvoiceMapper.toEntity(invoiceDTO);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return InvoiceMapper.toDTO(savedInvoice);
    }

    public InvoiceDTO updateInvoice(Integer invoiceId, InvoiceDTO invoiceDTO) {
        Optional<Invoice> existingInvoice = invoiceRepository.findById(invoiceId);
        if (existingInvoice.isPresent()) {
            Invoice invoice = existingInvoice.get();
            invoice.setCustomerId(invoiceDTO.customerId());
            invoice.setInvoiceDate(invoiceDTO.invoiceDate());
            invoice.setBillingAddress(invoiceDTO.billingAddress());
            invoice.setBillingCity(invoiceDTO.billingCity());
            invoice.setBillingState(invoiceDTO.billingState());
            invoice.setBillingCountry(invoiceDTO.billingCountry());
            invoice.setBillingPostalCode(invoiceDTO.billingPostalCode());
            invoice.setTotal(invoiceDTO.total());
            Invoice updatedInvoice = invoiceRepository.save(invoice);
            return InvoiceMapper.toDTO(updatedInvoice);
        }
        return null;
    }

    public boolean deleteInvoice(Integer invoiceId) {
        if (invoiceRepository.existsById(invoiceId)) {
            invoiceRepository.deleteById(invoiceId);
            return true;
        }
        return false;
    }
}
