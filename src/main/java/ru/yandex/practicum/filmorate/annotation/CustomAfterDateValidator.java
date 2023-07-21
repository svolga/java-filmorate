package ru.yandex.practicum.filmorate.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class CustomAfterDateValidator implements ConstraintValidator<CustomAfterDate, LocalDate> {

    private String minDate;
    private String formatDate;

    @Override
    public void initialize(CustomAfterDate annotation) {
        ConstraintValidator.super.initialize(annotation);
        minDate = annotation.minDate();
        formatDate = annotation.formatDate();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        LocalDate minLocalDate = LocalDate.parse(minDate, DateTimeFormatter.ofPattern(formatDate));
        return value.isAfter(minLocalDate);
    }
}