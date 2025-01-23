package com.group2.glamping.service.interfaces;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ICampTypeService {
    Integer findAvailableSlots(Integer campTypeId, LocalDateTime checkInDate, LocalDateTime checkOutDate);
}
