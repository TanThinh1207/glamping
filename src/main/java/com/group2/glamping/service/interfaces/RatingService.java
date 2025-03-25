package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.response.FullRatingResponse;
import com.group2.glamping.model.dto.response.PagingResponseSingle;
import org.springframework.stereotype.Service;

@Service
public interface RatingService {

    PagingResponseSingle<FullRatingResponse> getFullRating(Integer campSiteId, int page, int size, String sortBy, String direction);
}
