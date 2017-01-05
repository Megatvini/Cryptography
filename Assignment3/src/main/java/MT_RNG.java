import java.util.Random;

/**
 * Created by Nika Doghonadze
 */
public class MT_RNG {
    private long index;
    private long[] mt;
    MT_RNG(long seed) {
        index = 624;
        mt = new long[624];
        mt[0] = seed;
        for (int i = 1; i < 624; i++) {
            mt[i] = int32(1812433253 * (mt[i - 1] ^ mt[i - 1] >> 30) + i);
        }
    }

    long extractNumber() {
        if (index >= 624) {
            twist();
        }

        long x = mt[(int) index];

        x = x ^ x >> 11;
        x = x ^ x << 7 & 2636928640L;
        x = x ^ x << 15 & 4022730752L;
        x = x ^ x >> 18;

        index += 1;

        return int32(x);
    }

    private void twist() {
        for (int i = 0; i < 624; i++) {
            long y = int32((mt[i] & 0x80000000L) + (mt[(i + 1) % 624] & 0x7fffffffL));
            mt[i] = mt[(i + 397) % 624] ^ y >> 1;

            if ((y % 2) != 0)
                mt[i] = mt[i] ^ 0x9908b0dfL;
        }
        index = 0;
    }

    private long int32(long x) {
        return x % 4294967296L;
    }


    public static void main(String[] args) throws Exception {
        MT_RNG mt_rng = new MT_RNG(1);
        for (int i = 0; i < 200; i++) {
            System.out.println(mt_rng.extractNumber());
        }
    }
}
