package ru.yandex.practicum.filmorate.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Documented;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomAfterDateValidator.class)
@Documented
public @interface CustomAfterDate {
    String minDate();
    String formatDate();

    String message() default "Неверная дата!";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}