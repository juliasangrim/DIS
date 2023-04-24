package ru.nsu.trubitsyna.worker.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;
import ru.nsu.ccfit.schema.crack_hash_response.ObjectFactory;
import ru.nsu.trubitsyna.worker.rabbitmq.configuration.RabbitConfig;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkerResponsePublisher {

    private final RabbitTemplate rabbitTemplate;
    private ObjectFactory factory = new ObjectFactory();

    /**
     * Отправляет в очередь с ответами воркера результат работы воркера.
     * @param request задачки из очереди запросов менеджера.
     * @param answers результат работы воркера.
     */
    public void sendWorkerResponse(CrackHashManagerRequest request, List<String> answers) {
        CrackHashWorkerResponse.Answers workerAnswers = factory.createCrackHashWorkerResponseAnswers();
        workerAnswers.getWords().addAll(answers);

        CrackHashWorkerResponse workerResponse = factory.createCrackHashWorkerResponse();
        workerResponse.setRequestId(request.getRequestId());
        workerResponse.setPartNumber(request.getPartNumber());
        workerResponse.setAnswers(workerAnswers);

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME, RabbitConfig.WORKER_RESPONSE_ROUTING_KEY, workerResponse);
        log.info("Worker response {} ({}/{}) was sent to queue: {}",
                request.getRequestId(), request.getPartNumber()+1, request.getPartCount(), answers);
    }
}
