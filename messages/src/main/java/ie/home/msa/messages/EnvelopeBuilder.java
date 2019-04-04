package ie.home.msa.messages;

public class EnvelopeBuilder {
    public static ServiceEnvelope serviceEnvelope(String data){
        return new ServiceEnvelope(data);
    }
}
