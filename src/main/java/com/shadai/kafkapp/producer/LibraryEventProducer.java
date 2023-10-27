package com.shadai.kafkapp.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shadai.kafkapp.domain.LibraryEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    public CompletableFuture<SendResult<Integer, String>> sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException{
        var key = libraryEvent.libraryEventId();
        var value = objectMapper.writeValueAsString(libraryEvent);
        var producer = buildProducerRecord(key,value);
        var cf =  kafkaTemplate.send(producer);

        return cf.whenComplete((sendResult, throwable)->{
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

    public ProducerRecord<Integer,String> buildProducerRecord(Integer key, String value){
        List<Header> recordHeaders = List.of(new RecordHeader("source-event", "scanner".getBytes()));
        return new ProducerRecord<>(topic, null, key, value, recordHeaders);
    }
}