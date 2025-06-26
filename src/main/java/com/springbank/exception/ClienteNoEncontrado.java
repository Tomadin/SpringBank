
package com.springbank.exception;


public class ClienteNoEncontrado extends Exception{

    
    public ClienteNoEncontrado() {
    }

    
    public ClienteNoEncontrado(String msg) {
        super(msg);
    }
}
