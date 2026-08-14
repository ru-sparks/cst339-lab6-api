package com.sparkco.lab2_api.features.customer;

public class CustomerMapper {

    public static CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }
        return new CustomerDTO(customer.getCustomerId(), customer.getFirstName(), customer.getLastName(),
                customer.getCompany(), customer.getAddress(), customer.getCity(), customer.getState(),
                customer.getCountry(), customer.getPostalCode(), customer.getPhone(), customer.getFax(),
                customer.getEmail(), customer.getSupportRepId());
    }

    public static Customer toEntity(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            return null;
        }
        Customer customer = new Customer();
        if (customerDTO.customerId() != null) {
            customer.setCustomerId(customerDTO.customerId());
        }
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
        return customer;
    }
}
