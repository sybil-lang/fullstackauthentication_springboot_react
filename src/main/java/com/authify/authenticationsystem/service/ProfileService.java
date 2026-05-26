package com.authify.authenticationsystem.service;

import com.authify.authenticationsystem.io.ProfileRequest;
import com.authify.authenticationsystem.io.ProfileResponse;

public interface ProfileService {

    ProfileResponse createProfile(ProfileRequest profileRequest);;
}
