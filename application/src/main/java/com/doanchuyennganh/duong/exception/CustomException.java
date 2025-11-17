package com.doanchuyennganh.duong.exception;

public class CustomException {

    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message) {
            super(message);
        }
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public static class SettingsNotFoundException extends RuntimeException {
        public SettingsNotFoundException(String message) {
            super(message);
        }
    }

    public static class DailyWordException extends RuntimeException {
        public DailyWordException(String message) {
            super(message);
        }
    }

    public static class InvalidSettingsException extends RuntimeException {
        public InvalidSettingsException(String message) {
            super(message);
        }
    }
}
