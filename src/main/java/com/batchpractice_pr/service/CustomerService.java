package com.batchpractice_pr.service;

import com.batchpractice_pr.Entity.Customer;
import com.batchpractice_pr.Repository.Customerrepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private Customerrepo customerRepository;

    // Basic CRUD operations
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(int id) {
        return customerRepository.findById(id);
    }

    public List<Customer> getCustomersByCountry(String country) {
        return customerRepository.findByCountry(country);
    }

    public List<Customer> getCustomersByGender(String gender) {
        return customerRepository.findByGender(gender);
    }

    public List<Customer> searchCustomersByName(String name) {
        return customerRepository.searchByName(name);
    }

    public Page<Customer> getCustomersWithPagination(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    public List<Customer> getCustomersByEmailDomain(String domain) {
        return customerRepository.findByEmailDomain(domain);
    }

    // Statistics
    public CustomerStatistics getCustomerStatistics() {
        List<Object[]> countryStats = customerRepository.countCustomersByCountry();
        long totalCustomers = customerRepository.count();
        long customersWithValidDob = customerRepository.findWithValidDob().size();
        long customersWithFormattedPhones = customerRepository.findWithFormattedPhoneNumbers().size();

        return new CustomerStatistics(totalCustomers, customersWithValidDob, customersWithFormattedPhones, countryStats);
    }

    // Create, Update, Delete
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(int id, Customer customerDetails) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setFirstname(customerDetails.getFirstname());
            customer.setLastname(customerDetails.getLastname());
            customer.setEmail(customerDetails.getEmail());
            customer.setGender(customerDetails.getGender());
            customer.setContactno(customerDetails.getContactno());
            customer.setCountry(customerDetails.getCountry());
            customer.setDob(customerDetails.getDob());
            return customerRepository.save(customer);
        }
        return null;
    }

    public boolean deleteCustomer(int id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Statistics DTO
    public static class CustomerStatistics {
        private final long totalCustomers;
        private final long customersWithValidDob;
        private final long customersWithFormattedPhones;
        private final List<Object[]> countryDistribution;

        public CustomerStatistics(long totalCustomers, long customersWithValidDob,
                                  long customersWithFormattedPhones, List<Object[]> countryDistribution) {
            this.totalCustomers = totalCustomers;
            this.customersWithValidDob = customersWithValidDob;
            this.customersWithFormattedPhones = customersWithFormattedPhones;
            this.countryDistribution = countryDistribution;
        }

        public long getTotalCustomers() { return totalCustomers; }
        public long getCustomersWithValidDob() { return customersWithValidDob; }
        public long getCustomersWithFormattedPhones() { return customersWithFormattedPhones; }
        public List<Object[]> getCountryDistribution() { return countryDistribution; }
    }
}
