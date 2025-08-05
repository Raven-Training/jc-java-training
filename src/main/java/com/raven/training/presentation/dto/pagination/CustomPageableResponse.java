package com.raven.training.presentation.dto.pagination;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a custom paginated response.
 * This record is used to provide detailed pagination information to the client,
 * including the content of the current page and metadata about the entire dataset.
 *
 * @param <T> The type of the content in the page.
 * @param page The list of items on the current page.
 * @param count The number of items on the current page.
 * @param limit The maximum number of items per page.
 * @param offset The starting position (index) of the first item on the current page.
 * @param total_pages The total number of pages available.
 * @param total_count The total number of items across all pages.
 * @param previous_page The number of the previous page, or null if there is no previous page.
 * @param current_page The number of the current page.
 * @param next_page The number of the next page, or null if there is no next page.
 */
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
