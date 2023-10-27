package com.shadai.kafkapp.domain;

public record LibraryEvent(
    Integer libraryEventId,
    LibraryEventType type,
    Book book
) {    
}
