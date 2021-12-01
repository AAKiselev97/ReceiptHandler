package ru.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingPrintDto implements Serializable {
    private Integer id;
    private Integer roomId;
    private Integer roomNumber;
    private Integer visitorId;
    private String visitorName;
    private Boolean canceled;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String source;
}
