package com.proyectoexamen.backend.controller;

import com.proyectoexamen.backend.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public String notFound(ResourceNotFoundException ex, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        model.addAttribute("title", "Recurso no encontrado");
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String unexpected(Exception ex, Model model, HttpServletResponse response) {
        log.error("Error inesperado al procesar la solicitud", ex);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        model.addAttribute("title", "No pudimos completar la operacion");
        model.addAttribute("message", "Ocurrio un error inesperado. Revise los datos e intentelo nuevamente.");
        return "error";
    }
}
