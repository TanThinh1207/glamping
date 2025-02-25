package com.group2.glamping.service.impl;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.BookingRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.BookingResponse;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.entity.*;
import com.group2.glamping.model.entity.id.IdBookingSelection;
import com.group2.glamping.model.enums.BookingDetailStatus;
import com.group2.glamping.model.enums.BookingStatus;
import com.group2.glamping.model.enums.PaymentStatus;
import com.group2.glamping.model.mapper.BookingMapper;
import com.group2.glamping.repository.*;
import com.group2.glamping.service.interfaces.BookingService;
import com.group2.glamping.service.interfaces.EmailService;
import com.group2.glamping.service.interfaces.PaymentService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final PaymentService paymentService;
    private final PushNotificationService pushNotificationService;

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

//        for (int i = 0; i < 100000; i++) {
//            pushNotificationService.sendNotification(bookingRequest.getUserId(), "New Booking!", "A new booking has been made for your campsite.");
//        }

        pushNotificationService.sendNotification(bookingRequest.getUserId(), "New Booking!", "A new booking has been made for your campsite.");

        return Optional.of(bookingMapper.toDto(resp));
    }

    //Accept Bookings
    @Override
    public BookingResponse acceptBookings(Integer bookingId) {
        Optional<Booking> existedBooking = bookingRepository.findById(bookingId);
        Booking booking = new Booking();
        if (existedBooking.isPresent()) {
            booking = existedBooking.get();
            if (booking.getStatus() == BookingStatus.Pending) {
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
    public BookingResponse denyBookings(Integer bookingId, String deniedReason) {
        Optional<Booking> existedBooking = bookingRepository.findById(bookingId);
        Booking booking = new Booking();
        if (existedBooking.isPresent()) {
            booking = existedBooking.get();
            if (booking.getStatus() == BookingStatus.Pending) {
                booking.setStatus(BookingStatus.Denied);
            }
            bookingRepository.save(booking);
            User user = booking.getUser();
            emailService.sendDeniedBookingEmail(user.getEmail(),
                    user.getFirstname(),
                    bookingId,
                    booking.getCampSite().getName(),
                    deniedReason);
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    public PagingResponse<?> getBookings(Map<String, String> params, int page, int size) {
        Specification<Booking> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            params.forEach((key, value) -> {
                switch (key) {
                    case "id":
                        predicates.add(criteriaBuilder.equal(root.get("id"), value));
                        break;
                    case "name":
                        predicates.add(criteriaBuilder.like(root.get("name"), "%" + value + "%"));
                        break;
                    case "status":
                        predicates.add(criteriaBuilder.equal(root.get("status"), Boolean.parseBoolean(value)));
                        break;
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page, size);
        Page<Booking> bookingPage = bookingRepository.findAll(spec, pageable);
        List<BookingResponse> bookingResponses = bookingPage.getContent().stream()
                .map(bookingMapper::toDto)  // Fix lỗi gọi hàm mapping
                .toList();

        return new PagingResponse<>(
                bookingResponses,
                bookingPage.getTotalElements(),
                bookingPage.getTotalPages(),
                bookingPage.getNumber(),
                bookingPage.getNumberOfElements()
        );
    }

    @Override
    public MappingJacksonValue getFilteredBookings(Map<String, String> params, int page, int size, String fields) {
        PagingResponse<?> bookings = getBookings(params, page, size); // Fix lỗi gọi sai hàm

        SimpleFilterProvider filters = new SimpleFilterProvider();
        if (fields != null && !fields.isEmpty()) {
            filters.addFilter("dynamicFilter", SimpleBeanPropertyFilter.filterOutAllExcept(fields.split(",")));
        } else {
            filters.addFilter("dynamicFilter", SimpleBeanPropertyFilter.serializeAll());
        }

        MappingJacksonValue mapping = new MappingJacksonValue(BaseResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .data(bookings)
                .message("Retrieve all bookings successfully") // Fix message
                .build());

        mapping.setFilters(filters);

        return mapping;
    }


    @Override
    public void confirmPaymentSuccess(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
        Payment payment = Payment.builder()
                .booking(booking)
                .paymentMethod("VN-Pay")
                .completedTime(LocalDateTime.now())
                .status(PaymentStatus.Completed)
                .totalAmount(booking.getTotalAmount() * 0.3)
                .build();
        paymentService.save(payment);
        booking.setStatus(BookingStatus.Deposit);
        bookingRepository.save(booking);
    }
}

