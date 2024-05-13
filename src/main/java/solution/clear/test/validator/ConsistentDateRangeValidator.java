package solution.clear.test.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import java.time.LocalDate;
import solution.clear.test.annotation.ConsistentDateRange;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class ConsistentDateRangeValidator implements ConstraintValidator<ConsistentDateRange, Object[]> {

    @Override
    public boolean isValid(Object[] value, ConstraintValidatorContext context) {
        if ((value[0] == null && value[1] == null)
            || (value[0] == null && (value[1] instanceof LocalDate to))
            || (value[1] == null && (value[0] instanceof LocalDate to)))
            return true;
        if (!(value[0] instanceof LocalDate from) 
           || !(value[1] instanceof LocalDate to))
            throw new IllegalArgumentException("Two parameters must be LocalDate type.");
        return to.isAfter(from);
    }

}
