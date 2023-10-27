package com.shadai.kafkapp.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shadai.kafkapp.domain.LibraryEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LibraryEventProducer{

    @Value("${spring.kafka.topic}")
    public String topic;

    private final KafkaTemplate<Integer,String> kafkaTemplate;

    private ObjectMapper objectMapper;

    public LibraryEventProducer(KafkaTemplate<Integer,String>kafkaTemplate, ObjectMapper objectMapper)
    {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException{
        var key = libraryEvent.libraryEventId();
        var value = objectMapper.writeValueAsString(libraryEvent);
        var cf =  kafkaTemplate.send(topic,key,value);
        cf.whenComplete((sendResult, throwable)->{
            if (throwable != null) {
                handleFailure(key,value,throwable);
            }else{
                handleSuccess(key,value,sendResult);
            }
        });
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> sendResult) {
        log.info("message was send succesfully key: {} value: {} partition {}", key, value,
        sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(Integer key, String value, Throwable throwable) {
        log.error("error sending he message: {}", throwable.getMessage(), throwable);
    }
}