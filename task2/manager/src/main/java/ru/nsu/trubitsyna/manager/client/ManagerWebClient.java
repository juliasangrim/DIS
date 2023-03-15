package ru.nsu.trubitsyna.manager.client;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_request.ObjectFactory;
import ru.nsu.trubitsyna.manager.dto.CrackHashRequest;
import ru.nsu.trubitsyna.manager.utils.Alphabet;

import java.util.UUID;

/**
 * Клиент для отправки сообщений воркеру.
 */
@Component
@Slf4j
public class ManagerWebClient {
    private static final int DEFAULT_PART_NUMBER = 0;
    private static final int DEFAULT_PART_COUNT = 1;
    private static final String WORKER_POST_URI = "/internal/api/worker/hash/crack/task";

    @Value("${worker.url}")
    private String workerUrl;

    private WebClient webClient;
    private ObjectFactory factory;

    /**
     * Инициализация веб-клиента.
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
     * Метод для отправки сообщений воркеру.
     *
     * @param requestId идентификатор задачи.
     * @param request   запрос на взлом хэша.
     */
    public void sendRequest(UUID requestId, CrackHashRequest request) {
        var managerRequest = formManagerRequest(requestId, request);

        var response = webClient.post()
                .uri(WORKER_POST_URI)
                .bodyValue(managerRequest)
                .retrieve()
                .toBodilessEntity()
                .subscribe();
        log.info("Manager send request to worker with hash {}.", request.getHash());
    }

    private CrackHashManagerRequest formManagerRequest(UUID requestID,
                                                       CrackHashRequest request) {
        var alphabet = factory.createCrackHashManagerRequestAlphabet();
        alphabet.getSymbols().addAll(Alphabet.asList());

        var crackHashManagerRequest = factory.createCrackHashManagerRequest();
        crackHashManagerRequest.setRequestId(requestID.toString());
        crackHashManagerRequest.setHash(request.getHash());
        crackHashManagerRequest.setMaxLength(request.getMaxLength());
        crackHashManagerRequest.setPartNumber(DEFAULT_PART_NUMBER);
        crackHashManagerRequest.setPartCount(DEFAULT_PART_COUNT);
        crackHashManagerRequest.setAlphabet(alphabet);

        return crackHashManagerRequest;
    }
}