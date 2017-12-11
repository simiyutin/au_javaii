package requests;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ClientRequestCallbackFactory {
    public static ClientRequestCallback parseRequest(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        RequestType type = RequestType.valueOf(dis.readInt());
        switch (type) {
            case STAT: {
                StatRequest request = StatRequest.parse(is);
                return new StatRequestCallback(request);
            }
            case GET: {
                GetRequest request = GetRequest.parse(is);
                return new GetRequestCallback(request);
            }
            default:
                throw new IOException("Unknown request type");
        }
    }
}
