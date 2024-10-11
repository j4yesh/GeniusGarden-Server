package com.geniusgarden.server.Repository;

import com.geniusgarden.server.Model.Otp;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OtpRepository extends MongoRepository<Otp,String> {
    Optional<Otp> findByOtp(String otp);
}
