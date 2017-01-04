import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Nika Doghonadze
 */
public class CBCPaddingOracle {
    private static String[] randomStrings = {
            "MDAwMDAwTm93IHRoYXQgdGhlIHBhcnR5IGlzIGp1bXBpbmc=",
            "MDAwMDAxV2l0aCB0aGUgYmFzcyBraWNrZWQgaW4gYW5kIHRoZSBWZWdhJ3MgYXJlIHB1bXBpbic=",
            "MDAwMDAyUXVpY2sgdG8gdGhlIHBvaW50LCB0byB0aGUgcG9pbnQsIG5vIGZha2luZw==",
            "MDAwMDAzQ29va2luZyBNQydzIGxpa2UgYSBwb3VuZCBvZiBiYWNvbg==",
            "MDAwMDA0QnVybmluZyAnZW0sIGlmIHlvdSBhaW4ndCBxdWljayBhbmQgbmltYmxl",
            "MDAwMDA1SSBnbyBjcmF6eSB3aGVuIEkgaGVhciBhIGN5bWJhbA==",
            "MDAwMDA2QW5kIGEgaGlnaCBoYXQgd2l0aCBhIHNvdXBlZCB1cCB0ZW1wbw==",
            "MDAwMDA3SSdtIG9uIGEgcm9sbCwgaXQncyB0aW1lIHRvIGdvIHNvbG8=",
            "MDAwMDA4b2xsaW4nIGluIG15IGZpdmUgcG9pbnQgb2g=",
            "MDAwMDA5aXRoIG15IHJhZy10b3AgZG93biBzbyBteSBoYWlyIGNhbiBibG93",
    };

    private static Random random = new Random();
    private static byte[] key = Utils.generateRandomBytes(random, 16);
    private static byte[] iv = Utils.generateRandomBytes(random, 16);;

    private static byte[] getRandomStringEncrypt() throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        String randomBase64String = randomStrings[random.nextInt(randomStrings.length)];
        byte[] decodedString = Base64.decodeBase64(randomBase64String);
        System.out.println(Arrays.toString(Utils.addPadding(decodedString, 16)));
        return Utils.CBCEncrypt(decodedString, key, iv);
    }

    private static boolean hasValidPadding(byte[] encrypted) {
        try {
            Utils.CBCDecrypt(encrypted, key, iv);
            return true;
        } catch (BadPaddingException ignored) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void main(String[] args) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] randomEncrypt = getRandomStringEncrypt();
        int blockLength = 16; //TODO

        byte[] guessed = new byte[randomEncrypt.length];
        int numBlocks = randomEncrypt.length / blockLength;

        for (int curBlockIndex = numBlocks - 1; curBlockIndex >= 0; curBlockIndex--) {
            byte[] curAndPreviousBlocks = getCurrentAndPreviousBlocks(randomEncrypt, curBlockIndex, blockLength);
            byte[] guessedBlock = guessCurBlock(curAndPreviousBlocks, blockLength);
            System.arraycopy(guessedBlock, 0, guessed, curBlockIndex * blockLength, blockLength);
        }

        System.out.println(Arrays.toString(guessed));
    }

    private static byte[] getCurrentAndPreviousBlocks(byte[] data, int curBlockIndex, int blockLength) {
        byte[] res = new byte[2 * blockLength];
        if (curBlockIndex > 0)
            System.arraycopy(data, curBlockIndex * blockLength - blockLength, res, 0, 2 * blockLength);
        else {
            System.arraycopy(iv, 0, res, 0, blockLength);
            System.arraycopy(data, 0, res, blockLength, blockLength);
        }

        return res;
    }

    private static byte[] guessCurBlock(byte[] curAndPreviousBlocks, int blockSize) {
        byte[] res = new byte[blockSize];
        for (int i = blockSize - 1; i >= 0; i--) {
            byte guessedByte = guessCurByte(i, curAndPreviousBlocks, blockSize, res);
            res[i] = guessedByte;
        }
        return res;
    }

    private static byte guessCurByte(int curByteIndex, byte[] curAndPreviousBlocks, int blockSize, byte[] guessedBytes) {
        int xorLen = blockSize - curByteIndex;
        for (int i = xorLen + 1; i < 256; i++) {
            byte[] data = curAndPreviousBlocks.clone();
            for (int j = curByteIndex + 1; j < blockSize; j++) {
                data[j] = (byte) (data[j] ^ xorLen ^ guessedBytes[j]);
            }
            data[curByteIndex] = (byte) (data[curByteIndex] ^ xorLen ^ i);
            if (hasValidPadding(data))
                return (byte) i;
        }
        return (byte) xorLen;
    }
}
