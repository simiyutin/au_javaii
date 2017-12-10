package requests;

import java.security.InvalidParameterException;

public enum RequestType {
    LIST(1),
    UPLOAD(2),
    SOURCES(3),
    UPDATE(4),
    STAT(5),
    GET(6);

    private final int value;

    RequestType(int value) {
        this.value = value;
    }

    public static RequestType valueOf(int value) {
        switch (value) {
            case 1:
                return LIST;
            case 2:
                return UPLOAD;
            case 3:
                return SOURCES;
            case 4:
                return UPDATE;
            case 5:
                return STAT;
            case 6:
                return GET;
            default:
                throw new InvalidParameterException();
        }
    }

    public int getValue() {
        return value;
    }
}
