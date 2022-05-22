package jvmdog.protocol.api.model;

import java.nio.charset.Charset;

public class DogMessage {
    private int version;
    private int type;
    private int headerLength = 0;
    private int dataLength = 0;
    private byte[] header;
    private byte[] data;

    public static DogMessage clientResponse(String header) {
        DogMessage dogMessage = from(DogMessageType.CLIENT_COMMAND_RESPONSE.getValue());
        dogMessage.setHeader(header.getBytes(Charset.forName("utf-8")));

        return dogMessage;
    }

    public static DogMessage clientCommand() {
        return from(DogMessageType.CLIENT_COMMAND.getValue());
    }

    public static DogMessage clientCommandStop() {
        return from(DogMessageType.CLIENT_COMMAND_STOP.getValue());
    }

    public static DogMessage clientCommandStopResponse(String header) {
        DogMessage dogMessage = from(DogMessageType.CLIENT_COMMAND_STOP_RESPONSE.getValue());
        dogMessage.setHeader(header.getBytes(Charset.forName("utf-8")));

        return dogMessage;
    }

    public static DogMessage registration() {
        return from(DogMessageType.REGISTRATION.getValue());
    }

    public static DogMessage from(int type) {
        DogMessage message = new DogMessage();
        message.setVersion(Version.CURRENT_VERSION);
        message.setType(type);

        return message;
    }
    

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        if (data == null) {
            dataLength = 0;
        } else {
            dataLength = data.length;
        }
        this.data = data;
    }

    public int getHeaderLength() {
        return headerLength;
    }

    public int getDataLength() {
        return dataLength;
    }

    public byte[] getHeader() {
        return header;
    }

    public void setHeader(byte[] header) {
        if (header == null) {
            headerLength = 0;
        } else {
            headerLength = header.length;
        }
        this.header = header;
    }

}
