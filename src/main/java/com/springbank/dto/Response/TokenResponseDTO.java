
package com.springbank.dto.Response;


public class TokenResponseDTO {
    private final String token;
    private final String refreshToken;

    public TokenResponseDTO(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
    
}
