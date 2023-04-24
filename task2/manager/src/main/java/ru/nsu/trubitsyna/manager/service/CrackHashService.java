package ru.nsu.trubitsyna.manager.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;
import ru.nsu.trubitsyna.manager.dto.*;
import ru.nsu.trubitsyna.manager.entity.CrackHashTaskEntity;
import ru.nsu.trubitsyna.manager.rabbitmq.ManagerRequestPublisher;
import ru.nsu.trubitsyna.manager.repository.CrackHashTaskRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Сервис для обработки запросов на взлом хэша от пользователей.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrackHashService {

    private static final int TIMEOUT_MILLIS = 300000;
    private final ManagerRequestPublisher managerRequestPublisher;
    private final CrackHashTaskRepository crackHashTaskRepository;
    @Value("${config.worker.count}")
    private int partCount;

    /**
     * Метод для отправки запроса на взлом хэша воркеру.
     *
     * @param request запрос на взлом хэша от пользователя.
     * @return ответ с идентификатором задачи.
     */
    public CrackHashResponse sendHashCrackRequest(CrackHashRequest request) {
        CrackHashTaskEntity task = CrackHashTaskEntity.builder()
                .hash(request.getHash())
                .maxLength(request.getMaxLength())
                .status(Status.WAIT)
                .processedParts(new HashSet<>())
                .data(new ArrayList<>())
                .lastUpdate(System.currentTimeMillis())
                .build();
        String requestId = crackHashTaskRepository.save(task)
                .getId();

        return CrackHashResponse.builder()
                .requestId(requestId)
                .build();
    }

    /**
     * Метод для получения статуса задачи.
     *
     * @param requestId идентификатор задачи.
     * @return ответ со статусом задачи.
     */
    public StatusResponse getRequestStatus(String requestId) {
        var taskOpt = crackHashTaskRepository.findById(requestId);
        if (taskOpt.isEmpty()) {
            return new StatusResponse();
        }

        var task = taskOpt.get();
        return StatusResponse.builder()
                .status(task.getStatus())
                .data(task.getData())
                .build();
    }

    /**
     * Метод для обработки ответа от воркера.
     *
     * @param response ответ воркера.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void writeResponse(CrackHashWorkerResponse response) {
        var taskOpt = crackHashTaskRepository.findById(response.getRequestId());
        if (taskOpt.isEmpty()) {
            log.info("Task {} not found", response.getRequestId());
            return;
        }

        var task = taskOpt.get();
        if (task.getProcessedParts().contains(response.getPartNumber())) {
            log.info("Part {}/{} of task {} already processed", response.getPartNumber()+1, partCount, response.getRequestId());
            return;
        }
        if (task.getStatus() == Status.ERROR) {
            log.info("Task {} already marked as ERROR", task.getId());
            return;
        }

        task.getData().addAll(response.getAnswers().getWords());
        task.getProcessedParts().add(response.getPartNumber());
        if (task.getProcessedParts().size() == partCount) {
            task.setStatus(Status.READY);
        }
        task.setLastUpdate(System.currentTimeMillis());

        crackHashTaskRepository.save(task);
    }

    /**
     * Шедулер для постановки задач в очередь.
     */
    @Scheduled(fixedDelay = 3000L)
    public void checkWaitTasks() {
        List<CrackHashTaskEntity> tasks = crackHashTaskRepository.findByStatus(Status.WAIT);
        if (tasks.isEmpty()) {
            return;
        }

        log.info("Handle {} waiting tasks", tasks.size());
        for (var task : tasks) {
            try {
                log.info("Handle task {}", task.getId());
                for (int partNumber = 0; partNumber < partCount; partNumber++) {
                    managerRequestPublisher.publishManagerRequest(task, partNumber, partCount);
                }
                task.setStatus(Status.IN_PROGRESS);
                crackHashTaskRepository.save(task);
            } catch (Exception e) {
                log.error(String.format("Can't handle task %s - %s", task.getId(), e.getMessage()), e);
            }
        }
        log.info("All {} waiting tasks handled", tasks.size());
    }


    /**
     * Шедулер для поиска повисших тасок.
     */
    @Scheduled(fixedDelay = 120000L)
    public void checkStuckTask() {
        List<CrackHashTaskEntity> tasks = crackHashTaskRepository.findByStatus(Status.IN_PROGRESS);
        if (tasks.isEmpty()) {
            return;
        }

        log.info("Handle {} in progress tasks", tasks.size());
        for (var task : tasks) {
            try {
                log.info("Handle task {}", task.getId());
                if (System.currentTimeMillis() - task.getLastUpdate() > TIMEOUT_MILLIS) {
                    task.setStatus(Status.ERROR);
                    crackHashTaskRepository.save(task);
                }
            } catch (Exception e) {
                log.error(String.format("Can't handle task %s - %s", task.getId(), e.getMessage()), e);
            }
        }
        log.info("All {} in progress tasks handled", tasks.size());
    }

}
