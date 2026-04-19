package com.ek.app.customer.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ek.app.customer.entity.Customer;
import com.ek.app.customer.repository.CustomerRepository;
import com.ek.app.customer.service.dto.CustomerCreateRequest;
import com.ek.app.customer.service.dto.CustomerResponse;
import com.ek.app.customer.service.dto.CustomerSummaryResponse;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public CustomerResponse create(CustomerCreateRequest request) {
        Customer customer = new Customer();
        customer.setCpId(request.getCpId());
        customer.setName(request.getName());
        customer.setContactName(request.getContactName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setGstin(request.getGstin());
        customer.setPan(request.getPan());
        customer.setAddressLine1(request.getAddressLine1());
        customer.setAddressLine2(request.getAddressLine2());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setPincode(request.getPincode());
        customer.setCountry(request.getCountry());
        customer.setArea(request.getArea());
        customer.setLatitude(request.getLatitude());
        customer.setLongitude(request.getLongitude());
        return toResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerSummaryResponse> listAll() {
        return customerRepository.findAll().stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + id));
        return toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerSummaryResponse> search(String query) {
        String safeQuery = query == null ? "" : query.trim();
        if (safeQuery.isBlank()) {
            return listAll();
        }
        return customerRepository.search(safeQuery).stream()
                .map(this::toSummary)
                .toList();
    }

    private CustomerSummaryResponse toSummary(Customer customer) {
        return new CustomerSummaryResponse(customer.getId(), customer.getCpId(), customer.getName(), customer.getGstin());
    }

    private CustomerResponse toResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setCpId(customer.getCpId());
        response.setName(customer.getName());
        response.setContactName(customer.getContactName());
        response.setPhone(customer.getPhone());
        response.setEmail(customer.getEmail());
        response.setGstin(customer.getGstin());
        response.setPan(customer.getPan());
        response.setAddressLine1(customer.getAddressLine1());
        response.setAddressLine2(customer.getAddressLine2());
        response.setCity(customer.getCity());
        response.setState(customer.getState());
        response.setPincode(customer.getPincode());
        response.setCountry(customer.getCountry());
        response.setArea(customer.getArea());
        response.setLatitude(customer.getLatitude());
        response.setLongitude(customer.getLongitude());
        response.setCreatedAt(customer.getCreatedAt());
        response.setUpdatedAt(customer.getUpdatedAt());
        return response;
    }
}
