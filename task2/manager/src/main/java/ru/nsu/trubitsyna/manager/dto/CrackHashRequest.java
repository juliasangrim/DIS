package ru.nsu.trubitsyna.manager.dto;

import lombok.*;

/**
 * Запрос на взлом хэша от пользователя.
 */
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrackHashRequest {
    private String hash;
    private int maxLength;
}