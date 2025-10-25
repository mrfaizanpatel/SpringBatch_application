package com.batchpractice_pr.Controller;

import com.batchpractice_pr.Entity.Customer;
import com.batchpractice_pr.service.CustomerService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job customerProcessingJob;

    // ===== DEBUG & HEALTH ENDPOINTS =====
    @GetMapping("/test")
    public String test() {
        return "CustomerController is working!";
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Spring Batch Practice API is running!");
    }

    // ===== BATCH JOB ENDPOINTS =====
    @PostMapping("/batch/start")
    public ResponseEntity<String> startBatchJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(customerProcessingJob, params);

            return ResponseEntity.ok("Batch job started successfully!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to start batch job: " + e.getMessage());
        }
    }

    // ===== CUSTOMER CRUD ENDPOINTS (Only methods from Service) =====

    // Get all customers
    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get customer by ID
    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable int id) {
        try {
            Optional<Customer> customer = customerService.getCustomerById(id);
            return customer.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get customers by country
    @GetMapping("/customers/country/{country}")
    public ResponseEntity<List<Customer>> getCustomersByCountry(@PathVariable String country) {
        try {
            List<Customer> customers = customerService.getCustomersByCountry(country);
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get customers by gender
    @GetMapping("/customers/gender/{gender}")
    public ResponseEntity<List<Customer>> getCustomersByGender(@PathVariable String gender) {
        try {
            List<Customer> customers = customerService.getCustomersByGender(gender);
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Search customers by name
    @GetMapping("/customers/search")
    public ResponseEntity<List<Customer>> searchCustomersByName(@RequestParam String name) {
        try {
            List<Customer> customers = customerService.searchCustomersByName(name);
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get customers with pagination
    @GetMapping("/customers/page")
    public ResponseEntity<Page<Customer>> getCustomersWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Customer> customers = customerService.getCustomersWithPagination(pageable);
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get customers by email domain
    @GetMapping("/customers/domain/{domain}")
    public ResponseEntity<List<Customer>> getCustomersByEmailDomain(@PathVariable String domain) {
        try {
            List<Customer> customers = customerService.getCustomersByEmailDomain(domain);
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===== STATISTICS ENDPOINTS =====

    // Get customer statistics
    @GetMapping("/customers/statistics")
    public ResponseEntity<CustomerService.CustomerStatistics> getCustomerStatistics() {
        try {
            CustomerService.CustomerStatistics statistics = customerService.getCustomerStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===== CREATE, UPDATE, DELETE ENDPOINTS =====

    // Create new customer
    @PostMapping("/customers")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        try {
            Customer createdCustomer = customerService.createCustomer(customer);
            return ResponseEntity.ok(createdCustomer);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Update customer
    @PutMapping("/customers/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable int id, @RequestBody Customer customerDetails) {
        try {
            Customer updatedCustomer = customerService.updateCustomer(id, customerDetails);
            if (updatedCustomer != null) {
                return ResponseEntity.ok(updatedCustomer);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Delete customer by ID
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable int id) {
        try {
            boolean deleted = customerService.deleteCustomer(id);
            if (deleted) {
                return ResponseEntity.ok("Customer deleted successfully");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting customer");
        }
    }
}
