package requests;

import java.util.List;

public class StatResponse {
    private final List<Integer> parts;

    public StatResponse(List<Integer> parts) {
        this.parts = parts;
    }

    public int getCount() {
        return parts.size();
    }

    public List<Integer> getParts() {
        return parts;
    }
}
