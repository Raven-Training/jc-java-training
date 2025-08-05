package com.raven.training.persistence.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private List<ApiError> errors;
    private LocalDateTime timestamp;
}
