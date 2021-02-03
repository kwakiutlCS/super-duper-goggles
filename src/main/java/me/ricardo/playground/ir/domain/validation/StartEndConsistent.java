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

import me.ricardo.playground.ir.domain.entity.bound.Bound.BoundType;
import me.ricardo.playground.ir.domain.entity.repetition.DailyRepetition;
import me.ricardo.playground.ir.domain.validation.StartEndConsistent.StartEndConsistentValidator;

@Documented
@Constraint(validatedBy = StartEndConsistentValidator.class)
@Target( { ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface StartEndConsistent {
    
    String message() default "field is invalid";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    
    class StartEndConsistentValidator implements ConstraintValidator<StartEndConsistent, DailyRepetition> {
        
        @Override
        public boolean isValid(final DailyRepetition value, final ConstraintValidatorContext context) {
        	boolean valid = true;
        	
        	if (value != null && value.getBound() != null && value.getBound().type() == BoundType.TIMESTAMP_BOUND && value.getBound().timestamp() <= value.getStart()) {
        	    valid = false;
        	    context.disableDefaultConstraintViolation();
        	    context.buildConstraintViolationWithTemplate("must have timestamp bound after start value").addConstraintViolation();
        	}

        	return valid;
        }
    }
}