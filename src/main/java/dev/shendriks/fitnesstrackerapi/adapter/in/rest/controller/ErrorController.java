package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.ApiErrorResponseDTO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    @RequestMapping("/error")
    public ResponseEntity<ApiErrorResponseDTO> handleError(HttpServletRequest request) {
        String errorUrl = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        if (status != null) {
            log.info("Request Error: {} - {} {}", errorUrl, status, errorMessage);
        }

        return ResponseEntity
            .status(status != null ? (Integer) status : 500)
            .body(new ApiErrorResponseDTO("Error"));
    }
}
