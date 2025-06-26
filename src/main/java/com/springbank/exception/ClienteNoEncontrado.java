
package com.springbank.exception;


public class ClienteNoEncontrado extends RuntimeException{

    
    public ClienteNoEncontrado() {
    }

    
    public ClienteNoEncontrado(String msg) {
        super(msg);
    }
}
