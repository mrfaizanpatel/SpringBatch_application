package com.batchpractice_pr.Repository;

import com.batchpractice_pr.Entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface Customerrepo extends JpaRepository<Customer, Integer> {
    // Find customers by country
    List<Customer> findByCountry(String country);

    // Find customers by gender
    List<Customer> findByGender(String gender);

    // Find customers by email domain
    @Query("SELECT c FROM Customer c WHERE c.email LIKE %:domain%")
    List<Customer> findByEmailDomain(String domain);

    // Find customers with formatted phone numbers
    @Query("SELECT c FROM Customer c WHERE c.contactno LIKE '(%) %-%'")
    List<Customer> findWithFormattedPhoneNumbers();

    // Count customers by country
    @Query("SELECT c.country, COUNT(c) FROM Customer c GROUP BY c.country")
    List<Object[]> countCustomersByCountry();

    // Search customers by name
    @Query("SELECT c FROM Customer c WHERE LOWER(c.firstname) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(c.lastname) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Customer> searchByName(String name);

    // Find customers by first name
    List<Customer> findByFirstnameContainingIgnoreCase(String firstname);

    // Find customers by last name
    List<Customer> findByLastnameContainingIgnoreCase(String lastname);

    // Find customers by exact email
    Customer findByEmail(String email);

    // Find customers with non-empty date of birth
    @Query("SELECT c FROM Customer c WHERE c.dob IS NOT NULL AND c.dob != ''")
    List<Customer> findWithValidDob();

    // Count customers with formatted phone numbers
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.contactno LIKE '(%) %-%'")
    Long countWithFormattedPhoneNumbers();

    // Get unique countries
    @Query("SELECT DISTINCT c.country FROM Customer c ORDER BY c.country")
    List<String> findDistinctCountries();

    // Get unique genders
    @Query("SELECT DISTINCT c.gender FROM Customer c ORDER BY c.gender")
    List<String> findDistinctGenders();

    // Find customers by country and gender
    List<Customer> findByCountryAndGender(String country, String gender);

    // Find customers with phone numbers containing specific pattern
    @Query("SELECT c FROM Customer c WHERE c.contactno LIKE %:pattern%")
    List<Customer> findByContactnoContaining(String pattern);

    // Pagination support
    Page<Customer> findAll(Pageable pageable);

    // Pagination by country
    Page<Customer> findByCountry(String country, Pageable pageable);

    // Pagination by gender
    Page<Customer> findByGender(String gender, Pageable pageable);

    // Check if email exists
    boolean existsByEmail(String email);

    // Count by country
    Long countByCountry(String country);

    // Count by gender
    Long countByGender(String gender);

    // Delete by email
    void deleteByEmail(String email);
}
