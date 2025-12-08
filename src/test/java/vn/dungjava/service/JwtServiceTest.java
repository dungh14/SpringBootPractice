package vn.dungjava.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.dungjava.common.TokenType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    @Mock
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateAccessToken_Success() {
        //Chuan bi du lieu
        String username = "john.doe";
        List<String> authorities = List.of("USER", "ADMIN");
        String expectedToken = "mockAccessToken";

        //gia lap phuong thuc phu createAccessToken
        when(jwtService.generateAccessToken(username, authorities)).thenReturn(expectedToken);

        //goi phuong thuc can kiem tra
        String actualToken = jwtService.generateAccessToken(username, authorities);

        //kiem tra ket qua
        assertEquals(expectedToken, actualToken);
        verify(jwtService, times(1)).generateAccessToken(username, authorities);
    }

    @Test
    void testGenerateRefreshToken_Success() {
        //chuan bi du lieu
        String username = "john.doe";
        List<String> authorities = List.of("USER", "ADMIN");
        String expectedToken = "mockRefreshToken";

        //gia lap phuong thuc phu
        when(jwtService.generateRefreshToken(username, authorities)).thenReturn(expectedToken);

        //goi phuong thuc can kiem tra
        String actualToken = jwtService.generateRefreshToken(username, authorities);

        //kiem tra ket qua
        assertEquals(expectedToken, actualToken);
        verify(jwtService, times(1)).generateRefreshToken(username, authorities);
    }

    @Test
    void testExtractUsername_Success() {
        //chuan bi du lieu
        String token = "mockToken";
        TokenType tokenType = TokenType.ACCESS_TOKEN;
        String expectedUsername = "john.doe";

        //gia lap hanh vi cua extractClaim
        when(jwtService.extractUsername(token, tokenType)).thenReturn(expectedUsername);

        //goi phuong thuc can kiem tra
        String actualUsername = jwtService.extractUsername(token, tokenType);

        //kiem tra ket qua
        assertEquals(expectedUsername, actualUsername);
        verify(jwtService, times(1)).extractUsername(token, tokenType);

    }
}