package Coding;

public interface CodingListener {
    void start(long lengthInBytes);

    void next(byte b);

    void end();
}
