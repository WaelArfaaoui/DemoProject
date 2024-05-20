package com.talan.adminmodule.controller;



import com.talan.adminmodule.dto.AuthenticationRequest;
import com.talan.adminmodule.dto.AuthenticationResponse;

import com.talan.adminmodule.service.impl.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Tag(name = "Authentication")
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  @Autowired
  public AuthenticationController(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
          @RequestBody AuthenticationRequest request
  ) {

    try {
      return ResponseEntity.ok(authenticationService.authenticate(request));
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
              .body(AuthenticationResponse.builder()
                      .error("Credentials provided are incorrect. Please check your credentials and try again.")
                      .build());
    } catch (AuthenticationException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
              .body(AuthenticationResponse.builder()
                      .error("An error occurred during authentication. Please try again later.")
                      .build());
    }
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    authenticationService.refreshToken(request, response);
  }


}
