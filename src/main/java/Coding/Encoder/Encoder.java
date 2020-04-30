package Coding.Encoder;

import Coding.AbstractArithmeticCoder;
import Coding.CodingListener;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import static java.math.BigDecimal.*;

public class Encoder extends AbstractArithmeticCoder {
    private final CodingListener listener;


    public Encoder(CodingListener listener) {
        this.listener = listener;
    }

    private void send(byte b) {
        listener.next(b);
    }

    private void start(long lengthInBytes) {
        listener.start(lengthInBytes);
    }

    private void end() {
        listener.end();
    }

    private void scale() {
        boolean repeat = true;
        while (repeat) {
            repeat = false;
            while (L.compareTo(ZERO) >= 0 && R.compareTo(HALF) < 0) {
                L = L.multiply(TWO, MathContext.UNLIMITED);
                R = R.multiply(TWO, MathContext.UNLIMITED);
                send((byte) 0);
                repeat = true;
            }
            while (L.compareTo(HALF) >= 0 && R.compareTo(ONE) < 0) {
                L = L.multiply(TWO, MathContext.UNLIMITED).subtract(ONE, MathContext.UNLIMITED);
                R = R.multiply(TWO, MathContext.UNLIMITED).subtract(ONE, MathContext.UNLIMITED);
                send((byte) 1);
                repeat = true;
            }
        }
    }

    private void sendTag(BigDecimal L, BigDecimal R) {
        BigDecimal tag = L.add(R).divide(TWO, MathContext.DECIMAL128);
        tag = tag.movePointRight(15);
        BigInteger i = tag.toBigInteger();
        String s = i.toString(2);
        for (int j = 0; j < s.length(); j++) {
            if (s.charAt(j) == '0') send((byte) 0);
            else send((byte) 1);
        }
    }

    public synchronized void encode(byte[] file) {
        start(file.length);
        counter = 256;
        L = ZERO;
        R = ONE;
        BigDecimal range;
        prepareFreq();
        for (byte b : file) {
            int unsigned = Byte.toUnsignedInt(b);
            long tempSum = 0;
            for (int i = 0; i <= unsigned; i++) tempSum += freq[i];

            range = R.subtract(L, MathContext.UNLIMITED);
            R = L.add(range.multiply(valueOf(tempSum), MathContext.UNLIMITED).divide(valueOf(counter), MathContext.DECIMAL128), MathContext.UNLIMITED);
            L = L.add(range.multiply(valueOf(tempSum - freq[unsigned]), MathContext.UNLIMITED).divide(valueOf(counter), MathContext.DECIMAL128), MathContext.UNLIMITED);

            scale();
            updateModel(unsigned);
        }
        sendTag(L,R);
        end();
    }

}
