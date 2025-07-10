
package com.springbank.exception;


public class UsernameNoEncontradoException extends RuntimeException{


    public UsernameNoEncontradoException() {
    }


    public UsernameNoEncontradoException(String msg) {
        super(msg);
    }
}
