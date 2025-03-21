package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.response.RevenueGraphDto;
import com.group2.glamping.model.entity.Booking;
import com.group2.glamping.model.entity.BookingDetail;
import com.group2.glamping.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class RevenueService {

    private final BookingRepository bookingRepository;

    public List<RevenueGraphDto> getRevenueGraph(Long hostId, LocalDateTime startDate, LocalDateTime endDate, Long campSiteId, String interval) {
        List<Booking> bookings = bookingRepository.findCompletedBookings(hostId, startDate, endDate);

        Map<String, RevenueGraphDto> revenueMap = new TreeMap<>();

        for (Booking booking : bookings) {
            String keyDate = interval.equals("monthly")
                    ? YearMonth.from(booking.getCheckOutTime()).toString() // yyyy-MM
                    : booking.getCheckOutTime().toLocalDate().toString();  // yyyy-MM-dd

            double revenue = booking.getNetAmount();
            double addOn = booking.getBookingDetailList().stream()
                    .filter(bd -> campSiteId == null || bd.getCamp().getId() == (campSiteId))
                    .mapToDouble(BookingDetail::getAddOn)
                    .sum();
            double profit = booking.getPaymentList().stream()
                    .mapToDouble(p -> p.getTotalAmount() * 0.9)
                    .sum();

            revenueMap.computeIfAbsent(keyDate, k -> new RevenueGraphDto(k, 0.0, 0.0))
                    .addRevenueAndProfit(revenue + addOn, profit);
        }

        return new ArrayList<>(revenueMap.values());
    }

}
