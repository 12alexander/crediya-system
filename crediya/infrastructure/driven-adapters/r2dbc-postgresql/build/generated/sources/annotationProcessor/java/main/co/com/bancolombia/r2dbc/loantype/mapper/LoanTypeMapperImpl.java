package co.com.bancolombia.r2dbc.loantype.mapper;

import co.com.bancolombia.model.loantype.LoanType;
import co.com.bancolombia.r2dbc.loantype.data.LoanTypeData;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-08T22:26:56-0500",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.3.jar, environment: Java 17.0.16 (Ubuntu)"
)
@Component
public class LoanTypeMapperImpl implements LoanTypeMapper {

    @Override
    public LoanType toDomain(LoanTypeData loanTypeData) {
        if ( loanTypeData == null ) {
            return null;
        }

        LoanType.LoanTypeBuilder loanType = LoanType.builder();

        loanType.id( loanTypeData.getId() );
        loanType.name( loanTypeData.getName() );
        loanType.minimumAmount( loanTypeData.getMinimumAmount() );
        loanType.maximumAmount( loanTypeData.getMaximumAmount() );
        loanType.interestRate( loanTypeData.getInterestRate() );
        loanType.automaticValidation( loanTypeData.getAutomaticValidation() );

        return loanType.build();
    }

    @Override
    public LoanTypeData toData(LoanType loanType) {
        if ( loanType == null ) {
            return null;
        }

        LoanTypeData.LoanTypeDataBuilder loanTypeData = LoanTypeData.builder();

        loanTypeData.id( loanType.getId() );
        loanTypeData.name( loanType.getName() );
        loanTypeData.minimumAmount( loanType.getMinimumAmount() );
        loanTypeData.maximumAmount( loanType.getMaximumAmount() );
        loanTypeData.interestRate( loanType.getInterestRate() );
        loanTypeData.automaticValidation( loanType.getAutomaticValidation() );

        return loanTypeData.build();
    }
}
