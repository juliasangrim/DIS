package ru.nsu.trubitsyna.worker.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.nsu.trubitsyna.worker.service.CrackHashService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ManagerRequestListener {

    private final CrackHashService crackHashService;

    /**
     * Слушает очередь запросов менеджера и забирает таски в работу.
     * @param managerRequest запрос от менеджера на выполнение таски.
     */
    @RabbitListener(queues = {"q.manager-request"})
    public void onManagerRequest(CrackHashManagerRequest managerRequest) {
        log.info("Get manager request {} ({}/{})",
                managerRequest.getRequestId(), managerRequest.getPartNumber()+1, managerRequest.getPartCount());
        crackHashService.crackHash(managerRequest);
    }
}
