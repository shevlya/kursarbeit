package ru.ssau.srestapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.ssau.srestapp.dto.auth.LoginRequestDto;
import ru.ssau.srestapp.dto.auth.RefreshTokenRequestDto;
import ru.ssau.srestapp.dto.auth.TokenResponseDto;
import ru.ssau.srestapp.security.CustomUserDetails;
import ru.ssau.srestapp.security.JwtTokenService;
import ru.ssau.srestapp.service.CustomUserDetailsService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String accessToken = jwtTokenService.generateAccessToken(userDetails);
            String refreshToken = jwtTokenService.generateRefreshToken(userDetails);
            return new TokenResponseDto(accessToken, refreshToken, userDetails.getUserId(), userDetails.getEmail());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Неверный email или пароль");
        }
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponseDto refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        Long userId = jwtTokenService.getUserIdFromToken(request.getRefreshToken());
        CustomUserDetails userDetails = customUserDetailsService.loadUserById(userId);
        String newAccessToken = jwtTokenService.generateAccessToken(userDetails);
        String newRefreshToken = jwtTokenService.generateRefreshToken(userDetails);
        return new TokenResponseDto(newAccessToken, newRefreshToken, userDetails.getUserId(), userDetails.getEmail());
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getCurrentUser(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userDetails.getUserId());
        response.put("email", userDetails.getEmail());
        response.put("roles", userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .toList());
        return response;
    }
}
