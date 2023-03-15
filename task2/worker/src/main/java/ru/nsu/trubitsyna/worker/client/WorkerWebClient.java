package ru.nsu.trubitsyna.worker.client;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;
import ru.nsu.ccfit.schema.crack_hash_response.ObjectFactory;

import java.util.List;

/**
 * Веб-клиент для отправки ответов менеджеру.
 */
@Slf4j
@Component
public class WorkerWebClient {
    private static final String MANAGER_PATCH_URI = "/internal/api/manager/hash/crack/task";

    @Value("${manager.url}")
    private String workerUrl;

    private WebClient webClient;
    private ObjectFactory factory;

    /**
     * Инициализации веб-клиента.
     */
    @PostConstruct
    public void init() {
        webClient = WebClient.builder()
                .baseUrl(workerUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .build();
        factory = new ObjectFactory();
    }

    /**
     * Метод для отправки ответа менеджеру.
     *
     * @param requestId  идентификатор задачи.
     * @param partNumber номер воркера.
     * @param words      слова, получившиеся в результате взлома хэша.
     */
    public void sendResponse(String requestId, int partNumber, List<String> words) {
        var workerResponse = formWorkerResponse(requestId, partNumber, words);

        var response = webClient.patch()
                .uri(MANAGER_PATCH_URI)
                .bodyValue(workerResponse)
                .retrieve()
                .toBodilessEntity()
                .block();

        log.info("Response send to manager.");
    }

    private CrackHashWorkerResponse formWorkerResponse(String requestID,
                                                       int partNumber,
                                                       List<String> words) {
        var answers = factory.createCrackHashWorkerResponseAnswers();
        answers.getWords().addAll(words);

        var crackHashWorkerResponse = factory.createCrackHashWorkerResponse();
        crackHashWorkerResponse.setRequestId(requestID);
        crackHashWorkerResponse.setPartNumber(partNumber);
        crackHashWorkerResponse.setAnswers(answers);

        return crackHashWorkerResponse;
    }
}
