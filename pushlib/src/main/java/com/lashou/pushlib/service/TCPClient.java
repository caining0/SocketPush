package com.lashou.pushlib.service;


import com.lashou.pushlib.utils.ByteUtil;
import com.lashou.pushlib.utils.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.lashou.pushlib.utils.ByteUtil.printByteArray;


public class TCPClient {

    public static final int millis = 1000 * 60;
    public static int sequence = 0;

    public static DataPacket read(DataInputStream dis) throws IOException {
        Logger.d("-------read start------------------------------");
        DataPacket returnStr = null;
        try {
            byte[] b = new byte[512];
            int tempbyte = dis.read(b);
            if(tempbyte>0){
                byte[] btye = new byte[tempbyte];
                System.arraycopy(b, 0, btye, 0, tempbyte);
                printByteArray(btye);
                returnStr = parseData(btye);
            }else {
                Logger.d("tempbyte" + tempbyte);
                printByteArray(b);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        Logger.d("-------read end------------------------------");
        return returnStr;
    }

    private static DataPacket parseData(byte[] bytes){
        DataPacket dataPacket = null;
        String bodyStr = null;
        if(bytes == null){
            Logger.d("数据为null");
            return dataPacket;
        }
        if( bytes.length >= packet_size_empty){
            byte[] dataHeader = new byte[packet_size_empty];
            System.arraycopy(bytes, 0, dataHeader, 0, packet_size_empty);
            byte[] packet = new byte[4];
            System.arraycopy(dataHeader, 0, packet, 0, 4);
            byte[] header = new byte[2];
            System.arraycopy(dataHeader, 4, header, 0, 2);
            byte[] version = new byte[2];
            System.arraycopy(dataHeader, 6, version, 0, 2);
            byte[] operation = new byte[4];
            System.arraycopy(dataHeader, 8, operation, 0, 4);
            byte[] sequence = new byte[4];
            System.arraycopy(dataHeader, 12, sequence, 0, 4);


            int packeti = ByteUtil.byteArrayToInt(packet);
            int headeri = ByteUtil.byte2ArrayToInt(header);
            int versioni =   ByteUtil.byte2ArrayToInt(version);
            int operationi = ByteUtil.byteArrayToInt(operation);
            int sequencei = ByteUtil.byteArrayToInt(sequence);
            if(packeti == bytes.length){
                dataPacket = new DataPacket();
                dataPacket.setHeader(headeri);
                dataPacket.setPacket(packeti);
                dataPacket.setVersion(versioni);
                dataPacket.setOperation(operationi);
                dataPacket.setSequence(sequencei);
                if(packeti > packet_size_empty){
                    byte[] body = new byte[packeti - packet_size_empty];
                    System.arraycopy(bytes, packet_size_empty, body, 0, packeti - packet_size_empty);
                    bodyStr = new String(body);
                    dataPacket.setBody(bodyStr);
                }
                switch (operationi){
                    case message_operation_auth_return:
                        Logger.d("autn:" + bodyStr);
                        break;
                    case message_operation_heartbeat_return:
                        Logger.d("heartbeat:" + bodyStr);
                        break;
                    case message_operation_push:
                        Logger.d("push:" + bodyStr);
                        break;
                }
            }else{
                Logger.d("解析数据异常 parse length="+ packeti+",,receive length="+bytes.length);
            }
        }

        return dataPacket;
    }

    public static byte[] heartbeatWithMessage(String bodyStr) {

        byte[] body = bodyStr.getBytes();
        int i = body.length+packet_size_empty;
        byte[] packet = ByteUtil.intToByteArray(i);
        byte[] header = ByteUtil.intTo2ByteArray(16);
        byte[] version = ByteUtil.intTo2ByteArray(1);
        byte[] operation = ByteUtil.intToByteArray(2);
        byte[] sequence = ByteUtil.intToByteArray(TCPClient.sequence);
        TCPClient.sequence++;

        List<byte[]> list = new ArrayList<byte[]>();
        list.add(packet);
        list.add(header);
        list.add(version);
        list.add(operation);
        list.add(sequence);
        list.add(body);
        byte[] bytes = byteMerger(list);
        Logger.d( "信息包");//bytesInt=[B@de6ced
        printByteArray(bytes);
        return bytes;

    }
    public static byte[] auth(String key) {
        byte[] body = key.getBytes();
        byte[] packet = ByteUtil.intToByteArray(packet_size_empty+body.length);
        byte[] header = ByteUtil.intTo2ByteArray(16);
        byte[] version = ByteUtil.intTo2ByteArray(1);
        byte[] operation = ByteUtil.intToByteArray(7);
        byte[] sequence = ByteUtil.intToByteArray(TCPClient.sequence);
        TCPClient.sequence++;

        List<byte[]> list = new ArrayList<byte[]>();
        list.add(packet);
        list.add(header);
        list.add(version);
        list.add(operation);
        list.add(sequence);
        list.add(body);
        byte[] bytes = byteMerger(list);
        Logger.d( "auth包");//bytesInt=[B@de6ced
        printByteArray(bytes);
        return bytes;

    }
    public static final int packet_size_empty = 16;
    public static final int message_operation_auth = 7;
    public static final int message_operation_auth_return = 8;
    public static final int message_operation_heartbeat = 2;
    public static final int message_operation_heartbeat_return = 3;
    public static final int message_operation_push = 5;
    public static byte[] heartbeat(String key) {

        byte[] body = key.getBytes();
        byte[] packet = ByteUtil.intToByteArray(packet_size_empty+body.length);
        byte[] header = ByteUtil.intTo2ByteArray(16);
        byte[] version = ByteUtil.intTo2ByteArray(1);
        byte[] operation = ByteUtil.intToByteArray(2);
        byte[] sequence = ByteUtil.intToByteArray(TCPClient.sequence);
        TCPClient.sequence++;

        List<byte[]> list = new ArrayList<byte[]>();
        list.add(packet);
        list.add(header);
        list.add(version);
        list.add(operation);
        list.add(sequence);
        list.add(body);
        byte[] bytes = byteMerger(list);
        Logger.d( "心跳包");
        printByteArray(bytes);
        return bytes;

    }

    //字节数组组合操作2
    private static byte[] byteMerger(List<byte[]> byteList) {

        int length = 0;
        for (int i = 0; i < byteList.size(); i++) {
            length += byteList.get(i).length;
        }
        byte[] result = new byte[length];

        int index = 0;
        for (int i = 0; i < byteList.size(); i++) {
            byte[] nowByte = byteList.get(i);
            for (int k = 0; k < nowByte.length; k++) {
                result[index] = nowByte[k];
                index++;
            }
        }
//        for (int i = 0; i < index; i++) {
//            CommonUtils.LogWuwei("", "result[" + i + "] is " + result[i]);
//        }
        return result;
    }
}
