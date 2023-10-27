package com.shadai.kafkapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shadai.kafkapp.domain.LibraryEvent;
import com.shadai.kafkapp.producer.LibraryEventProducer;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class LibraryEventController {

    private final LibraryEventProducer libraryEventProducer;

    public LibraryEventController(LibraryEventProducer libraryEventProducer){
        this.libraryEventProducer = libraryEventProducer;
    }

    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(
        @RequestBody LibraryEvent libraryEvent
    ){
        log.info("libraryEvent : {}", libraryEvent);
        try {
            libraryEventProducer.sendLibraryEvent(libraryEvent);
            return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatus.CREATED.value())).body(libraryEvent);
        } catch (JsonProcessingException e) {
            log.error("Json processing failure {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())).body(libraryEvent);
        }        
    }
    
}
