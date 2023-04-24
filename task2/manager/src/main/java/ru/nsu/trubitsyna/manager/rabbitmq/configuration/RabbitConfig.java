package ru.nsu.trubitsyna.manager.rabbitmq.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MarshallingMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "e.crack-hash";
    public static final String MANAGER_REQUEST_ROUTING_KEY = "manager-request";
    private static final String MANAGER_REQUEST_QUEUE_NAME = "q." + MANAGER_REQUEST_ROUTING_KEY;
    public static final String WORKER_RESPONSE_ROUTING_KEY = "worker-response";
    private static final String WORKER_RESPONSE_QUEUE_NAME = "q." + WORKER_RESPONSE_ROUTING_KEY;

    /**
     * Создание Direct Exchange.
     * @return экземпляр DirectExchange.
     */
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    /**
     * Создание очереди для запросов менеджера.
     * @return очередь для запросов менеджера.
     */
    @Bean
    public Queue managerRequestQueue() {
        return new Queue(MANAGER_REQUEST_QUEUE_NAME);
    }

    /**
     * Привязка routing-key к очереди для запросов менеджера.
     * @param exchange
     * @return биндинг.
     */
    @Bean
    public Binding managerRequestBinding(DirectExchange exchange) {
        return BindingBuilder.bind(managerRequestQueue()).to(exchange).with(MANAGER_REQUEST_ROUTING_KEY);
    }

    /**
     * Создание очереди для ответов воркера.
     * @return очередь для ответов воркера.
     */
    @Bean
    public Queue workerResponseQueue() {
        return new Queue(WORKER_RESPONSE_QUEUE_NAME);
    }

    /**
     * Привязка routing-key к очереди для ответов воркера.
     * @param exchange
     * @return биндинг.
     */
    @Bean
    public Binding workerResponseBinding(DirectExchange exchange) {
        return BindingBuilder.bind(workerResponseQueue()).to(exchange).with(WORKER_RESPONSE_ROUTING_KEY);
    }

    /**
     * Создание экземпляра класса для доступа к очередям.
     * @param connectionFactory
     * @return экземпляр класса для доступа к очередям.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(xmlMessageConverter());
        return rabbitTemplate;
    }

    /**
     * Создание конвертера для XML-файлов.
     * @return конвертер для XML-файлов.
     */
    @Bean
    public MessageConverter xmlMessageConverter() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(CrackHashManagerRequest.class, CrackHashWorkerResponse.class);
        return new MarshallingMessageConverter(jaxb2Marshaller);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setConsecutiveActiveTrigger(1);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setMessageConverter(xmlMessageConverter());
        return factory;
    }
}
