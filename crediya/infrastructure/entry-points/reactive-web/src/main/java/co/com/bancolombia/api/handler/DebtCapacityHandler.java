package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.request.CapacityValidationRequestDTO;
import co.com.bancolombia.api.dto.response.DebtCapacityResponseDTO;
import co.com.bancolombia.model.constants.ValidationMessages;
import co.com.bancolombia.model.debtcapacity.DebtCapacity;
import co.com.bancolombia.usecase.debtcapacity.interfaces.IDebtCapacityUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DebtCapacityHandler {

    private final IDebtCapacityUseCase debtCapacityUseCase;
    private final Validator validator;

    public Mono<ServerResponse> calculateDebtCapacity(ServerRequest request) {
        return request.bodyToMono(CapacityValidationRequestDTO.class)
                .doOnNext(this::validateRequest)
                .flatMap(this::processDebtCapacityCalculation)
                .flatMap(this::buildSuccessResponse)
                .onErrorResume(this::handleError)
                .doOnSuccess(response -> log.info(ValidationMessages.DEBT_CAPACITY_PROCESSED))
                .doOnError(error -> log.error(ValidationMessages.CAPACITY_CALCULATION_FAILED, error));
    }

    private void validateRequest(CapacityValidationRequestDTO requestDTO) {
        Set<ConstraintViolation<CapacityValidationRequestDTO>> violations = validator.validate(requestDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private Mono<DebtCapacity> processDebtCapacityCalculation(CapacityValidationRequestDTO requestDTO) {
        DebtCapacity debtCapacity = DebtCapacity.fromValidationRequest(
                requestDTO.getOrderId(),
                requestDTO.getUserId(),
                requestDTO.getAmount(),
                requestDTO.getDeadline(),
                requestDTO.getEmailAddress(),
                requestDTO.getBaseSalary(),
                requestDTO.getInterestRate(),
                requestDTO.getLoanTypeId()
        );

        return debtCapacityUseCase.processDebtCapacityRequest(debtCapacity);
    }

    private Mono<ServerResponse> buildSuccessResponse(DebtCapacity debtCapacity) {
        DebtCapacityResponseDTO responseDTO = mapToResponseDTO(debtCapacity);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(responseDTO);
    }

    private DebtCapacityResponseDTO mapToResponseDTO(DebtCapacity debtCapacity) {
        return DebtCapacityResponseDTO.builder()
                .id(debtCapacity.getId())
                .orderId(debtCapacity.getOrderId())
                .decision(debtCapacity.getStatus().getName())
                .availableCapacity(debtCapacity.getAvailableCapacity())
                .monthlyPayment(debtCapacity.getMonthlyPayment())
                .currentMonthlyDebt(debtCapacity.getCurrentMonthlyDebt())
                .maxDebtCapacity(debtCapacity.getMaxDebtCapacity())
                .reason(debtCapacity.getReason())
                .requestDate(debtCapacity.getRequestDate())
                .processingDate(debtCapacity.getProcessingDate())
                .build();
    }

    private Mono<ServerResponse> handleError(Throwable error) {
        log.error(ValidationMessages.CAPACITY_CALCULATION_FAILED, error);
        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ValidationMessages.CAPACITY_CALCULATION_FAILED);
    }
}