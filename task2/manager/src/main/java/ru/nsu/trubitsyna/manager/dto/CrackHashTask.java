package ru.nsu.trubitsyna.manager.dto;

import lombok.*;

import java.util.List;

/**
 * Сущность для отслеживания статуса задачи.
 */
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrackHashTask {
    private Status status;
    private List<String> data;
    private long lastUpdated;

}