package com.nrwall.flightlog.auth;

import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.nrwall.flightlog.security.JwtService;
import com.nrwall.flightlog.users.entity.AppUser;
import com.nrwall.flightlog.users.service.UserService;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

  @Autowired private UserService userService;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private AuthenticationManager authenticationManager;
  @Autowired private JwtService jwtService;

  public static record RegisterRequest(@NotBlank String username, @NotBlank String password) {}
  public static record LoginRequest(@NotBlank String username, @NotBlank String password) {}
  public static record ChangePwRequest(@NotBlank String currentPassword, @NotBlank String newPassword) {}
  public static record AuthResponse(String token, String username) {}
  public static record MeResponse(Long id, String username) {}

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
    String u = req.username().trim().toLowerCase();
    if (u.length() < 3) return ResponseEntity.badRequest().body("Username must be at least 3 chars");
    if (req.password().length() < 6) return ResponseEntity.badRequest().body("Password must be at least 6 chars");
    if (userService.exists(u)) return ResponseEntity.badRequest().body("Username already exists");

    AppUser user = new AppUser(u, passwordEncoder.encode(req.password()));
    userService.save(user);
    String token = jwtService.generateToken(user.getUsername(), user.getId());
    return ResponseEntity.ok(new AuthResponse(token, user.getUsername()));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest req) {
    String u = req.username().trim().toLowerCase();
    Authentication auth = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(u, req.password()));
    AppUser user = (AppUser) auth.getPrincipal();
    String token = jwtService.generateToken(user.getUsername(), user.getId());
    return ResponseEntity.ok(new AuthResponse(token, user.getUsername()));
  }

  @GetMapping("/me")
  public ResponseEntity<?> me(Authentication auth) {
    AppUser user = (AppUser) auth.getPrincipal();
    return ResponseEntity.ok(new MeResponse(user.getId(), user.getUsername()));
  }
  
  @PostMapping("/changePassword")
  public ResponseEntity<?> changePassword(@RequestBody ChangePwRequest req, Authentication auth){
	  AppUser user = (AppUser) auth.getPrincipal();
	  
	  //validate current password is correct
	  if (!passwordEncoder.matches(req.currentPassword(), user.getPassword())) {
		  return ResponseEntity.status(400).body("Current Password is not Correct");
	  }
	  //validate password length
	  var newPass = req.newPassword();
	  if (newPass.length() < 6) {
		  return ResponseEntity.badRequest().body("Invalid Password Length: Must be 6 or greater");
	  }
	  //set new password
	  user.setPasswordHash(passwordEncoder.encode(newPass));
	  userService.save(user);
	  //issue new token
	  String token = jwtService.generateToken(user.getUsername(), user.getId());
	  return ResponseEntity.ok(new AuthResponse(token, user.getUsername()));
  }
}
