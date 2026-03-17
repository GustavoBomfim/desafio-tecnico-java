package br.com.shipay.ms_bkd_user.infraestructure.rest.controller.handler;

import br.com.shipay.ms_bkd_user.domain.exceptions.ResourceNotFoundException;
import br.com.shipay.ms_bkd_user.infraestructure.rest.dto.response.ExcecaoResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.UUID;

@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExcecaoResponseDTO> handlerResourceNotFoundException(ResourceNotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status.value()).body(new ExcecaoResponseDTO(e.getMessage(), status.value()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExcecaoResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(new ExcecaoResponseDTO(ex.getMessage(), status.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExcecaoResponseDTO> handleInternalError(Exception ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);

        logger.error("[INTERNAL_ERROR] TraceID: {} | Path: {} | Message: {}",
                traceId, request.getRequestURI(), ex.getMessage(), ex);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(
                new ExcecaoResponseDTO("Ocorreu um erro interno. Protocolo: " + traceId, status.value())
        );
    }
}
