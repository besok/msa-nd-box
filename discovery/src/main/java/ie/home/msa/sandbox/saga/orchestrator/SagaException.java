package ie.home.msa.sandbox.saga.orchestrator;

public class SagaException extends RuntimeException {
    public SagaException(String s) {
        super(s);
    }
}
