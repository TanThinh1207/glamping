package com.group2.glamping.service.impl;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.BookingRequest;
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
import com.group2.glamping.utils.ResponseFilterUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
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
                .checkInTime(bookingRequest.getCheckInTime())
                .checkOutTime(bookingRequest.getCheckOutTime())
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
        return Optional.of(bookingMapper.toDto(resp));
    }

    //Accept Bookings
    @Override
    public BookingResponse acceptBookings(Integer bookingId) {
        Optional<Booking> existedBooking = bookingRepository.findById(bookingId);
        Booking booking = new Booking();
        if (existedBooking.isPresent()) {
            booking = existedBooking.get();
            if (booking.getStatus() == BookingStatus.Deposit) {
                booking.setStatus(BookingStatus.Accepted);
            }
            bookingRepository.save(booking);
            User user = booking.getUser();
            emailService.sendBookingConfirmation(user.getEmail(), user.getFirstname(), bookingId, booking.getCampSite().getName());
        }
        return bookingMapper.toDto(booking);
    }

    //Deny Bookings
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
    public PagingResponse<?> getBookings(Map<String, String> params, int page, int size, String sortBy, String direction) {
        Specification<Booking> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Booking, CampSite> campSiteJoin = root.join("campSite");

            if (params.containsKey("campSiteId") && params.get("campSiteId").isEmpty()) {
                return criteriaBuilder.and(criteriaBuilder.disjunction());
            }

            if (params.containsKey("hostId")) {
                String hostIdValue = params.get("hostId");
                predicates.add(criteriaBuilder.equal(campSiteJoin.get("user").get("id"), Long.parseLong(hostIdValue)));
            }

            params.forEach((key, value) -> {
                switch (key) {
                    case "id":
                        predicates.add(criteriaBuilder.equal(root.get("id"), value));
                        break;
                    case "name":
                        predicates.add(criteriaBuilder.like(root.get("name"), "%" + value + "%"));
                        break;
                    case "userId":
                        Join<Booking, User> bookingUserJoin = root.join("user");
                        predicates.add(criteriaBuilder.equal(bookingUserJoin.get("id"), value));
                        break;
                    case "status":
                        predicates.add(criteriaBuilder.equal(root.get("status"), value));
                        break;
                    case "rating":
                        predicates.add(criteriaBuilder.equal(root.get("rating"), value));
                        break;
                    case "campSiteId":
                        if (value.contains(",")) {
                            List<Long> campSiteIds = Arrays.stream(value.split(","))
                                    .map(Long::parseLong)
                                    .toList();
                            predicates.add(campSiteJoin.get("id").in(campSiteIds));
                        } else {
                            predicates.add(criteriaBuilder.equal(campSiteJoin.get("id"), Long.parseLong(value)));
                        }
                        break;
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Booking> bookingPage = bookingRepository.findAll(spec, pageable);
        List<BookingResponse> bookingResponses = bookingPage.getContent().stream()
                .map(bookingMapper::toDto)
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
    public Object getFilteredBookings(Map<String, String> params, int page, int size, String fields, String sortBy, String direction) {
        PagingResponse<?> bookings = getBookings(params, page, size, sortBy, direction);
        return ResponseFilterUtil.getFilteredResponse(fields, bookings, "Return using dynamic filter successfully");
    }


    @Override
    public void confirmPaymentSuccess(Integer paymentId) {
        Booking booking = bookingRepository.findById(paymentId).orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
        Payment payment = Payment.builder()
                .booking(booking)
                .paymentMethod("VNPay")
                .completedTime(LocalDateTime.now())
                .status(PaymentStatus.Completed)
                .totalAmount(booking.getTotalAmount() * 0.3)
                .build();
        paymentService.savePayment(payment);
        booking.setStatus(BookingStatus.Deposit);
        bookingRepository.save(booking);
        pushNotificationService.sendNotification(booking.getCampSite().getUser().getId(), "New Booking For " + booking.getCampSite().getName(),
                "A new booking has been made for your campsite " + booking.getCampSite().getName() + "from " + booking.getUser().getFirstname());
    }

    @Override
    public BookingResponse checkInBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
        List<BookingDetail> bookingDetails = bookingDetailRepository.findBookingDetails(booking.getId());

        for (BookingDetail detail : bookingDetails) {
            detail.setCheckInTime(LocalDateTime.now());
            detail.setStatus(BookingDetailStatus.Check_In);
        }

        bookingDetailRepository.saveAll(bookingDetails);

        booking.setStatus(BookingStatus.Check_In);
        bookingRepository.save(booking);

        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingResponse checkOutBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
        List<BookingDetail> bookingDetails = bookingDetailRepository.findBookingDetails(booking.getId());

        for (BookingDetail detail : bookingDetails) {
            detail.setCheckInTime(LocalDateTime.now());
            detail.setStatus(BookingDetailStatus.Check_Out);
        }

        bookingDetailRepository.saveAll(bookingDetails);

        booking.setStatus(BookingStatus.Check_Out);
        bookingRepository.save(booking);

        return bookingMapper.toDto(booking);
    }
}

