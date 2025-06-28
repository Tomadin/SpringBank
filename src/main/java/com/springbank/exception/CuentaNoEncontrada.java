
package com.springbank.exception;


public class CuentaNoEncontrada extends RuntimeException {


    public CuentaNoEncontrada() {
    }

    
    public CuentaNoEncontrada(String msg) {
        super(msg);
    }
}
