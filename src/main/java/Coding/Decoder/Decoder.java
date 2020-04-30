package Coding.Decoder;

import Coding.AbstractArithmeticCoder;
import Coding.CodingListener;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.*;

public class Decoder extends AbstractArithmeticCoder implements CodingListener {
    private List<Byte> decoded;
    private long decodedLength;
    private boolean inProgress;
    private TagGenerator generator;
    private final int precision;


    public Decoder(int precision) {
        this.precision = precision;
        decoded = new ArrayList<>();
        inProgress = false;
        generator = new TagGenerator(1);
    }


    private void scale() {
        generator.shiftTag(0);
        int shift = 0;
        boolean repeat = true;
        while (repeat) {
            repeat = false;
            while (L.compareTo(ZERO) >= 0 && R.compareTo(HALF) < 0) {
                L = L.multiply(TWO, MathContext.UNLIMITED);
                R = R.multiply(TWO, MathContext.UNLIMITED);
                repeat = true;
                shift++;
            }
            while (L.compareTo(HALF) >= 0 && R.compareTo(ONE) < 0) {
                L = L.multiply(TWO, MathContext.UNLIMITED).subtract(ONE);
                R = R.multiply(TWO, MathContext.UNLIMITED).subtract(ONE);
                repeat = true;
                shift++;
            }
        }
        generator.shiftTag(shift);
    }

    private void decodeNext() {
        long sum = 0;
        int result = 0;
        BigDecimal range = R.subtract(L, MathContext.UNLIMITED);
        BigDecimal tag = generator.getTag();
        BigDecimal temp = tag.subtract(L).multiply(valueOf(counter)).divide(range, MathContext.DECIMAL128);

        for (int i = 0; i < freq.length; i++) {
            sum += freq[i];
            if (temp.compareTo(valueOf(sum - freq[i])) >= 0 && temp.compareTo(valueOf(sum)) < 0) {
                R = L.add(range.multiply(valueOf(sum)).divide(valueOf(counter), MathContext.DECIMAL128));
                L = L.add(range.multiply(valueOf(sum - freq[i])).divide(valueOf(counter), MathContext.DECIMAL128));
                result = i;
                break;
            }
        }
        scale();
        decoded.add((byte) result);
        updateModel(result);
    }

    @Override
    public synchronized void start(long lengthInBytes) {
        generator = new TagGenerator(precision);
        decoded = new ArrayList<>((int) lengthInBytes);
        decodedLength = lengthInBytes;
        L = ZERO;
        R = ONE;
        prepareFreq();
    }

    @Override
    public synchronized void next(byte b) {
        generator.addToBuffer(b);
        if (inProgress && generator.canShiftTag(10)) {
            decodeNext();
        }
        if (!inProgress && generator.canGenerate()) {
            generator.generateTag();
            decodeNext();
            inProgress = true;
        }

    }

    @Override
    public synchronized void end() {
        generator.setEndOfTag(true);
        if (!inProgress) {
            generator.generateTag();
            decodeNext();
            inProgress = true;
        }
        while (counter - 256 < decodedLength) decodeNext();
    }


    public boolean isDecodingFinished() {
        return decoded.size() == decodedLength;
    }

    public List<Byte> getDecoded() {
        return decoded;
    }
}
