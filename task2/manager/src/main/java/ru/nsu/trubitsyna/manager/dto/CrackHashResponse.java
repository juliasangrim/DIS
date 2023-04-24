package ru.nsu.trubitsyna.manager.dto;

import lombok.*;

/**
 * Ответ с идентификатором задачи.
 */
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrackHashResponse {
    private String requestId;
}