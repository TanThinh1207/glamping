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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class RevenueService {

    private final BookingRepository bookingRepository;

    public RevenueGraphResponse getRevenueGraph(Long hostId, LocalDateTime startDate, LocalDateTime endDate, Long campSiteId, String interval) {
        List<Booking> bookings = bookingRepository.findCompletedBookings(hostId, startDate, endDate);
        return calculateRevenueGraph(bookings, startDate, endDate, interval, campSiteId, false);
    }

    public RevenueGraphResponse getSystemRevenueGraph(LocalDateTime startDate, LocalDateTime endDate, String interval) {
        List<Booking> bookings = bookingRepository.findCompletedBookingsForSystem(startDate, endDate);
        return calculateRevenueGraph(bookings, startDate, endDate, interval, null, true);
    }

    private RevenueGraphResponse calculateRevenueGraph(List<Booking> bookings, LocalDateTime startDate, LocalDateTime endDate, String interval, Long campSiteId, boolean isSystemFee) {
        TreeMap<String, RevenueGraphDto> revenueMap = new TreeMap<>();

        for (Booking booking : bookings) {
            String keyDate = interval.equals("monthly")
                    ? YearMonth.from(booking.getCheckOutTime()).toString()
                    : booking.getCheckOutTime().toLocalDate().toString();

            double revenue, profit;
            if (isSystemFee) {
                revenue = profit = booking.getSystemFee();
            } else {
                revenue = booking.getNetAmount();
                double addOn = booking.getBookingDetailList().stream()
                        .filter(bd -> campSiteId == null || bd.getCamp().getId() == (campSiteId))
                        .mapToDouble(BookingDetail::getAddOn)
                        .sum();
                revenue += addOn;

                double totalAmount = booking.getTotalAmount();
                double firstPaymentAmount = booking.getPaymentList().stream()
                        .findFirst()
                        .map(Payment::getTotalAmount)
                        .orElse(0.0);
                profit = firstPaymentAmount - (totalAmount * 0.1);
            }

            revenueMap.computeIfAbsent(keyDate, k -> new RevenueGraphDto(k, 0.0, 0.0, 0))
                    .addRevenueProfitAndBooking(revenue, profit, 1);
        }

        LocalDateTime previousStartDate, previousEndDate;
        if ("daily".equals(interval)) {
            long daysRange = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
            previousStartDate = startDate.minusDays(daysRange + 1);
            previousEndDate = endDate.minusDays(daysRange + 1);
        } else {
            long monthsRange = ChronoUnit.MONTHS.between(
                    YearMonth.from(startDate.toLocalDate()),
                    YearMonth.from(endDate.toLocalDate())
            );

            previousStartDate = startDate.minusMonths(monthsRange + 1);
            previousEndDate = endDate.minusMonths(monthsRange + 1);
        }
        System.out.println("Previous Start Date: " + previousStartDate);
        System.out.println("Previous End Date: " + previousEndDate);
        double recentRevenue = bookings.stream()
                .filter(b -> !b.getCheckOutTime().isBefore(previousStartDate) && !b.getCheckOutTime().isAfter(previousEndDate))
                .mapToDouble(b -> {
                    if (isSystemFee) {
                        return b.getSystemFee();
                    } else {
                        double addOn = b.getBookingDetailList().stream()
                                .filter(bd -> campSiteId == null || bd.getCamp().getId() == (campSiteId))
                                .mapToDouble(BookingDetail::getAddOn)
                                .sum();
                        return b.getNetAmount() + addOn;
                    }
                })
                .sum();


        return new RevenueGraphResponse(recentRevenue, new ArrayList<>(revenueMap.values()));
    }


}
