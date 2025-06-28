
package com.springbank.exception;


public class MontoInvalidoException extends RuntimeException{

    
    public MontoInvalidoException() {
    }

    
    public MontoInvalidoException(String msg) {
        super(msg);
    }
}
