package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.BookingDetailRequest;
import com.group2.glamping.model.dto.requests.BookingRequest;
import com.group2.glamping.model.dto.requests.BookingServiceRequest;
import com.group2.glamping.model.entity.*;
import com.group2.glamping.model.entity.id.IdBookingService;
import com.group2.glamping.model.enums.BookingDetailStatus;
import com.group2.glamping.model.enums.BookingStatus;
import com.group2.glamping.repository.*;
import com.group2.glamping.service.interfaces.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final BookingServiceRepository bookingServiceRepository;
    private final CampTypeRepository campTypeRepository;
    private final CampSiteRepository campSiteRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    @Override
    public Optional<Booking> createBooking(BookingRequest bookingRequest) {
        Optional<User> user = userRepository.findById(bookingRequest.getUserId());

        Optional<CampSite> campSite = campSiteRepository.findById(bookingRequest.getCampSiteId());

        Booking booking = new Booking();

        if (user.isPresent() && campSite.isPresent()) {
            booking = Booking.builder()
                    .user(user.get())
                    .campSite(campSite.get())
                    .totalAmount(bookingRequest.getTotalAmount())
                    .status(BookingStatus.Pending)
                    .createdTime(LocalDateTime.now())
                    .build();
            System.out.println(bookingRequest.getBookingServiceList());
        }


        Booking bookingDb = bookingRepository.save(booking);
        //bookingServiceRepository.saveAll(bookingRequest.getBookingServiceList());


        for (BookingServiceRequest bookingService : bookingRequest.getBookingServiceList()) {
            Optional<com.group2.glamping.model.entity.Service> service = serviceRepository.findById(bookingService.getId_service());
            IdBookingService idBookingService = new IdBookingService();
            if (service.isPresent()) {
                idBookingService.setBookingId(bookingDb.getId());
                idBookingService.setServiceId(service.get().getId());
                com.group2.glamping.model.entity.BookingService bookingServiceEntity = new com.group2.glamping.model.entity.BookingService();
                bookingServiceEntity.setIdBookingService(idBookingService);
                bookingServiceEntity.setBooking(bookingDb);
                bookingServiceEntity.setService(service.get());
                bookingServiceEntity.setQuantity(bookingService.getQuantity());
                bookingServiceRepository.save(bookingServiceEntity);
            }

        }

        for (BookingDetailRequest bookingDetailRequest : bookingRequest.getBookingDetails()) {
            Optional<CampType> campType = campTypeRepository.findById(bookingDetailRequest.getCampTypeId());
            if (campType.isPresent()) {
                for (int i = 0; i < bookingDetailRequest.getQuantity(); i++) {
                    BookingDetail bookingDetail = BookingDetail.builder()
                            .booking(bookingDb)
                            .campType(campType.get())
                            .createdTime(LocalDateTime.now())
                            .checkInTime(bookingDetailRequest.getCheckInAt())
                            .checkOutTime(bookingDetailRequest.getCheckOutAt())
                            .status(BookingDetailStatus.Waiting)
                            .build();
                    bookingDetailRepository.save(bookingDetail);
                }
            }

        }
        return Optional.of(bookingDb);
    }

}
/*
 *
 *
 * */
