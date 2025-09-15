package co.com.bancolombia.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDecisionRequestDTO {
    
    @NotBlank(message = "La decisión es obligatoria")
    @Pattern(regexp = "APPROVED|REJECTED", message = "La decisión debe ser APPROVED o REJECTED")
    @JsonProperty("decision")
    private String decision;
    
    @JsonProperty("comments")
    private String comments;
}