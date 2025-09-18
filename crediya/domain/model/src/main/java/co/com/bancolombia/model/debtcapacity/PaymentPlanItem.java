package co.com.bancolombia.model.debtcapacity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
public class PaymentPlanItem {

    private Integer month;
    private BigDecimal capital;
    private BigDecimal interest;
    private BigDecimal total;
    private BigDecimal balance;

    public static PaymentPlanItem create(Integer month, BigDecimal capital, BigDecimal interest, BigDecimal balance) {
        BigDecimal total = capital.add(interest);
        return PaymentPlanItem.builder()
                .month(month)
                .capital(capital)
                .interest(interest)
                .total(total)
                .balance(balance)
                .build();
    }
}