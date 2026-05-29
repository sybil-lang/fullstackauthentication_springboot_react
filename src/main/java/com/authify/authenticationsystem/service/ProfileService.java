package com.authify.authenticationsystem.service;

import com.authify.authenticationsystem.io.ProfileRequest;
import com.authify.authenticationsystem.io.ProfileResponse;
import com.authify.authenticationsystem.io.OtpResponse;

public interface ProfileService {

    ProfileResponse createProfile(ProfileRequest profileRequest);;
    ProfileResponse getProfile(String email);

    void sendResetOtp(String email);

    void resetPassword(String email,String otp,String newPassword);

    OtpResponse sendOtp(String email);
    void verifyOtp(String email,String otp);


}
