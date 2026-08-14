package com.sparkco.lab2_api.features.employee;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public EmployeeDTO getEmployeeById(Integer employeeId) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        return employee.map(EmployeeMapper::toDTO).orElse(null);
    }

    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Employee employee = EmployeeMapper.toEntity(employeeDTO);
        Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.toDTO(savedEmployee);
    }

    public EmployeeDTO updateEmployee(Integer employeeId, EmployeeDTO employeeDTO) {
        Optional<Employee> existingEmployee = employeeRepository.findById(employeeId);
        if (existingEmployee.isPresent()) {
            Employee employee = existingEmployee.get();
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
            Employee updatedEmployee = employeeRepository.save(employee);
            return EmployeeMapper.toDTO(updatedEmployee);
        }
        return null;
    }

    public boolean deleteEmployee(Integer employeeId) {
        if (employeeRepository.existsById(employeeId)) {
            employeeRepository.deleteById(employeeId);
            return true;
        }
        return false;
    }
}
