package ru.nsu.trubitsyna.manager.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;
import ru.nsu.trubitsyna.manager.client.ManagerWebClient;
import ru.nsu.trubitsyna.manager.dto.*;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Сервис для обработки запросов на взлом хэша от пользователей.
 */
@Slf4j
@Service
@AllArgsConstructor
public class CrackHashService {

    private static final int TIMEOUT = 300000;
    private final ManagerWebClient managerWebClient;
    private final ConcurrentMap<String, CrackHashTask> requestInfo = new ConcurrentHashMap<>();

    /**
     * Метод для отправки запроса на взлом хэша воркеру.
     *
     * @param request запрос на взлом хэша от пользователя.
     * @return ответ с идентификатором задачи.
     */
    public CrackHashResponse sendHashCrackRequest(CrackHashRequest request) {
        UUID requestId = UUID.randomUUID();
        requestInfo.put(requestId.toString(), CrackHashTask.builder()
                .status(Status.IN_PROGRESS)
                .data(null)
                .lastUpdated(System.currentTimeMillis())
                .build());

        managerWebClient.sendRequest(requestId, request);

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
        requestInfo.computeIfPresent(requestId, (key, value) -> {
            long currTime = System.currentTimeMillis();
            if (Status.IN_PROGRESS.equals(value.getStatus()) && currTime - value.getLastUpdated() >= TIMEOUT) {
                value.setStatus(Status.ERROR);
                log.info("Task status with id {} updated.", requestId);
            }
            return value;
        });
        var info = requestInfo.getOrDefault(requestId, new CrackHashTask());
        return StatusResponse.builder()
                .status(info.getStatus())
                .data(info.getData())
                .build();
    }

    /**
     * Метод для обработки ответа от воркера.
     *
     * @param response ответ воркера.
     */
    public void writeResponse(CrackHashWorkerResponse response) {
        requestInfo.computeIfPresent(response.getRequestId(), (key, value) -> {
            value.setStatus(Status.READY);
            value.setData(response.getAnswers().getWords());
            return value;
        });
        log.info("Task status with id {} updated.", response.getRequestId());
    }

}
