package com.talan.adminmodule.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.talan.adminmodule.config.JwtService;
import com.talan.adminmodule.dto.AuthenticationRequest;
import com.talan.adminmodule.dto.AuthenticationResponse;
import com.talan.adminmodule.entity.Role;
import com.talan.adminmodule.entity.User;
import com.talan.adminmodule.repository.UserRepository;
import com.talan.adminmodule.service.impl.AuthenticationService;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.ResponseFacade;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.mock.web.MockHttpServletMapping;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.access.intercept.RunAsImplAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
@ContextConfiguration(classes = {AuthenticationService.class, AuthenticationManager.class})
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class AuthenticationServiceTest {
    @MockBean
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthenticationService authenticationService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserRepository userRepository;
    /**
     * Method under test:
     * {@link AuthenticationService#authenticate(AuthenticationRequest)}
     */
    @Test
    void testAuthenticate() throws AuthenticationException {
        // Arrange
        when(authenticationManager.authenticate(Mockito.<Authentication>any()))
                .thenReturn(new TestingAuthenticationToken("Principal", "Credentials"));
        User user = new User();
        user.setActive(true);
        user.setCompany("Company");
        user.setEmail("jane.doe@example.org");
        user.setFirstname("Jane");
        user.setId(1);
        user.setLastname("Doe");
        user.setNonExpired(true);
        user.setPassword("iloveyou");
        user.setPhone("6625550144");
        user.setProfileImagePath("Profile Image Path");
        user.setRole(Role.BUSINESSEXPERT);
        Optional<User> ofResult = Optional.of(user);
        when(userRepository.findByEmail(Mockito.<String>any())).thenReturn(ofResult);
        when(jwtService.generateRefreshToken(Mockito.<UserDetails>any())).thenReturn("ABC123");
        when(jwtService.generateToken(Mockito.<UserDetails>any())).thenReturn("ABC123");
        // Act
        AuthenticationResponse actualAuthenticateResult = authenticationService
                .authenticate(new AuthenticationRequest("jane.doe@example.org", "iloveyou"));
        // Assert
        verify(jwtService).generateRefreshToken(isA(UserDetails.class));
        verify(jwtService).generateToken(isA(UserDetails.class));
        verify(userRepository).findByEmail("jane.doe@example.org");
        verify(authenticationManager).authenticate(isA(Authentication.class));
        assertEquals("ABC123", actualAuthenticateResult.getAccessToken());
        assertEquals("ABC123", actualAuthenticateResult.getRefreshToken());
        assertNull(actualAuthenticateResult.getError());
    }
    /**
     * Method under test:
     * {@link AuthenticationService#refreshToken(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    void testRefreshToken() throws IOException {
        //   Diffblue Cover was unable to create a Spring-specific test for this Spring method.
        // Arrange
        ArrayList<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(new RunAsImplAuthenticationProvider());
        ProviderManager authenticationManager = new ProviderManager(providers);
        UserRepository repository = mock(UserRepository.class);
        AuthenticationService authenticationService = new AuthenticationService(repository, new JwtService(),
                authenticationManager);
        MockHttpServletRequest request = new MockHttpServletRequest();
        Response response = new Response();
        // Act
        authenticationService.refreshToken(request, response);
        // Assert that nothing has changed
        HttpServletResponse response2 = response.getResponse();
        assertInstanceOf(ResponseFacade.class, response2);
        assertInstanceOf(DelegatingServletInputStream.class, request.getInputStream());
        assertInstanceOf(MockHttpServletMapping.class, request.getHttpServletMapping());
        assertInstanceOf(MockHttpSession.class, request.getSession());
        assertEquals("", request.getContextPath());
        assertEquals("", request.getMethod());
        assertEquals("", request.getRequestURI());
        assertEquals("", request.getServletPath());
        assertEquals("HTTP/1.1", request.getProtocol());
        assertEquals("http", request.getScheme());
        assertEquals("localhost", request.getLocalName());
        assertEquals("localhost", request.getRemoteHost());
        assertEquals("localhost", request.getServerName());
        assertEquals(80, request.getLocalPort());
        assertEquals(80, request.getRemotePort());
        assertEquals(80, request.getServerPort());
        assertEquals(DispatcherType.REQUEST, request.getDispatcherType());
        assertFalse(request.isAsyncStarted());
        assertFalse(request.isAsyncSupported());
        assertFalse(request.isRequestedSessionIdFromURL());
        assertTrue(request.isActive());
        assertTrue(request.isRequestedSessionIdFromCookie());
        assertTrue(request.isRequestedSessionIdValid());
        ServletOutputStream expectedOutputStream = response.getOutputStream();
        assertSame(expectedOutputStream, response2.getOutputStream());
    }

    @Test
    void testRefreshToken2() throws IOException {
        // Arrange
        ArrayList<AuthenticationProvider> providers = new ArrayList<>();
        providers.add(new RunAsImplAuthenticationProvider());
        ProviderManager authenticationManager = new ProviderManager(providers);
        UserRepository repository = mock(UserRepository.class);
        AuthenticationService authenticationService = new AuthenticationService(repository, new JwtService(),
                authenticationManager);
        HttpServletRequestWrapper request = mock(HttpServletRequestWrapper.class);
        when(request.getHeader(Mockito.<String>any())).thenReturn("https://example.org/example");
        Response response = new Response();
        // Act
        authenticationService.refreshToken(request, response);
        // Assert that nothing has changed
        verify(request).getHeader("Authorization");
        HttpServletResponse response2 = response.getResponse();
        assertTrue(response2 instanceof ResponseFacade);
        ServletOutputStream expectedOutputStream = response.getOutputStream();
        assertSame(expectedOutputStream, response2.getOutputStream());
    }
}
