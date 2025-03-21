package com.group2.glamping.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RevenueGraphDto {
    private String date;
    private Double revenue;
    private Double withdraw;

    public void addRevenueAndProfit(double additionalRevenue, double additionalProfit) {
        this.revenue += additionalRevenue;
        this.withdraw += additionalProfit;
    }
}