
package com.springbank.exception;


public class TokenInvalidoException extends IllegalArgumentException{


    public TokenInvalidoException() {
    }


    public TokenInvalidoException(String msg) {
        super(msg);
    }
}
