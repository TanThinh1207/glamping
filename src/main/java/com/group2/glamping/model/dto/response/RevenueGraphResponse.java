package com.group2.glamping.model.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RevenueGraphResponse {
    private double recentRevenue;
    private List<RevenueGraphDto> profitList;

    public RevenueGraphResponse(double recentRevenue, List<RevenueGraphDto> profitList) {
        this.recentRevenue = recentRevenue;
        this.profitList = profitList;
    }

}
