package com.lashou.pushlib.utils;

import java.nio.ByteBuffer;


public class ByteUtil {
      
    private static ByteBuffer buffer = ByteBuffer.allocate(8);      
  
//    public static void main(String[] args) {
//
//        //测试 int 转 byte
//        int int0 = 234;
//        byte byte0 = intToByte(int0);
//        System.out.println("byte0=" + byte0);//byte0=-22
//        //测试 byte 转 int
//        int int1 = byteToInt(byte0);
//        System.out.println("int1=" + int1);//int1=234
//
//
//
//        //测试 int 转 byte 数组
//        int int2 = 1417;
//        byte[] bytesInt = intToByteArray(int2);
//        System.out.println("bytesInt=" + bytesInt);//bytesInt=[B@de6ced
//        //测试 byte 数组转 int
//        int int3 = byteArrayToInt(bytesInt);
//        System.out.println("int3=" + int3);//int3=1417
//
//
//        //测试 long 转 byte 数组
//        long long1 = 2223;
//        byte[] bytesLong = longToBytes(long1);
//        System.out.println("bytes=" + bytesLong);//bytes=[B@c17164
//        //测试 byte 数组 转 long
//        long long2 = bytesToLong(bytesLong);
//        System.out.println("long2=" + long2);//long2=2223
//
//
//        format();
//    }

//    private static void format() {
//        String bodyStr = "hello server";
//
//        byte[] body = bodyStr.getBytes();
//        int i = body.length+16;
//        byte[] packet = intToByteArray(i);
//        printByteArray(packet);
//        byte[] header = intTo2ByteArray(16);
//        printByteArray(header);
//        byte[] version = intTo2ByteArray(1);
//        printByteArray(version);
//        byte[] operation = intToByteArray(2);
//        printByteArray(operation);
//        byte[] sequence = intToByteArray(1);
//        printByteArray(sequence);
//
//    }

//    //字节数组组合操作1
//    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
//        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
//        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
//        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
//        return byte_3;
//    }
//
//    //字节数组组合操作2
//    public static byte[] byteMerger(byte[][] byteList) {
//
//        int length = 0;
//        for (int i = 0; i < byteList.length; i++) {
//            length += byteList[i].length;
//        }
//        byte[] result = new byte[length];
//
//        int index = 0;
//        for (int i = 0; i < byteList.length; i++) {
//            byte[] nowByte = byteList[i];
//            for (int k = 0; k < byteList[i].length; k++) {
//                result[index] = nowByte[k];
//                index++;
//            }
//        }
//        for (int i = 0; i < index; i++) {
//            // CommonUtils.LogWuwei("", "result[" + i + "] is " + result[i]);
//        }
//        return result;
//    }

    public static void printByteArray(byte[] bytesInt) {
        printByteArray(bytesInt,bytesInt.length);
    }
    public static void printByteArray(byte[] bytesInt,int length) {
        if(length<=0 || length>bytesInt.length){
            length = bytesInt.length;
        }
        StringBuilder sb = new StringBuilder();
        for(int i= 0 ;i<length;i++){
            sb.append(" "+bytesInt[i]);
        }
        Logger.d(sb.toString());
    }


    //byte 与 int 的相互转换  
    public static byte intToByte(int x) {  
        return (byte) x;  
    }  
      
    public static int byteToInt(byte b) {  
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值  
        return b & 0xFF;  
    }  
      
    //byte 数组与 int 的相互转换  
    public static int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF |  
                (b[2] & 0xFF) << 8 |  
                (b[1] & 0xFF) << 16 |  
                (b[0] & 0xFF) << 24;  
    }
    public static int byte2ArrayToInt(byte[] b) {
        return   b[1] & 0xFF |
                (b[0] & 0xFF) << 8;
    }
    public static byte[] intTo2ByteArray(int a) {
        return new byte[] {
//                (byte) ((a >> 24) & 0xFF),
//                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }
    public static byte[] intToByteArray(int a) {  
        return new byte[] {  
            (byte) ((a >> 24) & 0xFF),  
            (byte) ((a >> 16) & 0xFF),     
            (byte) ((a >> 8) & 0xFF),     
            (byte) (a & 0xFF)  
        };  
    }  
  
    //byte 数组与 long 的相互转换  
    public static byte[] longToBytes(long x) {  
        buffer.putLong(0, x);  
        return buffer.array();  
    }  
  
    public static long bytesToLong(byte[] bytes) {  
        buffer.put(bytes, 0, bytes.length);  
        buffer.flip();//need flip   
        return buffer.getLong();  
    }  
  
}  