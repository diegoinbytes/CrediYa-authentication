package co.com.pragma.api.exception;

public class ContractException {
    public static class RequestErrorException extends RuntimeException{
        public RequestErrorException(String message) {
            super(message);
        }
    }
}
