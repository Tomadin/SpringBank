
package com.springbank.dto.Response;


public class TokenResponseDTO {
    private final String token;

    public TokenResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
    
}
