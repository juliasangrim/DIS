package ru.nsu.trubitsyna.manager.dto;

import lombok.*;

import java.util.UUID;

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
    private UUID requestId;
}