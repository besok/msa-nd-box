package ie.home.msa.sandbox.discovery.server;

import java.util.Optional;

public enum LoadBalanceStrategy {
    ROBIN("round-robin"),RANDOM("random");

    private final String name;

    LoadBalanceStrategy(String name) {
        this.name = name;
    }

    public static Optional<LoadBalanceStrategy> from(String name){
        LoadBalanceStrategy[] values = LoadBalanceStrategy.values();
        for (LoadBalanceStrategy value : values) {
            if(value.name.equals(name)){
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
