package com.mahak.capstone.interviewprocesstrackingsystem.validation;

import java.util.regex.Pattern;
import com.mahak.capstone.interviewprocesstrackingsystem.exception.InvalidRequestException;

/**
 * Global utility for common validation patterns.
 */
public class ValidationUtils {

    // Allows only letters, spaces, dots, and hyphens. Blocks numbers and special chars.
    private static final String NAME_REGEX = "^[a-zA-Z\\s.-]+$";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    /**
     * Validates that a string is a valid name (no numbers).
     * @param name The string to validate.
     * @param fieldName The name of the field for the error message.
     */
    public static void validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidRequestException(fieldName + " cannot be empty.");
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new InvalidRequestException(fieldName + " should contain only alphabets and spaces.");
        }
    }
}
