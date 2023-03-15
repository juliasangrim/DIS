package ru.nsu.trubitsyna.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;
import ru.nsu.trubitsyna.manager.service.CrackHashService;

/**
 * Контроллер для обработки ответа от воркера.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/api/manager")
public class WorkerResponseHandlingController {

    private final CrackHashService crackHashService;

    /**
     * Эндпоинт для обработки ответа от воркера.
     *
     * @param response ответ воркера.
     */
    @PatchMapping("/hash/crack/task")
    public void getWorkerResponse(@RequestBody CrackHashWorkerResponse response) {
        crackHashService.writeResponse(response);
    }
}