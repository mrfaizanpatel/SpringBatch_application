package com.batchpractice_pr.listener;

import com.batchpractice_pr.Entity.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {
    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;

    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("!!! JOB STARTING! Processing customer data from CSV...");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB COMPLETED! Verifying results...");

            // Updated SQL query to match your table and column names
            String sql = "SELECT id, firstname, lastname, email, gender, contactno, country, dob FROM customer_info ORDER BY id";

            jdbcTemplate.query(sql, (rs, row) ->
                    new Customer(
                            rs.getInt("id"),
                            rs.getString("firstname"),
                            rs.getString("lastname"),
                            rs.getString("email"),
                            rs.getString("gender"),
                            rs.getString("contactno"),
                            rs.getString("country"),
                            rs.getString("dob"))
            ).forEach(customer ->
                    log.info("Found: {} {} - {} - {} - {}",
                            customer.getId(),
                            customer.getFirstname(),
                            customer.getLastname(),
                            customer.getCountry(),
                            customer.getContactno())
            );

            // Get statistics - updated table name
            Long totalCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM customer_info", Long.class);

            // Since dob is stored as String, count non-null and non-empty values
            Long validDobCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM customer_info WHERE dob IS NOT NULL AND dob != ''",
                    Long.class
            );

            log.info("=== BATCH PROCESSING SUMMARY ===");
            log.info("Total records processed: {}", totalCount);
            log.info("Records with valid date of birth: {}", validDobCount);
            log.info("Records with missing date of birth: {}", totalCount - validDobCount);

        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("!!! JOB FAILED! Check the logs for details");
            jobExecution.getAllFailureExceptions().forEach(ex ->
                    log.error("Failure reason: {}", ex.getMessage(), ex)
            );
        }
    }

}
