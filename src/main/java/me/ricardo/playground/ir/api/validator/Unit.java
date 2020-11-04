package me.ricardo.playground.ir.api.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import me.ricardo.playground.ir.api.validator.Unit.UnitValidator;

@Documented
@Constraint(validatedBy = UnitValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Unit {
    
    String message() default "field must be one of DAYS, WEEKS, MONTHS or YEARS";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    
    class UnitValidator implements ConstraintValidator<Unit, String> {
        
    	private static final Set<String> units = Set.of(ChronoUnit.DAYS.name(), ChronoUnit.WEEKS.name(), ChronoUnit.MONTHS.name(), ChronoUnit.YEARS.name());
    	
        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext context) {
        	boolean valid = true;
        	
        	if (value != null) {
        		valid = units.contains(value);
        	}
        	
        	return valid;
        }
    }
}