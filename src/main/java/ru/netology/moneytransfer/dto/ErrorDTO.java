package ru.netology.moneytransfer.dto;

import lombok.Builder;

@Builder
public record ErrorDTO(
        String message,
        int operationId
) {
}
