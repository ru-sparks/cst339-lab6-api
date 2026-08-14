package com.sparkco.lab2_api.features.employee;

public class EmployeeMapper {

    public static EmployeeDTO toDTO(Employee employee) {
        if (employee == null) {
            return null;
        }
        return new EmployeeDTO(employee.getEmployeeId(), employee.getLastName(), employee.getFirstName(),
                employee.getTitle(), employee.getReportsTo(), employee.getBirthDate(), employee.getHireDate(),
                employee.getAddress(), employee.getCity(), employee.getState(), employee.getCountry(),
                employee.getPostalCode(), employee.getPhone(), employee.getFax(), employee.getEmail());
    }

    public static Employee toEntity(EmployeeDTO employeeDTO) {
        if (employeeDTO == null) {
            return null;
        }
        Employee employee = new Employee();
        if (employeeDTO.employeeId() != null) {
            employee.setEmployeeId(employeeDTO.employeeId());
        }
        employee.setLastName(employeeDTO.lastName());
        employee.setFirstName(employeeDTO.firstName());
        employee.setTitle(employeeDTO.title());
        employee.setReportsTo(employeeDTO.reportsTo());
        employee.setBirthDate(employeeDTO.birthDate());
        employee.setHireDate(employeeDTO.hireDate());
        employee.setAddress(employeeDTO.address());
        employee.setCity(employeeDTO.city());
        employee.setState(employeeDTO.state());
        employee.setCountry(employeeDTO.country());
        employee.setPostalCode(employeeDTO.postalCode());
        employee.setPhone(employeeDTO.phone());
        employee.setFax(employeeDTO.fax());
        employee.setEmail(employeeDTO.email());
        return employee;
    }
}
