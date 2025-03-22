package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.response.RevenueGraphDto;
import com.group2.glamping.model.entity.Booking;
import com.group2.glamping.model.entity.BookingDetail;
import com.group2.glamping.model.entity.Payment;
import com.group2.glamping.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class RevenueService {

    private final BookingRepository bookingRepository;

    public List<RevenueGraphDto> getRevenueGraph(Long hostId, LocalDateTime startDate, LocalDateTime endDate, Long campSiteId, String interval) {
        List<Booking> bookings = bookingRepository.findCompletedBookings(hostId, startDate, endDate);
        TreeMap<String, RevenueGraphDto> revenueMap = new TreeMap<>();

        for (Booking booking : bookings) {
            String keyDate = interval.equals("monthly")
                    ? YearMonth.from(booking.getCheckOutTime()).toString()
                    : booking.getCheckOutTime().toLocalDate().toString();

            double revenue = booking.getNetAmount();
            double addOn = booking.getBookingDetailList().stream()
                    .filter(bd -> campSiteId == null || bd.getCamp().getId() == (campSiteId))
                    .mapToDouble(BookingDetail::getAddOn)
                    .sum();

            double totalAmount = booking.getTotalAmount();
            double firstPaymentAmount = booking.getPaymentList().stream()
                    .findFirst()
                    .map(Payment::getTotalAmount)
                    .orElse(0.0);

            double profit = firstPaymentAmount - (totalAmount * 0.1);

            revenueMap.computeIfAbsent(keyDate, k -> new RevenueGraphDto(k, 0.0, 0.0, 0, 0))
                    .addRevenueProfitAndBooking(revenue + addOn, profit, 1);
        }
        if (!revenueMap.isEmpty()) {
            String latestKey = revenueMap.lastKey();
            revenueMap.get(latestKey).setRecentRevenue(revenueMap.get(latestKey).getTotalRevenue());
        }

        return new ArrayList<>(revenueMap.values());
    }


}
