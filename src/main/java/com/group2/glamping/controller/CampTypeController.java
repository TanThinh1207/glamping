package com.group2.glamping.controller;

import com.group2.glamping.service.interfaces.ICampTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/camps")
public class CampTypeController {

    @Autowired
    ICampTypeService campTypeService;

    @GetMapping("/availableQuantity")
    public ResponseEntity<Integer> getAvailableQuantity(
            @RequestParam Integer campTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime checkOut) {

        Integer availableQuantity = campTypeService.findAvailableSlots(campTypeId, checkIn, checkOut);
        return new ResponseEntity<>(availableQuantity, HttpStatus.OK);
    }
}
