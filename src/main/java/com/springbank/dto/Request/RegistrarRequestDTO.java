
package com.springbank.dto.Request;


public class RegistrarRequestDTO {
    private final String username;
    private final String password;

    public RegistrarRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    
    
}
