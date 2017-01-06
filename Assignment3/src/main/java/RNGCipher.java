import java.util.Arrays;
import java.util.Random;

/**
 * Created by Nika Doghonadze
 */
public class RNGCipher {
    static byte[] encrypt(byte[] data, short seedKey) {
        MT_RNG random = new MT_RNG(seedKey);
        byte[] key = getKey(random, data.length);
        return Utils.xorBytes(data, key);
    }

    static byte[] decrypt(byte[] data, short seedKey) {
        return encrypt(data, seedKey);
    }

    private static byte[] getKey(MT_RNG random, int length) {
        byte[] res = new byte[length];
        int curIndex = 0;
        while (curIndex < length) {
            int needBytes = Math.min(4, length - curIndex);
            byte[] randomBytes = getRandomBytes(random, needBytes);
            System.arraycopy(randomBytes, 0, res, curIndex, randomBytes.length);
            curIndex += needBytes;
        }
        return res;
    }

    private static byte[] getRandomBytes(MT_RNG random, int numBytes) {
        long randNum = random.extractNumber();
        byte[] randBytes = Utils.numToBytesLittleEndian(randNum, 4);
        return Arrays.copyOfRange(randBytes, 0, numBytes);
    }

    static byte[] encryptWithRandomPrefix(byte[] data, short seed) {
        Random random = new Random();
        byte[] randPrefix = Utils.generateRandomBytes(random, random.nextInt(50));
        byte[] fullData = Utils.concat(randPrefix, data);
        return encrypt(fullData, seed);
    }


    private static short guessSeed(byte[] plaintext, byte[] encrypt) {
        for (int curSeed = 0; curSeed < Short.MAX_VALUE; curSeed++) {
            byte[] data = new byte[encrypt.length];
            byte[] curEncrypt = encrypt(data, (short) curSeed);
            if (hasSameEnd(curEncrypt, encrypt, plaintext.length))
                return (short) curSeed;
        }
        return -1;
    }

    private static boolean hasSameEnd(byte[] one, byte[] two, int lenDiff) {
        int curIndex = one.length - 1;
        for (int i = 0; i < lenDiff; i++) {
            if (one[curIndex - i] != two[curIndex - i])
                return false;
        }
        return true;
    }

    static byte[] encryptCurTimeSeed(byte[] data) {
        short seed = getCurTime();
        return encrypt(data, seed);
    }

    private static short getCurTime() {
        return (short) (System.currentTimeMillis() / (1000 * 60));
    }

    static boolean checkHasCurTimeSeed(byte[] encrypt) {
        byte[] data = new byte[encrypt.length];
        short seed = getCurTime();
        byte[] decrypted = decrypt(encrypt, seed);
        return Arrays.equals(decrypted, data);
    }

    public static void main(String[] args) {
        byte[] data = new byte[14];
        short randomSeed = (short) new Random().nextInt(Short.MAX_VALUE);
        byte[] encrypt = encryptWithRandomPrefix(data, randomSeed);
        short guessedSeed = guessSeed(data, encrypt);
        System.out.println("guessed " + guessedSeed + " actual " + randomSeed);

        Random random = new Random();
        byte[] plaintTextBytes = new byte[random.nextInt(50)];
        byte[] encryptCurTimeSeed = encryptCurTimeSeed(plaintTextBytes);
        System.out.println(checkHasCurTimeSeed(encryptCurTimeSeed));
    }

}
