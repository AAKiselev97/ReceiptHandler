package ru.test.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.test.exception.CustomException;
import ru.test.model.BookingPrintDto;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

@Service
public class BookingService {
    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;
    private String source = "receipts/%s.txt";
    private static final Logger log = LogManager.getLogger(BookingService.class);

    @Autowired
    public BookingService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "createReceipt", groupId = "message_group_id")
    public void createOrReWriteReceipt(String message) {
        try {
            log.info("try parsing JSON " + message + " to BookingPrintDto.class");
            BookingPrintDto booking = objectMapper.readValue(message, BookingPrintDto.class);
            if (booking.getSource() != null) {
                deleteFile(booking.getSource());
            }
            createFile(booking);
            produce(booking);
        } catch (Exception e) {
            log.error("something wrong");
            throw new CustomException(e);
        }
    }

    public File getReceipt(String fileName) {
        File file = new File(String.format(source, fileName.replaceAll(".txt", "")));
        if (file.exists()) {
            return file;
        } else {
            throw new CustomException("File not found");
        }
    }

    private void deleteFile(String fileName) {
        File file = new File(String.format(source, fileName.replaceAll(".txt", "")));
        log.info(file.delete() ? "Receipt " + file.getName() + " remove" : "Receipt " + file.getName() + " not remove");
    }

    private String createFile(BookingPrintDto booking) throws IOException {
        File file = new File(String.format(source, UUID.randomUUID()));
        booking.setSource(file.getName());
        log.info("try create File with path " + file.getName());
        String receiptInfo = String.format("Чек бронирования с Id [%d]:\nкомната №%d, id [%d], посетитель %s, id [%d], время въезда - %s, время выезда - %s",
                booking.getId(), booking.getRoomNumber(), booking.getRoomId(), booking.getVisitorName(), booking.getVisitorId(), booking.getCheckInTime(), booking.getCheckOutTime());
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(receiptInfo);
            writer.flush();
        }
        return file.getCanonicalPath();
    }

    private void produce(BookingPrintDto booking) {
        System.out.println("Producing the message: " + booking);
        try {
            kafkaTemplate.send("messages", objectMapper.writeValueAsString(booking));
        } catch (JsonProcessingException e) {
            log.error("something wrong");
            throw new CustomException(e);
        }
    }
}
