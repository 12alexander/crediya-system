package co.com.bancolombia.model.user;
import co.com.bancolombia.model.role.Role;
import co.com.bancolombia.model.constants.ValidationMessages;
import co.com.bancolombia.model.constants.BusinessRules;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private String id;
    private String name;
    private String lastName;
    private LocalDate birthDate;
    private String address;
    private String phone;
    private String emailAddress;
    private BigDecimal baseSalary;
    private String idRol;
    private Role role;
    private String password;

    public static class UserBuilder {
        public UserBuilder name(String name) {
            if(name == null || name.trim().isEmpty()){
                throw new IllegalArgumentException(ValidationMessages.NAME_REQUIRED);
            }
            this.name = name.trim();
            return this;
        }

        public UserBuilder lastName(String lastName) {
            if(lastName == null || lastName.trim().isEmpty()){
                throw new IllegalArgumentException(ValidationMessages.LASTNAME_REQUIRED);
            }
            this.lastName = lastName.trim();
            return this;
        }

        public UserBuilder emailAddress(String emailAddress) {
            if(emailAddress == null || emailAddress.trim().isEmpty()){
                throw new IllegalArgumentException(ValidationMessages.EMAIL_REQUIRED);
            }
            if(!emailAddress.matches(BusinessRules.EMAIL_REGEX)){
                throw new IllegalArgumentException(ValidationMessages.EMAIL_INVALID_FORMAT);
            }
            this.emailAddress = emailAddress;
            return this;
        }

        public UserBuilder baseSalary(BigDecimal baseSalary) {
            if(baseSalary == null || baseSalary.compareTo(BigDecimal.ZERO) < 0){
                throw new IllegalArgumentException(ValidationMessages.SALARY_NULL);
            }
            if(baseSalary.compareTo(BusinessRules.MAX_SALARY) > 0){
                throw new IllegalArgumentException(ValidationMessages.SALARY_MAX_EXCEEDED);
            }
            this.baseSalary = baseSalary;
            return this;
        }

        public UserBuilder idRol(String idRol) {
            if(idRol == null || idRol.trim().isEmpty()){
                throw new IllegalArgumentException(ValidationMessages.ROLE_ID_REQUIRED);
            }
            this.idRol = idRol.trim();
            return this;
        }

        public UserBuilder password(String password) {
            if(password == null || password.trim().isEmpty()){
                throw new IllegalArgumentException(ValidationMessages.PASSWORD_REQUIRED);
            }
            if(password.length() < BusinessRules.MIN_PASSWORD_LENGTH){
                throw new IllegalArgumentException(ValidationMessages.PASSWORD_MIN_LENGTH);
            }
            this.password = password;
            return this;
        }
    }

    public void  validateData(){
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationMessages.NAME_REQUIRED);
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationMessages.LASTNAME_REQUIRED);
        }

        if (emailAddress == null || emailAddress.trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationMessages.EMAIL_REQUIRED);
        }

        if (!emailAddress.matches(BusinessRules.EMAIL_REGEX)) {
            throw new IllegalArgumentException(ValidationMessages.EMAIL_INVALID_FORMAT);
        }

        if (baseSalary == null) {
            throw new IllegalArgumentException(ValidationMessages.SALARY_NULL);
        }

        if (baseSalary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(ValidationMessages.SALARY_ZERO);
        }

        if (baseSalary.compareTo(BusinessRules.MAX_SALARY) > 0) {
            throw new IllegalArgumentException(ValidationMessages.SALARY_MAX_EXCEEDED);
        }

        if (idRol == null || idRol.trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationMessages.ROLE_ID_REQUIRED);
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationMessages.PASSWORD_REQUIRED);
        }

        if (password.length() < BusinessRules.MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(ValidationMessages.PASSWORD_MIN_LENGTH);
        }

    }

}
