package com.sparkco.lab2_api.features.customer;

public record CustomerDTO(Integer customerId, String firstName, String lastName, String company, String address,
        String city, String state, String country, String postalCode, String phone, String fax, String email,
        Integer supportRepId) {
}
