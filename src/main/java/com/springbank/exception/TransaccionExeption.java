
package com.springbank.exception;


public class TransaccionExeption extends RuntimeException{


    public TransaccionExeption() {
    }

   
    public TransaccionExeption(String msg) {
        super(msg);
    }
}
