
package com.springbank.exception;


public class DniInvalido extends RuntimeException {

    
    public DniInvalido() {
    }

    
    public DniInvalido(String msg) {
        super(msg);
    }
}
