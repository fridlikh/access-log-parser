package Exceptions;

public class InvalidLogFormatException extends RuntimeException {
    public InvalidLogFormatException(String message) {
        super(message);
    }
}