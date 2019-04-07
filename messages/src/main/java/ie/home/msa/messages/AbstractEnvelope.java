package ie.home.msa.messages;

public abstract class AbstractEnvelope<T extends Message<?>> {
    public abstract T getMessage();
    public abstract void setMessage(T message);
    public abstract void setVersion(long v);

    public abstract long getVersion();

    public abstract State getState();

    public abstract void setState(State state);

    protected <E extends AbstractEnvelope> E next() {
        this.setState(this.getState().next());
        this.setVersion(getVersion() + 1);
        return (E) this;
    }


    enum State {
        READY(1), SEND(2), RECEIVED(0);
        private int next;

        State(int next) {
            this.next = next;
        }

        public State next() {
            return State.values()[next];
        }
    }

}
