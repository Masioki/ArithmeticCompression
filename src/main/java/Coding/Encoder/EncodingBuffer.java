package Coding.Encoder;

import Coding.CodingListener;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Buffer for bits send by Encoder.
 */
public class EncodingBuffer extends Thread implements CodingListener {
    private long length;
    private List<Byte> bytes;
    private final byte[] buffer;
    private byte bufferUsed;
    private volatile boolean end;

    public EncodingBuffer() {
        length = 0;
        buffer = new byte[8];
        bufferUsed = 0;
        end = false;
    }

    private void clearBuffer() {
        Arrays.fill(buffer, (byte) 0);
        bufferUsed = 0;
    }

    public List<Byte> getBytes() {
        return bytes;
    }


    @Override
    public void start(long lengthInBytes) {
        clearBuffer();
        length = lengthInBytes;
        bytes = new ArrayList<>();
        byte[] arr = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt((int) length).array();
        for (byte b : arr) bytes.add(b);
    }

    @Override
    public void next(byte b) {
        byte result = 0;
        if (bufferUsed == 7) {
            buffer[7] = b;
            for (byte i : buffer) {
                result = (byte) (result << 1);
                result += i;
            }
            bytes.add(result);
            clearBuffer();
        } else {
            buffer[bufferUsed] = b;
            bufferUsed++;
        }

    }

    @Override
    public void end() {
        if (bufferUsed > 0) {
            byte result = 0;
            for (int i = 0; i < bufferUsed; i++) {
                result = (byte) (result << 1);
                result += buffer[i];
            }
            result = (byte) (result << (8 - bufferUsed));
            bytes.add(result);
            clearBuffer();
        }
        end = true;
    }

    @Override
    public void run() {
        while (!end) Thread.onSpinWait();
    }
}
