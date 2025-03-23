package com.group2.glamping.repository;

import com.group2.glamping.model.entity.Booking;
import com.group2.glamping.model.entity.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Integer> {

    List<BookingDetail> findBookingDetailByBooking(Booking bookingId);
}
