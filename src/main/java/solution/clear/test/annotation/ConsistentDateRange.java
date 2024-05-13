package solution.clear.test.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import solution.clear.test.validator.ConsistentDateRangeValidator;


@Target({ TYPE, FIELD, PARAMETER, METHOD, CONSTRUCTOR, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = ConsistentDateRangeValidator.class)
public @interface ConsistentDateRange {

    String message() default
        "End date must be after begin date.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
  
}
