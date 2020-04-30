package Coding.Decoder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.LinkedList;

/**
 * Tag buffer.
 * Stores tag with BigInteger.
 */
public class TagGenerator {
    private BigInteger tag;
    private boolean endOfTag;
    private final LinkedList<Byte> tagBuffer;
    private boolean generated;
    private final int length;
    private int usedTagDigits;

    public TagGenerator(int len) {
        length = len;
        tag = BigInteger.ZERO;
        generated = false;
        tagBuffer = new LinkedList<>();
        usedTagDigits = 0;
    }


    public boolean canGenerate() {
        return !generated && (tagBuffer.size() >= length || endOfTag);
    }

    public synchronized void generateTag() {
        shiftTag(0);
        generated = true;
    }

    public boolean canShiftTag(int num) {
        return endOfTag || (tagBuffer.size() >= num);
    }

    /**
     * Shifts tag and fills with bits from buffer.
     */
    public synchronized void shiftTag(int num) {
        if (num > 0) {
            for (int j = num; j < usedTagDigits; j++) {
                if (tag.testBit(length - 1 - j)) tag = tag.setBit(length - 1 - j + num);
                else tag = tag.clearBit(length - 1 - j + num);
            }
            usedTagDigits -= num;
        }

        while (usedTagDigits < length && tagBuffer.size() > 0) {
            if (tagBuffer.pop() == 1) tag = tag.setBit(length - 1 - usedTagDigits);
            else tag = tag.clearBit(length - 1 - usedTagDigits);
            usedTagDigits++;
        }
    }

    public synchronized void setEndOfTag(boolean val) {
        endOfTag = val;
    }

    public BigDecimal getTag() {
        return new BigDecimal(tag).divide(Decoder.TWO.pow(length), MathContext.DECIMAL128);
    }


    /**
     * Add bit sequence, stored in Byte, to buffer.
     */
    public synchronized void addToBuffer(Byte b) {
        byte val = b;
        for (int i = 0; i < 8; i++) {
            byte temp = (byte) (val >> (7 - i));
            if (temp < 0) temp = (byte) (~temp + 1);
            temp = (byte) (temp % 2);
            tagBuffer.add(temp);
        }
    }
}
