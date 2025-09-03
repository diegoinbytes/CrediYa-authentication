package co.com.bancolombia.api.exception;

public class ContractException {
    public static class RequestErrorException extends RuntimeException{
        public RequestErrorException(String message) {
            super(message);
        }
    }
}
