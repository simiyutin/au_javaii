package requests;

public class UpdateResponse {
    private final boolean status;

    public UpdateResponse(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }
}
