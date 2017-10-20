package com.lashou.pushlib.service;

/**
 * Created by lixinli on 2017/8/2.
 */
class DataPacket {
    private int packet;
    private int header;
    private int version;
    private int operation;
    private int sequence;
    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getHeader() {
        return header;
    }

    public void setHeader(int header) {
        this.header = header;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public int getPacket() {
        return packet;
    }

    public void setPacket(int packet) {
        this.packet = packet;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "DataPacket{" +
                ", packet=" + packet +
                ", header=" + header +
                ", version=" + version +
                ", operation=" + operation +
                ", sequence=" + sequence +
                "body='" + body + '\'' +
                '}';
    }
}
