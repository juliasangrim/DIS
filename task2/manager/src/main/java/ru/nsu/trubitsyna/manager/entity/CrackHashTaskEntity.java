package ru.nsu.trubitsyna.manager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.nsu.trubitsyna.manager.dto.Status;

import java.util.List;
import java.util.Set;

@Document("tasks")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrackHashTaskEntity {
    @Id
    private String id;
    private String hash;
    private Integer maxLength;
    private Status status;
    private Set<Integer> processedParts;
    private List<String> data;
    private long lastUpdate;
}
