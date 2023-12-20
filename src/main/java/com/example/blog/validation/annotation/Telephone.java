package com.example.blog.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TelephoneValidator.class)
public @interface Telephone {
	
	String message() default "전화번호 형식 일치하지 않음.";
	
	Class[] groups() default {};
	
	Class[] payload() default {};

}
