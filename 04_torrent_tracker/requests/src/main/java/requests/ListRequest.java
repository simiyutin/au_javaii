package requests;

import static requests.RequestType.LIST;

public class ListRequest {
    private final RequestType type = LIST;

    public RequestType getType() {
        return type;
    }
}
