package com.springbank.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ErrorDTO> runtimeExceptionHandler(RuntimeException ex, HttpServletRequest request) {
        return buildErrorResponse("P_500", ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = CuentaNoEncontrada.class)
    public ResponseEntity<ErrorDTO> cuentaNoEncontradaExceptionHandler(CuentaNoEncontrada ex, HttpServletRequest request) {
        return buildErrorResponse("P_404", ex, request, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(value = ClienteNoEncontrado.class)
    public ResponseEntity<ErrorDTO> clienteNoEncontradoExceptionHandler(ClienteNoEncontrado ex, HttpServletRequest request) {
        return buildErrorResponse("P_404", ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = CuentaInvalida.class)
    public ResponseEntity<ErrorDTO> cuentaInvalidaExceptionHandler(CuentaInvalida ex, HttpServletRequest request) {
        return buildErrorResponse("P_500", ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = SaldoInsuficienteException.class)
    public ResponseEntity<ErrorDTO> saldoInsuficienteExceptionHandler(SaldoInsuficienteException ex, HttpServletRequest request) {
        return buildErrorResponse("P_400", ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DniInvalido.class)
    public ResponseEntity<ErrorDTO> dniInvalidoExceptionHandler(DniInvalido ex, HttpServletRequest request) {
        return buildErrorResponse("P_500", ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = EmailInvalido.class)
    public ResponseEntity<ErrorDTO> emailInvalidoExceptionHandler(EmailInvalido ex, HttpServletRequest request) {
        return buildErrorResponse("P_400", ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MontoInvalidoException.class)
    public ResponseEntity<ErrorDTO> montoInvalidoExceptionHandler(MontoInvalidoException ex, HttpServletRequest request) {
        return buildErrorResponse("P_400", ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class) //Atrapa errores no controlados como configuracion, nullPointer, etc. Cubre lo no previsto.
    public ResponseEntity<ErrorDTO> handleGenericException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse("P_500", ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDTO> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        return buildErrorResponse("P_403", ex, request, HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<ErrorDTO> buildErrorResponse(String code, Exception ex, HttpServletRequest request, HttpStatus status) {
        return new ResponseEntity<>(
                new ErrorDTO(code, ex.getMessage(), request.getRequestURI(), status.value()),
                status
        );
    }
}
