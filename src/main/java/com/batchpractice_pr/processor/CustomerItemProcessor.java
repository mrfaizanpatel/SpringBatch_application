package com.batchpractice_pr.processor;

import com.batchpractice_pr.Entity.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomerItemProcessor implements ItemProcessor<Customer, Customer> {
    private static final Logger log = LoggerFactory.getLogger(CustomerItemProcessor.class);

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("yyyyMMdd")
    };

    @Override
    public Customer process(final Customer customer) throws Exception {
        // Clean and transform data
        final String firstname = cleanName(customer.getFirstname());
        final String lastname = cleanName(customer.getLastname());
        final String email = customer.getEmail().toLowerCase().trim();
        final String gender = capitalizeFirst(customer.getGender());
        final String contactno = formatContactNumber(customer.getContactno());
        final String country = capitalizeWords(customer.getCountry());
        final String dob = customer.getDob(); // Keep original DOB as string

        // Create transformed customer - using your exact constructor
        final Customer transformedCustomer = new Customer(
                customer.getId(), // Keep original ID from CSV
                firstname,
                lastname,
                email,
                gender,
                contactno,
                country,
                dob
        );

        log.info("Processing: {} {} -> {} {}",
                customer.getFirstname(), customer.getLastname(),
                transformedCustomer.getFirstname(), transformedCustomer.getLastname());

        return transformedCustomer;
    }

    private String cleanName(String name) {
        if (name == null) return "";
        return name.trim()
                .replaceAll("\\s+", " ")
                .toUpperCase();
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.trim().isEmpty()) return "Unknown";
        String cleaned = text.trim();
        return cleaned.substring(0, 1).toUpperCase() + cleaned.substring(1).toLowerCase();
    }

    private String capitalizeWords(String text) {
        if (text == null || text.trim().isEmpty()) return "Unknown";
        String[] words = text.trim().split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return result.toString().trim();
    }

    private String formatContactNumber(String contactno) {
        if (contactno == null || contactno.trim().isEmpty()) {
            return "N/A";
        }

        String digits = contactno.replaceAll("\\D", "");

        if (digits.length() == 10) {
            return "(" + digits.substring(0, 3) + ") " + digits.substring(3, 6) + "-" + digits.substring(6);
        } else if (digits.length() == 11 && digits.startsWith("1")) {
            return "+1 (" + digits.substring(1, 4) + ") " + digits.substring(4, 7) + "-" + digits.substring(7);
        }

        return contactno.trim();
    }

    // Optional: If you want to parse dates later, you can use this method
    private LocalDate parseDateOfBirth(String dob) {
        if (dob == null || dob.trim().isEmpty()) {
            return null;
        }

        String cleanDob = dob.trim();

        // Try to parse as Excel serial number (like 32521)
        try {
            double excelSerial = Double.parseDouble(cleanDob);
            if (excelSerial > 0) {
                // Excel date serial number (days since 1900-01-01)
                LocalDate date = LocalDate.of(1900, 1, 1).plusDays((long) excelSerial - 2);
                return date;
            }
        } catch (NumberFormatException e) {
            // Not an Excel serial number, try other formats
        }

        // Try different date formats
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(cleanDob, formatter);
            } catch (DateTimeParseException e) {
                // Try next format
            }
        }

        log.warn("Could not parse date: {}", dob);
        return null;
    }
}
