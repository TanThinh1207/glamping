package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.response.RevenueGraphDto;
import com.group2.glamping.model.dto.response.RevenueGraphResponse;
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

    public RevenueGraphResponse getRevenueGraph(Long hostId, LocalDateTime startDate, LocalDateTime endDate, Long campSiteId, String interval) {
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

            revenueMap.computeIfAbsent(keyDate, k -> new RevenueGraphDto(k, 0.0, 0.0, 0))
                    .addRevenueProfitAndBooking(revenue + addOn, profit, 1);
        }

        // Khúc này là để tính recentRevenue
        // 2025-03-01 -> 2025-03-30 là lấy theo daily
        //thì recent là tổng totalProfit của 2025-02-01 -> 2025-02-29
        //còn nếu monthly thì thành 2024-03-01 -> 2025-03-01
        //recent thành tổng totalProfit của 2023-03-01 -> 2024-03-01
        List<RevenueGraphDto> profitList = new ArrayList<>(revenueMap.values());
        LocalDateTime previousStartDate;
        LocalDateTime previousEndDate;
        if ("daily".equals(interval)) {
            YearMonth previousMonth = YearMonth.from(startDate.minusMonths(1));
            previousStartDate = previousMonth.atDay(1).atStartOfDay();
            previousEndDate = previousMonth.atEndOfMonth().atTime(23, 59, 59);
        } else {
            previousStartDate = startDate.minusYears(1);
            previousEndDate = endDate.minusYears(1);
        }

        double recentRevenue = bookingRepository.findCompletedBookings(hostId, previousStartDate, previousEndDate).stream()
                .mapToDouble(Booking::getTotalAmount)
                .sum();
        return new RevenueGraphResponse(recentRevenue, profitList);
    }


}
