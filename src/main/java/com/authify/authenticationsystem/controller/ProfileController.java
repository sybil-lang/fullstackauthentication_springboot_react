package com.authify.authenticationsystem.controller;


import com.authify.authenticationsystem.io.ProfileRequest;
import com.authify.authenticationsystem.io.ProfileResponse;
import com.authify.authenticationsystem.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(@RequestBody ProfileRequest request) {

        ProfileResponse response = profileService.createProfile(request);

        // TODO: send welcome email
        return response;
    }
}
