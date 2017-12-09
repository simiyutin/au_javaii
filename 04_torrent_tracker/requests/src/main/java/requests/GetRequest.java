package requests;

import static requests.RequestType.GET;

public class GetRequest {
    private final RequestType type = GET;
    private final int id;
    private final int part;

    public GetRequest(int id, int part) {
        this.id = id;
        this.part = part;
    }

    public RequestType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public int getPart() {
        return part;
    }
}
