package co.com.bancolombia.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDTO {
    
    private String code;
    private String message;
    private String path;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    private List<ValidationErrorDTO> validationErrors;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ValidationErrorDTO {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}