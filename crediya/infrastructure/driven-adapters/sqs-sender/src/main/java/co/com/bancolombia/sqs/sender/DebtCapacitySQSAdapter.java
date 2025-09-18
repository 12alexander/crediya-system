package co.com.bancolombia.sqs.sender;

import co.com.bancolombia.api.dto.request.CapacityValidationRequestDTO;
import co.com.bancolombia.model.constants.BusinessRules;
import co.com.bancolombia.model.debtcapacity.DebtCapacity;
import co.com.bancolombia.model.debtcapacity.gateways.DebtCapacityCalculationGateway;
import co.com.bancolombia.sqs.sender.config.DebtCapacitySQSProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class DebtCapacitySQSAdapter implements DebtCapacityCalculationGateway {

    private final DebtCapacitySQSProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> sendCalculationRequest(DebtCapacity debtCapacity) {
        return Mono.fromCallable(() -> buildCapacityValidationMessage(debtCapacity))
                .flatMap(this::sendToSQS)
                .doOnNext(response -> log.info("Debt capacity calculation request sent. MessageId: {}, OrderId: {}",
                    response.messageId(), debtCapacity.getOrderId()))
                .doOnError(error -> log.error("Error sending debt capacity calculation request for OrderId: {}",
                    debtCapacity.getOrderId(), error))
                .then();
    }

    private CapacityValidationRequestDTO buildCapacityValidationMessage(DebtCapacity debtCapacity) {
        return CapacityValidationRequestDTO.builder()
                .orderId(debtCapacity.getOrderId())
                .userId(debtCapacity.getUserId())
                .amount(debtCapacity.getAmount())
                .deadline(debtCapacity.getDeadline())
                .emailAddress(debtCapacity.getEmailAddress())
                .baseSalary(debtCapacity.getBaseSalary())
                .interestRate(debtCapacity.getInterestRate())
                .loanTypeId(debtCapacity.getLoanTypeId())
                .type(BusinessRules.CAPACITY_VALIDATION_TYPE)
                .build();
    }

    private Mono<SendMessageResponse> sendToSQS(CapacityValidationRequestDTO request) {
        return Mono.fromCallable(() -> {
            try {
                String messageBody = objectMapper.writeValueAsString(request);
                SendMessageRequest sqsRequest = SendMessageRequest.builder()
                        .queueUrl(properties.queueUrl())
                        .messageBody(messageBody)
                        .build();
                return sqsRequest;
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error serializing debt capacity request", e);
            }
        })
        .flatMap(sqsRequest -> Mono.fromFuture(client.sendMessage(sqsRequest)));
    }
}