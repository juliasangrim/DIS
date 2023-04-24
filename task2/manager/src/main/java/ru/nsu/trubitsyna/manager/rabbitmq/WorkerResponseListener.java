package ru.nsu.trubitsyna.manager.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;
import ru.nsu.trubitsyna.manager.service.CrackHashService;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkerResponseListener {

    private final CrackHashService crackHashService;
    @Value("${config.worker.count}")
    private int partCount;

    /**
     * Прослушивает очередь с ответами воркера и обновляет сущность таски в бд.
     * @param workerResponse
     */
    @RabbitListener(queues = {"q.worker-response"}, concurrency = "10")
    public void onManagerRequest(CrackHashWorkerResponse workerResponse) {
        log.info("Get worker response {} ({}/{}) - {}",
                workerResponse.getRequestId(), workerResponse.getPartNumber()+1, partCount, workerResponse.getAnswers().getWords());
        crackHashService.writeResponse(workerResponse);
    }
}
