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
import com.group2.glamping.model.mapper.BookingMapper;
import com.group2.glamping.repository.*;
import com.group2.glamping.service.interfaces.BookingService;
import com.group2.glamping.service.interfaces.EmailService;
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

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    @Override
    public Optional<BookingResponse> createBooking(BookingRequest bookingRequest) {
        User user = userRepository.findById(bookingRequest.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        CampSite campSite = campSiteRepository.findById(bookingRequest.getCampSiteId())
                .orElseThrow(() -> new AppException(ErrorCode.CAMP_SITE_NOT_FOUND));

        Booking booking = Booking.builder()
                .user(user)
                .campSite(campSite)
                .totalAmount(0.0)
                .status(BookingStatus.Pending)
                .createdTime(LocalDateTime.now())
                .checkInTime(bookingRequest.getCheckInTime())
                .checkOutTime(bookingRequest.getCheckOutTime())
                .systemFee(0.0)
                .netAmount(0.0)
                .build();

        Booking bookingDb = bookingRepository.save(booking);

        List<BookingSelection> bookingSelections = bookingRequest.getBookingSelectionRequestList().stream()
                .map(bookingSelectionRequest -> {
                    Selection service = selectionRepository.findById(bookingSelectionRequest.idSelection())
                            .orElseThrow(() -> new AppException(ErrorCode.SELECTION_NOT_FOUND));

                    return BookingSelection.builder()
                            .idBookingService(new IdBookingSelection(bookingDb.getId(), service.getId()))
                            .booking(bookingDb)
                            .selection(service)
                            .quantity(bookingSelectionRequest.quantity())
                            .name(service.getName())
                            .build();
                })
                .collect(Collectors.toList());

        bookingSelectionRepository.saveAll(bookingSelections);

        LocalDate checkInDate = bookingDb.getCheckInTime().toLocalDate();
        LocalDate checkOutDate = bookingDb.getCheckOutTime().toLocalDate();
        long totalDays = Math.max(1, ChronoUnit.DAYS.between(checkInDate, checkOutDate));
        long weekendDays = IntStream.range(0, (int) totalDays)
                .mapToObj(checkInDate::plusDays)
                .filter(date -> {
                    DayOfWeek dayOfWeek = date.getDayOfWeek();
                    return dayOfWeek == DayOfWeek.FRIDAY ||
                            dayOfWeek == DayOfWeek.SATURDAY ||
                            dayOfWeek == DayOfWeek.SUNDAY;
                })
                .count();
        long weekdayDays = totalDays - weekendDays;
        System.out.println("WeekendDays: " + weekendDays);
        System.out.println("Weekdays: " + weekdayDays);
        List<BookingDetail> bookingDetails = bookingRequest.getBookingDetails().stream()
                .flatMap(bookingDetailRequest -> {
                    CampType campType = campTypeRepository.findById(bookingDetailRequest.getCampTypeId())
                            .orElseThrow(() -> new AppException(ErrorCode.CAMP_TYPE_NOT_FOUND));


                    BigDecimal amountPerNight = BigDecimal.valueOf(campType.getPrice());
                    BigDecimal weekendRate = BigDecimal.valueOf(campType.getWeekendRate());
                    BigDecimal totalAmount = amountPerNight.multiply(BigDecimal.valueOf(weekdayDays))
                            .add(amountPerNight.multiply(weekendRate).multiply(BigDecimal.valueOf(weekendDays)));

                    System.out.println("Amount per night: " + amountPerNight);
                    System.out.println("weekendRate: " + weekendRate);
                    System.out.println("totalAmount: " + totalAmount);

                    return IntStream.range(0, bookingDetailRequest.getQuantity())
                            .mapToObj(i -> BookingDetail.builder()
                                    .booking(bookingDb)
                                    .campType(campType)
                                    .createdTime(LocalDateTime.now())
                                    .checkInTime(bookingDetailRequest.getCheckInAt())
                                    .checkOutTime(bookingDetailRequest.getCheckOutAt())
                                    .status(BookingDetailStatus.Waiting)
                                    .amount(totalAmount.doubleValue())
                                    .build());
                })
                .collect(Collectors.toList());

        bookingDetailRepository.saveAll(bookingDetails);

        double totalBookingAmount = bookingDetails.stream()
                .mapToDouble(BookingDetail::getAmount)
                .sum();

        double systemFee = 0.1 * totalBookingAmount;
        double netAmount = totalBookingAmount - systemFee;

        bookingDb.setTotalAmount(totalBookingAmount);
        bookingDb.setSystemFee(systemFee);
        bookingDb.setNetAmount(netAmount);

        bookingRepository.save(bookingDb);

        bookingDb.setBookingDetailList(bookingDetailRepository.findBookingDetails(bookingDb.getId()));
        bookingDb.setBookingSelectionList(bookingSelectionRepository.findBookingSelections(bookingDb.getId()));

        return Optional.of(bookingMapper.toDto(bookingDb));
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

