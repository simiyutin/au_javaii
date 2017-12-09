package requests;

import java.io.OutputStream;

public interface TrackerRequest {
    void execute(OutputStream os);
}
