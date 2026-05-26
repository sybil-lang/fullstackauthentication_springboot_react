package com.authify.authenticationsystem.service;

import com.authify.authenticationsystem.entity.UserEntity;
import com.authify.authenticationsystem.io.ProfileRequest;
import com.authify.authenticationsystem.io.ProfileResponse;
import com.authify.authenticationsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProfileServiceImpl implements ProfileService{ 
    
    
    private final UserRepository userRepository;

    public ProfileServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ProfileResponse createProfile(ProfileRequest request) {

        UserEntity newProfile = convertToUserEntity(request);

        newProfile = userRepository.save(newProfile);

        return convertToProfileResponse(newProfile);
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
                .password(request.getPassword())
                .isAccountVerified(false)
                .verifyOtp(null)
                .verifyOtpExpireAt(0L)
                .resetOtp(null)
                .resetOtpExpireAt(0L)
                .build();
    }
}
