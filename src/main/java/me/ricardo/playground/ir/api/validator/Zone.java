package me.ricardo.playground.ir.api.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.DateTimeException;
import java.time.ZoneId;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import me.ricardo.playground.ir.api.validator.Zone.UnitValidator;

@Documented
@Constraint(validatedBy = UnitValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Zone {
    
    String message() default "field must be a valid timezone id";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    
    class UnitValidator implements ConstraintValidator<Zone, String> {
        
        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext context) {
        	boolean valid = true;
        	
        	if (value != null) {
        		try {
        			ZoneId.of(value);
        		} catch (DateTimeException e) {
        			valid = false;
        		}
        	}
        	
        	return valid;
        }
    }
}