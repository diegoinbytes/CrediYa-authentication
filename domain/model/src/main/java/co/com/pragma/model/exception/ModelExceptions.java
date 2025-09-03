package co.com.pragma.model.exception;

public class ModelExceptions{
    public static class ExistEmailException extends RuntimeException {
        public ExistEmailException(String message) {
            super(message);
        }
    }
    public static class BasedSalaryNotValidException extends RuntimeException{
        public BasedSalaryNotValidException(String message) {
            super(message);
        }
    }
}

