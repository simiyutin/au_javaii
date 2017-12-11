package requests;

import java.io.*;

public class GetResponse {
    private final FilePart filePart;

    public GetResponse(FilePart filePart) {
        this.filePart = filePart;
    }

    public FilePart getFilePart() {
        return filePart;
    }

    public static GetResponse parse(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        int size = dis.readInt();
        byte bytes[] = new byte[size];
        dis.read(bytes, 0, size);
        FilePart filePart = new FilePart(bytes);
        return new GetResponse(filePart);
    }

    public void dump(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(getFilePart().getSize());
        byte[] buffer = new byte[1024];
        int bytesRead;
        while((bytesRead = getFilePart().getPartStream().read(buffer)) != -1) {
            os.write(buffer,0, bytesRead);
        }
    }
}
