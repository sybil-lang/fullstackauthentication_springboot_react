package com.authify.authenticationsystem.controller;


import com.authify.authenticationsystem.io.ProfileRequest;
import com.authify.authenticationsystem.io.ProfileResponse;
import com.authify.authenticationsystem.service.EmailService;
import com.authify.authenticationsystem.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final EmailService emailService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(@Valid @RequestBody ProfileRequest request) {

        ProfileResponse response = profileService.createProfile(request);

       emailService.sendWelcomeMail(response.getEmail(),response.getName());
        return response;
    }

    @GetMapping("/profile")
    public ProfileResponse getProfile(@CurrentSecurityContext(expression="authentication?.name")String email){
        return profileService.getProfile(email);
    }
}
