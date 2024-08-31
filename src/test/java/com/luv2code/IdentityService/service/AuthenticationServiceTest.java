package com.luv2code.IdentityService.service;

import com.luv2code.IdentityService.dto.request.AuthenticationRequest;
import com.luv2code.IdentityService.dto.request.IntrospectRequest;
import com.luv2code.IdentityService.dto.request.LogoutRequest;
import com.luv2code.IdentityService.dto.request.RefreshRequest;
import com.luv2code.IdentityService.dto.response.AuthenticationResponse;
import com.luv2code.IdentityService.dto.response.IntrospectResponse;
import com.luv2code.IdentityService.entity.InvalidatedToken;
import com.luv2code.IdentityService.entity.User;
import com.luv2code.IdentityService.exception.AppException;
import com.luv2code.IdentityService.repository.InvalidatedTokenRepository;
import com.luv2code.IdentityService.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.any;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthenticationServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private InvalidatedTokenRepository invalidatedTokenRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;

    private String token;

    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    @BeforeEach
    public void setup() throws JOSEException {
        user = User.builder()
                .username("john")
                .password(new BCryptPasswordEncoder().encode("password"))
                .build();

        token = createTestToken(user.getUsername());
    }

    private String createTestToken(String username) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("devteria.com")
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", "ROLE_USER")
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));

        return jwsObject.serialize();
    }

    @Test
    public void authenticate_validCredentials_success() {
        AuthenticationRequest request = new AuthenticationRequest("john", "password");
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertNotNull(response);
        assertTrue(response.isAuthenticated());
        assertNotNull(response.getToken());
    }

    @Test
    public void authenticate_invalidPassword_throwsException() {
        AuthenticationRequest request = new AuthenticationRequest("john", "wrongpassword");
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));

        assertThrows(AppException.class, () -> authenticationService.authenticate(request));
    }

    @Test
    public void introspect_validToken_success() throws ParseException, JOSEException {
        IntrospectRequest request = new IntrospectRequest(token);

        IntrospectResponse response = authenticationService.introspect(request);

        assertTrue(response.isValid());
    }

    @Test
    public void introspect_invalidToken_failure() throws ParseException, JOSEException {
        IntrospectRequest request = new IntrospectRequest("invalidToken");

        IntrospectResponse response = authenticationService.introspect(request);

        assertFalse(response.isValid());
    }

    @Test
    public void logout_validToken_success() throws ParseException, JOSEException {
        LogoutRequest request = new LogoutRequest(token);
        when(invalidatedTokenRepository.save(any(InvalidatedToken.class))).thenReturn(null);

        authenticationService.logout(request);

        verify(invalidatedTokenRepository, times(1)).save(any(InvalidatedToken.class));
    }

    @Test
    public void refreshToken_validToken_success() throws ParseException, JOSEException {
        RefreshRequest request = new RefreshRequest(token);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(invalidatedTokenRepository.save(any(InvalidatedToken.class))).thenReturn(null);

        AuthenticationResponse response = authenticationService.refreshToken(request);

        assertNotNull(response);
        assertTrue(response.isAuthenticated());
        assertNotNull(response.getToken());
    }

    @Test
    public void refreshToken_invalidToken_failure() throws ParseException, JOSEException {
        RefreshRequest request = new RefreshRequest("invalidToken");

        assertThrows(AppException.class, () -> authenticationService.refreshToken(request));
    }

    @Test
    public void verifyToken_expiredToken_failure() throws ParseException, JOSEException {
        String expiredToken = createExpiredTestToken(user.getUsername());
        assertThrows(AppException.class, () -> authenticationService.introspect(new IntrospectRequest(expiredToken)));
    }

    private String createExpiredTestToken(String username) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("devteria.com")
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().minus(1, ChronoUnit.HOURS))) // Expired 1 hour ago
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", "ROLE_USER")
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));

        return jwsObject.serialize();
    }
}
