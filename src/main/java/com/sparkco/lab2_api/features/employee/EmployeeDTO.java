package com.sparkco.lab2_api.features.employee;

import java.time.LocalDateTime;

public record EmployeeDTO(Integer employeeId, String lastName, String firstName, String title, Integer reportsTo,
        LocalDateTime birthDate, LocalDateTime hireDate, String address, String city, String state, String country,
        String postalCode, String phone, String fax, String email) {
}
