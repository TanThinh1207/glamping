package com.group2.glamping.model.dto.response;

import com.group2.glamping.model.entity.BookingSelection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingSelectionResponse {

    int id;
    String name;
    double quantity;

    public static BookingSelectionResponse fromEntity(BookingSelection bookingSelection) {
        if (bookingSelection == null) {
            System.out.println("Booking Selection is null");
            return null;
        }


        return BookingSelectionResponse.builder()
                .id(bookingSelection.getSelection().getId())
                .name(bookingSelection.getSelection().getName())
                .quantity(bookingSelection.getQuantity())
                .build();

    }

//    public static List<BookingSelectionResponse> fromEntityList(List<BookingSelection> bookingSelections) {
//        if (bookingSelections == null) {
//            System.out.println("Booking Selection is null");
//            return null;
//        }
//
//        return bookingSelections.stream()
//                .map(BookingSelectionResponse::fromEntity)
//                .collect(Collectors.toList());
//    }


}
