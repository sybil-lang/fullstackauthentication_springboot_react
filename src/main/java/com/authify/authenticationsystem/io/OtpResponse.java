package com.authify.authenticationsystem.io;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OtpResponse {

    private boolean success;

    private boolean otpSent;

    private String email;

    private String message;
}
