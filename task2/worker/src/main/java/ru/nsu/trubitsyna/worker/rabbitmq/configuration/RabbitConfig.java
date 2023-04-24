package ru.nsu.trubitsyna.worker.rabbitmq.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
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

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue managerRequestQueue() {
        return new Queue(MANAGER_REQUEST_QUEUE_NAME);
    }

    @Bean
    public Binding managerRequestBinding(DirectExchange exchange) {
        return BindingBuilder.bind(managerRequestQueue()).to(exchange).with(MANAGER_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Queue workerResponseQueue() {
        return new Queue(WORKER_RESPONSE_QUEUE_NAME);
    }

    @Bean
    public Binding workerResponseBinding(DirectExchange exchange) {
        return BindingBuilder.bind(workerResponseQueue()).to(exchange).with(WORKER_RESPONSE_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(xmlMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter xmlMessageConverter() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(CrackHashManagerRequest.class, CrackHashWorkerResponse.class);
        return new MarshallingMessageConverter(jaxb2Marshaller);
    }
}
