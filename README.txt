Adaptive arithmetic coding with scaling based on Integer arithmetic.

Simple arithmetic coding for Data Coding and Compression class.
Allows dynamic encoding and decoding, which means, in case of internet connection, bits of
code might be send and decoding can start even before encoding is finished.
Compressed file contains 4 bytes of file length and tag.

Arithmetic is based on BigInteger and BigDecimal, which gives great precision and easy management.
Using types like Double or Float is not recommended, because they are implemented as IEE754(64 and 32 bit)
so they range is big, but precision might be insufficient.

This coding uses simple model for adaptive compression.
At the beginning frequency table is filled with alphabet and frequency set to 1.
After encoding/decoding character frequency is incremented.

Scaling method:
    while Possible
        while 0 <= L and R < 0.5
            L = L * 2
            R = R * 2
            SEND BIT '0'
        while 0.5 <= L and R < 1###  Adaptive arithmetic coding with scaling based on Integer arithmetic.

Simple arithmetic coding for Data Coding and Compression class.
Allows dynamic encoding and decoding, which means, in case of internet connection, bits of
code might be send and decoding can start even before encoding is finished.
Compressed file contains 4 bytes of file length and tag.

Arithmetic is based on **BigInteger** and **BigDecimal**, which gives great precision and easy management.
Using types like Double or Float is not recommended, because they are implemented as IEE754(64 and 32 bit) so they range is big, but precision might be insufficient.

This coding uses simple model for adaptive compression.
At the beginning frequency table is filled with alphabet and frequency set to 1.
After encoding/decoding character frequency is incremented.

Scaling method:
   

     while Possible
            while 0 <= L and R < 0.5
                L = L * 2
                R = R * 2
                SEND BIT '0'
            while 0.5 <= L and R < 1
                L = L * 2 - 1
                R = R * 2 - 1
                SEND BIT '1'

Explanation:
  

      IF L-R is in range (0, 0.5)
            L = 0,0... (binary)
   Tag has to be in the same range, so TAG = 0,0...(bin)
   Multiplying by 2 in binary system means shifting left.
   Instead of calculating very small ranges first bits might be send earlier.
            L = L * 2 - 1
            R = R * 2 - 1
            SEND BIT '1'

Explanation:
    IF L-R is in range (0,0.5)
        L = 0,0... (binary)
    Tag has to be in the same range, so TAG = 0,0...(bin)
    Multiplying by 2 in binary system means shifting left.
    Instead of calculating very small ranges first bits might be send earlier.
