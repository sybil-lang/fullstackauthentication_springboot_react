package com.authify.authenticationsystem.io;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VerifyOtpResponse {

    private boolean success;

    private boolean verified;

    private String email;

    private String message;
}
