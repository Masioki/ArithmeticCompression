package Coding;

import java.math.BigDecimal;
import java.util.Arrays;

import static java.math.BigDecimal.valueOf;

public abstract class AbstractArithmeticCoder {
    protected int[] freq;
    protected int counter;
    protected BigDecimal R, L;

    public static BigDecimal TWO = valueOf(2);
    public static BigDecimal HALF = valueOf(0.5);

    /**
     * Prepares staring frequency table filled with 1.
     */
    protected final void prepareFreq() {
        counter = 256;
        freq = new int[256];
        Arrays.fill(freq, 1);
    }

    /**
     * Updates frequency table.
     */
    protected final void updateModel(int b) {
        freq[b] += 1;
        counter++;
    }

}
