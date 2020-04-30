package Coding;

import java.math.BigDecimal;
import java.util.Arrays;

import static java.math.BigDecimal.valueOf;

/**
 * Nie jest to może najpiękniejsza klasa abstrakcyjna, ale przynajmniej nie powtarzam kodu
 */
public abstract class AbstractArithmeticCoder {
    protected int[] freq;
    protected int counter;
    protected BigDecimal R, L;

    // BigDecimal ma tylko Zero, One, Ten
    public static BigDecimal TWO = valueOf(2);
    public static BigDecimal HALF = valueOf(0.5);

    /**
     * Przygotowuje startowe częstotliwości
     */
    protected final void prepareFreq() {
        counter = 256;
        freq = new int[256];
        Arrays.fill(freq, 1);
    }

    /**
     * Aktualizuje model adaptacyjny.
     * Model jest niezależny od samego kodowania i dekodowania.
     */
    protected final void updateModel(int b) {
        freq[b] += 1;
        counter++;
    }

}
