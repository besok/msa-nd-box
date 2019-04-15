package ie.home.msa.sandbox.discovery.server;

import java.util.List;

public class PlainValueFileStorage extends AbstractFileStorage<String> {
    public PlainValueFileStorage(String directory, StorageListenerHandler handler) {
        super(directory, handler);
    }

    @Override
    protected List<String> fromFile(List<String> params) {
        return params;
    }

    @Override
    protected List<String> toFile(List<String> params) {
        return params;
    }

    @Override
    protected boolean equal(String left, String right) {
        return left.equals(right);
    }
}
