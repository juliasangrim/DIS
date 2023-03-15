package ru.nsu.trubitsyna.manager.dto;

import lombok.*;

import java.util.List;

/**
 * Ответ со статусом задачи.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class StatusResponse {
    private Status status;
    private List<String> data;
}
