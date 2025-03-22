package com.group2.glamping.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RevenueGraphDto {
    private String date;
    private double totalRevenue;
    private double totalProfit;
    private long numberOfBookings;

    public void addRevenueProfitAndBooking(double revenue, double profit, long count) {
        this.totalRevenue += revenue;
        this.totalProfit += profit;
        this.numberOfBookings += count;
    }
}
