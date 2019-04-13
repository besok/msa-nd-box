package ie.home.msa.sandbox.discovery.server;

public interface Handler<I,O> {
    O handle(I val);
}
