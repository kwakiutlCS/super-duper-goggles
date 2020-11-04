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

import me.ricardo.playground.ir.api.entity.TimeDto;
import me.ricardo.playground.ir.api.validator.Time.TimeValidator;

@Documented
@Constraint(validatedBy = TimeValidator.class)
@Target( { ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Time {
    
    String message() default "field has wrong format";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    
    class TimeValidator implements ConstraintValidator<Time, TimeDto> {
        
        @Override
        public boolean isValid(final TimeDto value, final ConstraintValidatorContext context) {
            boolean valid = true;
            String error = null;
            
            if (value != null) {
                if (value.getUnit() != null && value.getStep() == null) {
                	error = "requires 'step' field, if 'unit' is present";
                	
                } else if (value.getUnit() == null && value.getStep() != null) {
                	error = "requires 'unit' field, if 'step' is present";
                
                } else if (value.getUnit() == null && (value.getBound() != null || value.getZone() != null || value.getExceptions() != null)) {
                	error = "requires 'bound', 'zone' and 'exceptions' to be null, if 'unit' is null";
                }
                
                if (error != null) {
                	valid = false;
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(error).addConstraintViolation();
                }
            }
            
            return valid;
        }
    }
}