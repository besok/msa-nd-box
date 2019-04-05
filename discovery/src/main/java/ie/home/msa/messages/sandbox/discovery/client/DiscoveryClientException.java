package ie.home.msa.messages.sandbox.discovery.client;

import org.junit.runner.RunWith;

public class DiscoveryClientException extends RuntimeException {
    public DiscoveryClientException() {
    }

    public DiscoveryClientException(String message) {
        super(message);
    }

    public DiscoveryClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public DiscoveryClientException(Throwable cause) {
        super(cause);
    }
}
