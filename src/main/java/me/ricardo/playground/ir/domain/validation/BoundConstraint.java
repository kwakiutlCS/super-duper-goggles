package me.ricardo.playground.ir.domain.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import me.ricardo.playground.ir.domain.entity.repetion.Bound;
import me.ricardo.playground.ir.domain.validation.BoundConstraint.BoundValidator;

@Documented
@Constraint(validatedBy = BoundValidator.class)
@Target( { ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface BoundConstraint {
    
    String message() default "field is invalid";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    
    class BoundValidator implements ConstraintValidator<BoundConstraint, Bound> {
        
        @Override
        public boolean isValid(final Bound value, final ConstraintValidatorContext context) {
        	boolean valid = true;
        	
        	switch(value.type()) {
            case COUNT_BOUND:
                valid = value.limit() > 0 && value.timestamp() == 0;
                break;
            case NO_BOUND:
                valid = value.limit() == 0 && value.timestamp() == 0;
                break;
            case TIMESTAMP_BOUND:
                valid = value.limit() == 0 && value.timestamp() > 0;
                break;
            default:
                break;
        	
        	}
        	
        	return valid;
        }
    }
}