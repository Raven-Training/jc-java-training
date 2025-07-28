package com.raven.training.persistence.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class ErrorResponse {

    private Integer status;
    private String message;
    private List<String> details;
    private LocalDateTime timestamp;
}
