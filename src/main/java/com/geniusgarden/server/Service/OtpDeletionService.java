package com.geniusgarden.server.Service;

import com.geniusgarden.server.Repository.OtpRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OtpDeletionService {

    private final OtpRepository otpRepository;

    public OtpDeletionService(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @Async
    public void scheduleOtpDeletion(String otpId, long expiryTimeMillis) {
        try {
            Thread.sleep(expiryTimeMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        otpRepository.deleteById(otpId);
        System.out.println("OTP with ID " + otpId + " deleted after expiry.");
    }
}