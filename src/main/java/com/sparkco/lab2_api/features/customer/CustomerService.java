package com.sparkco.lab2_api.features.customer;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(CustomerMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(Integer customerId) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        return customer.map(CustomerMapper::toDTO).orElse(null);
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        Customer customer = CustomerMapper.toEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return CustomerMapper.toDTO(savedCustomer);
    }

    public CustomerDTO updateCustomer(Integer customerId, CustomerDTO customerDTO) {
        Optional<Customer> existingCustomer = customerRepository.findById(customerId);
        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();
            customer.setFirstName(customerDTO.firstName());
            customer.setLastName(customerDTO.lastName());
            customer.setCompany(customerDTO.company());
            customer.setAddress(customerDTO.address());
            customer.setCity(customerDTO.city());
            customer.setState(customerDTO.state());
            customer.setCountry(customerDTO.country());
            customer.setPostalCode(customerDTO.postalCode());
            customer.setPhone(customerDTO.phone());
            customer.setFax(customerDTO.fax());
            customer.setEmail(customerDTO.email());
            customer.setSupportRepId(customerDTO.supportRepId());
            Customer updatedCustomer = customerRepository.save(customer);
            return CustomerMapper.toDTO(updatedCustomer);
        }
        return null;
    }

    public boolean deleteCustomer(Integer customerId) {
        if (customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
            return true;
        }
        return false;
    }
}
