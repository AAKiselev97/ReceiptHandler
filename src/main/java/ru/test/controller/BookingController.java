package ru.test.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.test.service.BookingService;

import java.io.File;

@RestController
@RequestMapping("/receipt")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping(value = "/{fileName}")
    public ResponseEntity<File> getAllBooking(@PathVariable(name = "fileName") String fileName) {
        File file = bookingService.getReceipt(fileName);
        return file != null
                ? new ResponseEntity<>(file, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
