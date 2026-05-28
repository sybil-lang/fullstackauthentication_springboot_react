package com.authify.authenticationsystem.service;

import com.authify.authenticationsystem.entity.UserEntity;
import com.authify.authenticationsystem.io.ProfileRequest;
import com.authify.authenticationsystem.io.ProfileResponse;
import com.authify.authenticationsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{ 
    
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public ProfileResponse createProfile(ProfileRequest request) {

        UserEntity newProfile = convertToUserEntity(request);

        if(!userRepository.existsByEmail(request.getEmail())) {
            newProfile = userRepository.save(newProfile);
            return convertToProfileResponse(newProfile);
        }

        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");

    }

    @Override
    public ProfileResponse getProfile(String email) {
      UserEntity existingUser= userRepository.findByEmail(email)
               .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

      return convertToProfileResponse(existingUser);
    }

    @Override
    public void sendResetOtp(String email) {

     UserEntity existingEntity=userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


     //Generate 6 digit otp
     String otp=String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));

     //calculate expiry time (current time + 15 minutes in milisecond)

        long expiryTime=System.currentTimeMillis()+(15*60*1000);

        existingEntity.setResetOtp(otp);
        existingEntity.setResetOtpExpireAt(expiryTime);

        //update  the profile/user
        userRepository.save(existingEntity);

        try{
            //Send the reset otp email
            emailService.sendResetOtpEmail(existingEntity.getEmail(),otp);
        }
        catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to send reset OTP email");
        }
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        UserEntity existingEntity=userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if( existingEntity.getResetOtp()==null || !existingEntity.getResetOtp().equals(otp)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reset OTP does not match");
        }

        if(existingEntity.getResetOtpExpireAt()<System.currentTimeMillis()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired");
        }

        existingEntity.setResetOtp(null);
        existingEntity.setPassword(passwordEncoder.encode(newPassword));
        existingEntity.setResetOtpExpireAt(0L);
        userRepository.save(existingEntity);
    }

    private ProfileResponse convertToProfileResponse(UserEntity newProfile) {

        return ProfileResponse.builder()
                .userId(newProfile.getUserId())
                .name(newProfile.getName())
                .email(newProfile.getEmail())
                .isAccountVerified(newProfile.getIsAccountVerified())
                .build();
    }

    private UserEntity convertToUserEntity(ProfileRequest request) {


        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountVerified(false)
                .verifyOtp(null)
                .verifyOtpExpireAt(0L)
                .resetOtp(null)
                .resetOtpExpireAt(0L)
                .build();
    }


    @Override
    public void sendOtp(String email) {

        // Find user by email
        UserEntity existingUser = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );

        if(existingUser.getIsAccountVerified()!=null && existingUser.getIsAccountVerified()){
            return;
        }

        //Generate 6 digit otp
        String otp=String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));

        //calculate expiry time (current time +24 hours in milisecond)

        long expiryTime=System.currentTimeMillis()+(24*60*60*1000);

        existingUser.setVerifyOtp(otp);
        existingUser.setVerifyOtpExpireAt(expiryTime);

         userRepository.save(existingUser);

         try{
             emailService.sendOtpEmail(existingUser.getEmail(),otp);
         }
         catch(Exception e){
             throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to send OTP email");
         }
    }


    @Override
    public void verifyOtp(
            String email,
            String otp
    ) {

        // Find user by email
        UserEntity existingUser = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );

        if(existingUser.getVerifyOtp()==null || !existingUser.getVerifyOtp().equals(otp)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }

        if(existingUser.getVerifyOtpExpireAt()<System.currentTimeMillis()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired");
        }

        existingUser.setVerifyOtp(null);
        existingUser.setVerifyOtpExpireAt(0L);
        existingUser.setIsAccountVerified(true);
        userRepository.save(existingUser);

    }


}
