
package com.springbank.exception;


public class CuentaInvalida extends RuntimeException {


    public CuentaInvalida() {
    }

    
    public CuentaInvalida(String msg) {
        super(msg);
    }
}
