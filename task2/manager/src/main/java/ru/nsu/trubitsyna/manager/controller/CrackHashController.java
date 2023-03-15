package ru.nsu.trubitsyna.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nsu.trubitsyna.manager.dto.CrackHashRequest;
import ru.nsu.trubitsyna.manager.dto.CrackHashResponse;
import ru.nsu.trubitsyna.manager.dto.StatusResponse;
import ru.nsu.trubitsyna.manager.service.CrackHashService;

/**
 * Контроллер для обработки запросов на взлом хэша от пользователей.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hash")
public class CrackHashController {

    private final CrackHashService service;

    /**
     * Эндпоинт для передачи задачи воркеру.
     *
     * @param request запрос на взлом хэша.
     * @return ответ с идентификатором задачи.
     */
    @PostMapping("/crack")
    public ResponseEntity<CrackHashResponse> hashCrack(@RequestBody CrackHashRequest request) {
        var response = service.sendHashCrackRequest(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Эндпоинт для получения статуса задачи.
     *
     * @param requestId идентификатор задачи.
     * @return ответ со статусом задачи.
     */
    @GetMapping("/status")
    public ResponseEntity<StatusResponse> getRequestStatus(@RequestParam String requestId) {
        var response = service.getRequestStatus(requestId);
        return ResponseEntity.ok(response);
    }
}
