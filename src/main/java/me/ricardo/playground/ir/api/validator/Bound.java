package me.ricardo.playground.ir.api.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import me.ricardo.playground.ir.api.entity.BoundDto;
import me.ricardo.playground.ir.api.validator.Bound.BoundValidator;

@Documented
@Constraint(validatedBy = BoundValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Bound {
    
    String message() default "field must contain limit or timestamp, but not both";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    
    class BoundValidator implements ConstraintValidator<Bound, BoundDto> {
        
        @Override
        public boolean isValid(final BoundDto value, final ConstraintValidatorContext context) {
        	boolean valid = true;
        	
        	if (value != null) {
        		valid = value.getLimit() != null ^ value.getTimestamp() != null;
        	}
        	
        	return valid;
        }
    }
}