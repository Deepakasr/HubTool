package com.tool.hub.repository;

import com.tool.hub.entity.EmailOtp;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailOtpRepo extends JpaRepository<EmailOtp, Long> {
    Optional<EmailOtp> findByEmail(String email);

    void deleteByEmail(String email);
}
