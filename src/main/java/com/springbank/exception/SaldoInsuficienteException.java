
package com.springbank.exception;


public class SaldoInsuficienteException extends RuntimeException{


    public SaldoInsuficienteException() {
    }


    public SaldoInsuficienteException(String msg) {
        super(msg);
    }
}
