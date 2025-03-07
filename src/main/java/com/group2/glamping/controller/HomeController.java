package com.group2.glamping.controller;

import com.group2.glamping.service.interfaces.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BookingService bookingService;

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/payment")
    public String showPaymentPage(Model model) {
        model.addAttribute("message", "Vui lòng xác nhận thanh toán");
        return "payment";
    }


    @PostMapping("/confirm-payment")
    public String vnpayCallback(
            @RequestParam("vnp_ResponseCode") String responseCode,
            @RequestParam("orderId") Integer orderId,
            Model model) {
        if ("00".equals(responseCode)) {
            // Thanh toán thành công
            bookingService.confirmPaymentSuccess(orderId);
            model.addAttribute("message", "Thanh toán thành công!");
        } else {
            // Thanh toán thất bại
            model.addAttribute("error", "Thanh toán thất bại. Mã lỗi: " + responseCode);
        }
        return "payment";
    }
}
