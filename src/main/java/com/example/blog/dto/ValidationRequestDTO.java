package com.example.blog.dto;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ValidationRequestDTO {
	
	@NotBlank
	String name;
	
	@Email
	String email;
	
	// I guess, since this is Java's string value, before it is parsed as regex,
	// you have to escape "\" anyway; if you have to escape "\" 
	// in the regex, not as a Java String value, then will you have to use
	// \\\\ or something like that? IWAAIL.
	@Pattern(regexp = "01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$")
	String phoneNumber;
	
	@Min(value = 20) @Max(value = 40)
	int age;
	
	@Size(min = 0, max = 40)
	String description;
	
	@Positive
	int count;
	
	@AssertTrue
	boolean booleanCheck;


}
