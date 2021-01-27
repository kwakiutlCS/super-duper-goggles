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
import me.ricardo.playground.ir.domain.entity.repetion.Bound.BoundType;
import me.ricardo.playground.ir.domain.validation.Bounded.BoundValidator;

@Documented
@Constraint(validatedBy = BoundValidator.class)
@Target( { ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Bounded {
    
    String message() default "field must have a bound";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    
    class BoundValidator implements ConstraintValidator<Bounded, Bound> {
        
        @Override
        public boolean isValid(final Bound value, final ConstraintValidatorContext context) {
        	boolean valid = true;
        	
        	if (value != null) {
        	   valid = value.type() != BoundType.NO_BOUND; 
        	}
        	
        	return valid;
        }
    }
}