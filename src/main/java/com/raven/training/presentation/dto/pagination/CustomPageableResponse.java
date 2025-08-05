package com.raven.training.presentation.dto.pagination;

import java.util.List;

public record CustomPageableResponse<T>(
        List<T> page,
        int count,
        int limit,
        int offset,
        int total_pages,
        long total_count,
        Integer previous_page,
        int current_page,
        Integer next_page
) {}
