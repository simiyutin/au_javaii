package com.simiyutin.javaii;

public enum RequestType {
    LIST(1), GET(2);

    private final int value;

    RequestType(int value) {
        this.value = value;
    }

    public static RequestType valueOf(int value) {
        switch (value) {
            case 1:
                return LIST;
            case 2:
                return GET;
            default:
                return null;
        }
    }

    public int getValue() {
        return value;
    }
}
