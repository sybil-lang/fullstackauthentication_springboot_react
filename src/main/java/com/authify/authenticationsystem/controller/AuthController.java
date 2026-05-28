package com.authify.authenticationsystem.controller;

import com.authify.authenticationsystem.io.AuthRequest;
import com.authify.authenticationsystem.io.AuthResponse;
import com.authify.authenticationsystem.service.AppUserDetailService;
import com.authify.authenticationsystem.service.ProfileService;
import com.authify.authenticationsystem.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AppUserDetailService appUserDetailService;
    private final JwtUtil jwtUtil;
    private final ProfileService profileService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {

        try {

            authenticate(
                    request.getEmail(),
                    request.getPassword()
            );

            final UserDetails userDetails =
                    appUserDetailService.loadUserByUsername(
                            request.getEmail()
                    );

            final String jwtToken =
                    jwtUtil.generateToken(userDetails);

            ResponseCookie cookie = ResponseCookie
                    .from("jwt", jwtToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("Strict")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new AuthResponse(request.getEmail(), jwtToken));

        } catch (BadCredentialsException ex) {

            Map<String, Object> error = new HashMap<>();

            error.put("error", true);
            error.put("message", "Email or password is incorrect");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(error);

        } catch (DisabledException ex) {

            Map<String, Object> error = new HashMap<>();

            error.put("error", true);
            error.put("message", "Account is disabled");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(error);

        } catch (Exception ex) {

            ex.printStackTrace();

            Map<String, Object> error = new HashMap<>();

            error.put("error", true);
            error.put("message", ex.getMessage());

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(error);
        }
    }

    private void authenticate(String email, String password) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );
    }

    @GetMapping("/is-authenticated")
    public ResponseEntity<Boolean> isAuthenticated(@CurrentSecurityContext(expression="authentication?.name")String email){

        return ResponseEntity.ok(email!=null);
    }


    @PostMapping("/send-reset-otp")
    public void sendResetOtp(@RequestParam String email){

        try{
            profileService.sendResetOtp(email);
        }
        catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
}