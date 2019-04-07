package ie.home.msa.messages;

import java.util.Map;

public class ServiceEnvelope extends AbstractEnvelope<ServiceMessage> {

    private ServiceMessage message;
    private long version;
    private State state;

    public ServiceEnvelope() {
    }

    public ServiceEnvelope(ServiceMessage.Metrics metrics) {
        version = 0;
        state = State.READY;
        message = Message.build(metrics, Message.Type.SERVICE_METRICS);
    }
    public ServiceEnvelope(Map<String,Integer> metrics) {
        version = 0;
        state = State.READY;
        message = Message.build(ServiceMessage.Metrics.from(metrics), Message.Type.SERVICE_METRICS);
    }

    @Override
    public ServiceMessage getMessage() {
        return message;
    }

    @Override
    public void setMessage(ServiceMessage message) {
        this.message = message;
    }

    @Override
    public void setVersion(long v) {
        this.version=v;
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state=state;
    }
}
