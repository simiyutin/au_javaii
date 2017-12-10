package requests;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TrackerRequestFactory {
    public static RequestCallback parseRequest(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        RequestType type = RequestType.valueOf(dis.readInt());
        switch (type) {
            case LIST:
                return new ListRequestCallback();
            case UPLOAD: {
                UploadRequest request = UploadRequest.parse(is);
                return new UploadRequestCallback(request);
            }
            case SOURCES: {
                SourcesRequest request = SourcesRequest.parse(is);
                return new SourcesRequestCallback(request);
            }
            case UPDATE: {
                UpdateRequest request = UpdateRequest.parse(is);
                return new UpdateRequestCallback(request);
            }

            default:
                return null;
        }
    };
}
