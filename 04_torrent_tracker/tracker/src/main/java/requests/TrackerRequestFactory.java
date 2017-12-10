package requests;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TrackerRequestFactory {
    public static TrackerRequest parseRequest(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        RequestType type = RequestType.valueOf(dis.readInt());
        switch (type) {
            case LIST:
                return new TrackerListRequest();
            default:
                return null;
        }
    };
}
