package ru.nsu.trubitsyna.manager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nsu.trubitsyna.manager.dto.Status;
import ru.nsu.trubitsyna.manager.entity.CrackHashTaskEntity;

import java.util.List;

public interface CrackHashTaskRepository extends MongoRepository<CrackHashTaskEntity, String> {
    List<CrackHashTaskEntity> findByStatus(Status status);
}
