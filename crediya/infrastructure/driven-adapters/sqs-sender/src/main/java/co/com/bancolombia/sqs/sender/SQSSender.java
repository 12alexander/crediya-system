package co.com.bancolombia.sqs.sender;

import co.com.bancolombia.model.enums.StatusEnum;
import co.com.bancolombia.model.notification.gateways.NotificationGateway;
import co.com.bancolombia.model.orders.Orders;
import co.com.bancolombia.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements NotificationGateway {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<Void> notifyOrderDecision(Orders order) {
        return Mono.fromCallable(() -> buildNotificationMessage(order))
                .flatMap(this::send)
                .doOnNext(messageId -> log.info("Order decision notification sent for order: {} with messageId: {}", 
                        order.getId(), messageId))
                .then()
                .onErrorMap(ex -> new RuntimeException("Failed to send order decision notification", ex));
    }

    private String buildNotificationMessage(Orders order) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("orderId", order.getId());
            message.put("emailAddress", order.getEmailAddress());
            message.put("decision", getDecisionFromStatusId(order.getIdStatus()));
            message.put("amount", order.getAmount());
            message.put("deadline", order.getDeadline());
            message.put("decisionDate", order.getUpdateDate());
            message.put("type", "ORDER_DECISION");
            
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize notification message", e);
        }
    }

    private String getDecisionFromStatusId(String statusId) {
        try {
            return StatusEnum.fromId(statusId).getName();
        } catch (IllegalArgumentException e) {
            log.warn("Unknown status ID: {}", statusId);
            return "UNKNOWN";
        }
    }
}
