package com.shadai.kafkapp.domain;

public record Book (
    Integer bookId, 
    String bookTitle, 
    String bookAuthor){

}
