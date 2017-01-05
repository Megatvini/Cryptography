/**
 * Created by Nika Doghonadze
 */
public class CloneMT_RNG {
    private static final int INT_SIZE = 32;

    public static void main(String[] args) {
        crackPRG();
    }

    private static void crackPRG() {
        MT_RNG mt_rng = new MT_RNG(System.currentTimeMillis());
        MT_RNG clone = new MT_RNG(1);

        for (int i = 0; i < 624; i++) {
            long number = mt_rng.extractNumber();
            untemper(i, clone, number);
        }

        for (int i = 0; i < 1000; i++) {
            long one = mt_rng.extractNumber();
            long two = clone.extractNumber();
            long diff = one - two;
            if (diff != 0) {
                System.out.println("ERROR guessed: " + one + " correct: " + two);
                return;
            }
        }
        System.out.println("PASS");
    }

    private static void untemper(int index, MT_RNG clone, long number) {
        long x = number;

        x = rightShiftReverse(x, 18);
        x = leftShiftReverse(x, 15, 4022730752L);
        x = leftShiftReverse(x, 7, 2636928640L);
        x = rightShiftReverse(x, 11);

        clone.mt[index] = x;
    }

    private static long rightShiftReverse(long num, int shiftBy) {
        String binary = Long.toBinaryString(num);

        StringBuilder res = new StringBuilder();
        String lastChunk = zeroes(shiftBy);
        int curIndex = 0;
        while (!lastChunk.equals("") && lastChunk.length() == shiftBy) {
            int to = Math.min(binary.length(), curIndex + shiftBy);
            String curChunk = binary.substring(curIndex, to);
            String xorEd = xorBinStrings(lastChunk, curChunk);
            res.append(xorEd);
            lastChunk = xorEd;
            curIndex += shiftBy;
        }

        return Long.parseLong(res.toString(), 2);
    }

    private static long leftShiftReverse(long num, int shiftBy, long and) {
        long result = 0;
        for (int curChunk = 0; curChunk <= INT_SIZE / shiftBy; curChunk++) {
            long c = (4294967295L >> (INT_SIZE - shiftBy)) << (shiftBy * curChunk);
            String binC = Long.toBinaryString(c);
            int from = Math.max(binC.length() - INT_SIZE, 0);
            int to = binC.length();
            binC = binC.substring(from, to);
            long t = Long.parseLong(binC, 2);
            long part = num & t;
            num ^= part << shiftBy & and;
            result |= part;
        }
        return result;
    }

    private static String xorBinStrings(String lastChunk, String curChunk) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < curChunk.length(); i++) {
            int c1 = curChunk.charAt(i) - '0';
            int c2 = lastChunk.charAt(i) - '0';
            String bin = Integer.toBinaryString(c1 ^ c2);
            res.append(bin);
        }

        return res.toString();
    }

    private static String zeroes(int numZeros) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < numZeros; i++) {
            res.append("0");
        }
        return res.toString();
    }
}
