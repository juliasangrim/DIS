package ru.nsu.trubitsyna.worker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.nsu.trubitsyna.worker.service.CrackHashService;

/**
 * Контроллер для обработки сообщений от менеджера.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/api/worker")
public class ManagerRequestHandlingController {

    private final CrackHashService service;

    /**
     * Эндпоинт для обработки сообщений от менеджера.
     *
     * @param request запрос на взлом хэша от менеджера.
     */
    @PostMapping(value = "/hash/crack/task",
            consumes = MediaType.APPLICATION_XML_VALUE)
    public void crackHash(@RequestBody CrackHashManagerRequest request) {
        service.crackHash(request);
    }
}