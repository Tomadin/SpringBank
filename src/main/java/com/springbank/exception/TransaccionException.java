
package com.springbank.exception;


public class TransaccionException extends RuntimeException{


    public TransaccionException() {
    }

   
    public TransaccionException(String msg) {
        super(msg);
    }
}
