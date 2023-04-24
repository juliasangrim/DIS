package ru.nsu.trubitsyna.manager.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MarshallingMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_request.ObjectFactory;
import ru.nsu.trubitsyna.manager.entity.CrackHashTaskEntity;
import ru.nsu.trubitsyna.manager.rabbitmq.configuration.RabbitConfig;
import ru.nsu.trubitsyna.manager.utils.Alphabet;

@Service
@Slf4j
@RequiredArgsConstructor
public class ManagerRequestPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectFactory factory = new ObjectFactory();

    /**
     * Формирует и отправляет таску в очередь с запросами менеджера.
     * @param task сущность таски из бд.
     * @param partNumber номер воркера.
     * @param partCount общее количество воркеров.
     */
    public void publishManagerRequest(CrackHashTaskEntity task, int partNumber, int partCount) {
        CrackHashManagerRequest.Alphabet managerAlphabet = factory.createCrackHashManagerRequestAlphabet();
        managerAlphabet.getSymbols().addAll(Alphabet.asList());

        CrackHashManagerRequest managerRequest = factory.createCrackHashManagerRequest();
        managerRequest.setRequestId(task.getId());
        managerRequest.setPartNumber(partNumber);
        managerRequest.setPartCount(partCount);
        managerRequest.setHash(task.getHash());
        managerRequest.setMaxLength(task.getMaxLength());
        managerRequest.setAlphabet(managerAlphabet);

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME, RabbitConfig.MANAGER_REQUEST_ROUTING_KEY, managerRequest);
        log.info("Manager request {} ({}/{}) was sent to queue", task.getId(), partNumber+1, partCount);
    }
}
