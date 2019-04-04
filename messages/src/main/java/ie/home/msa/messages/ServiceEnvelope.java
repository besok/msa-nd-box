package ie.home.msa.messages;

public class ServiceEnvelope extends AbstractEnvelope<ServiceMessage> {

    private ServiceMessage message;
    private long version;
    private State state;

    public ServiceEnvelope(String data) {
        version = 0;
        state = State.READY;
        message = Message.build(data, Message.Type.SERVICE);
    }

    @Override
    ServiceMessage message() {
        return message;
    }

    @Override
    protected void setVersion(long v) {
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
    protected void setState(State state) {
        this.state=state;
    }
}
