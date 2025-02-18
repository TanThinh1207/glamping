package com.group2.glamping.service.impl;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.BookingRequest;
import com.group2.glamping.model.dto.response.BookingResponse;
import com.group2.glamping.model.entity.*;
import com.group2.glamping.model.entity.id.IdBookingSelection;
import com.group2.glamping.model.enums.BookingDetailStatus;
import com.group2.glamping.model.enums.BookingStatus;
import com.group2.glamping.model.mapper.BookingMapper;
import com.group2.glamping.repository.*;
import com.group2.glamping.service.interfaces.BookingService;
import com.group2.glamping.service.interfaces.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final BookingSelectionRepository bookingSelectionRepository;
    private final CampTypeRepository campTypeRepository;
    private final CampSiteRepository campSiteRepository;
    private final UserRepository userRepository;
    private final SelectionRepository selectionRepository;
    private final BookingMapper bookingMapper;
    private final EmailService emailService;

    @Override
    public Optional<BookingResponse> createBooking(BookingRequest bookingRequest) {
        User user = userRepository.findById(bookingRequest.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        CampSite campSite = campSiteRepository.findById(bookingRequest.getCampSiteId())
                .orElseThrow(() -> new AppException(ErrorCode.CAMP_SITE_NOT_FOUND));

        Booking booking = Booking.builder()
                .user(user)
                .campSite(campSite)
                .totalAmount(bookingRequest.getTotalAmount())
                .status(BookingStatus.Pending)
                .createdTime(LocalDateTime.now())
                .systemFee(0.9 * bookingRequest.getTotalAmount())
                .netAmount(bookingRequest.getTotalAmount() - (0.9 * bookingRequest.getTotalAmount()))
                .build();

        Booking bookingDb = bookingRepository.save(booking);
        //bookingServiceRepository.saveAll(bookingRequest.getBookingServiceList());


        List<BookingSelection> bookingSelections = bookingRequest.getBookingSelectionRequestList().stream()
                .map(bookingService -> {
                    Selection service = selectionRepository.findById(bookingService.idSelection())
                            .orElseThrow(() -> new AppException(ErrorCode.SELECTION_NOT_FOUND));

                    return BookingSelection.builder()
                            .idBookingService(new IdBookingSelection(bookingDb.getId(), service.getId()))
                            .booking(bookingDb)
                            .selection(service)
                            .quantity(bookingService.quantity())
                            .name(service.getName())
                            .build();
                })
                .collect(Collectors.toList());

        bookingSelectionRepository.saveAll(bookingSelections);

        List<BookingDetail> bookingDetails = bookingRequest.getBookingDetails().stream()
                .flatMap(bookingDetailRequest -> {
                    CampType campType = campTypeRepository.findById(bookingDetailRequest.getCampTypeId())
                            .orElseThrow(() -> new AppException(ErrorCode.CAMP_TYPE_NOT_FOUND));

                    return IntStream.range(0, bookingDetailRequest.getQuantity())
                            .mapToObj(i -> BookingDetail.builder()
                                    .booking(bookingDb)
                                    .campType(campType)
                                    .createdTime(LocalDateTime.now())
                                    .checkInTime(bookingDetailRequest.getCheckInAt())
                                    .checkOutTime(bookingDetailRequest.getCheckOutAt())
                                    .status(BookingDetailStatus.Waiting)
                                    .build());
                })
                .collect(Collectors.toList());

        bookingDetailRepository.saveAll(bookingDetails);

        Booking resp = bookingRepository.findByIdWithoutDetails(bookingDb.getId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        resp.setBookingDetailList(bookingDetailRepository.findBookingDetails(bookingDb.getId()));
        resp.setBookingSelectionList(bookingSelectionRepository.findBookingSelections(bookingDb.getId()));

        return Optional.of(bookingMapper.toDto(resp));
    }

    //Retrieve Pending Bookings
    @Override
    public List<BookingResponse> getPendingBookingsByCampSiteId(Integer campSiteId) {
        List<Booking> bookings = bookingRepository.findPendingBookingsByCampSiteId(campSiteId);

        // Nếu danh sách booking trống, trả về danh sách rỗng
        if (bookings.isEmpty()) {
            return Collections.emptyList();
        }

        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    //Retrieve Completed Bookings
    @Override
    public List<BookingResponse> getCompletedBookingsByCampSiteId(Integer campSiteId) {
        List<Booking> bookings = bookingRepository.findCompletedBookingsByCampSiteId(campSiteId);

        // Nếu danh sách booking trống, trả về danh sách rỗng
        if (bookings.isEmpty()) {
            return Collections.emptyList();
        }

        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    //Retrieve bookings by Id
    @Override
    public BookingResponse getBookingById(Integer bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        return bookingOptional.map(bookingMapper::toDto).orElse(null);

    }


    //Accept Bookings
    @Override
    public BookingResponse acceptBookings(Integer bookingId) {
        Optional<Booking> existedBooking = bookingRepository.findById(bookingId);
        Booking booking = new Booking();
        if (existedBooking.isPresent()) {
            booking = existedBooking.get();
            if(booking.getStatus() == BookingStatus.Pending) {
                booking.setStatus(BookingStatus.Accepted);
            }
            bookingRepository.save(booking);
            User user = booking.getUser();
            emailService.sendBookingConfirmation(user.getEmail(), user.getFirstname(), bookingId, booking.getCampSite().getName());
        }
        return bookingMapper.toDto(booking);
    }

    //Accept Bookings
    @Override
    public BookingResponse denyBookings(Integer bookingId) {
        Optional<Booking> existedBooking = bookingRepository.findById(bookingId);
        Booking booking = new Booking();
        if (existedBooking.isPresent()) {
            booking = existedBooking.get();
            if(booking.getStatus() == BookingStatus.Pending) {
                booking.setStatus(BookingStatus.Denied);
            }
            bookingRepository.save(booking);
        }
        return bookingMapper.toDto(booking);
    }
}

