import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by Nika Doghonadze
 */
public class CTR {
    static byte[] encrypt(byte[] data, byte[] key, int nonce) throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        int blockSize = key.length;
        int numBlocks = getNumBlocks(data.length, blockSize);
        byte[] nonceBytes = Utils.intToBytesLittleEndian(nonce, 8);
        byte[] res = new byte[data.length];
        for (int curBlockIndex = 0; curBlockIndex < numBlocks; curBlockIndex++) {
            int from = curBlockIndex * blockSize;
            int to = Math.min(from + blockSize, data.length);
            byte[] curDataBlock = Arrays.copyOfRange(data, from, to);
            byte[] encryptedBlock = encryptSingleBlock(curDataBlock, key, nonceBytes, curBlockIndex);
            System.arraycopy(encryptedBlock, 0, res, curBlockIndex * blockSize, encryptedBlock.length);
        }
        return res;
    }

    static byte[] decrypt(byte[] data, byte[] key, int nonce) throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        return encrypt(data, key, nonce);
    }

    private static byte[] encryptSingleBlock(byte[] curDataBlock, byte[] key, byte[] nonceBytes, int blockIndex) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        byte[] ctrKey = generateCtrKey(key, nonceBytes, blockIndex);
        if (ctrKey.length > curDataBlock.length) {
            byte[] shorterKey = Arrays.copyOfRange(ctrKey, 0, curDataBlock.length);
            return Utils.xorBytes(curDataBlock, shorterKey);
        }
        return Utils.xorBytes(curDataBlock, ctrKey);
    }

    private static byte[] generateCtrKey(byte[] key, byte[] nonceBytes, int blockIndex) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        byte[] data = new byte[16];
        byte[] blockIndexBytes = Utils.intToBytesLittleEndian(blockIndex, 8);
        System.arraycopy(nonceBytes, 0, data, 0, nonceBytes.length);
        System.arraycopy(blockIndexBytes, 0, data, nonceBytes.length, blockIndexBytes.length);
        return Utils.ECBEncrypt(data, key);
    }

    private static int getNumBlocks(int length, int blockSize) {
        return (length / blockSize) + (length % blockSize > 0 ? 1 : 0);
    }

    public static void main(String[] args) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] key = "YELLOW SUBMARINE".getBytes();
        byte[] data = Base64.decodeBase64("L77na/nrFsKvynd6HzOoG7GHTLXsTVu9qvY/2syLXzhPweyyMTJULu/6/kXX0KSvoOLSFQ==");
        int nonce = 0;
        System.out.println(new String(decrypt(data, key, nonce)));
    }

}
