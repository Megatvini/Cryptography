import org.apache.commons.codec.binary.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;


/**
 * Created by Nika Doghonadze
 */
public class Utils {
    static byte[] addPadding(byte[] message, int blockSize) {
        int resLen;
        if (message.length % blockSize != 0) {
            resLen = message.length + blockSize - message.length % blockSize;
        } else {
            resLen = message.length + blockSize;
        }
        byte[] res = new byte[resLen];
        for (int i = 0; i < res.length; i++) {
            if (i < message.length) {
                res[i] = message[i];
            } else {
                res[i] = (byte) (res.length - message.length);
            }
        }
        return res;
    }

    private static byte[] CBCEncryptBlock(byte[] block, byte[] lastEncryption, byte[] key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        byte[] res = xorBytes(block, lastEncryption);
        return ECBEncrypt(res, key);
    }

    private static byte[] CBCEncrypt(byte[] data, byte[] key, byte[] IV)
            throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchPaddingException {

        int blockSize = key.length;
        data = addPadding(data, blockSize);

        if (data.length % blockSize != 0)
            throw new RuntimeException("Wrong data or block size");

        byte[] lastEncryption = IV;
        byte[] res = new byte[data.length];
        int curIndex = 0;
        while (curIndex < data.length) {
            byte[] curBlock = Arrays.copyOfRange(data, curIndex, curIndex + blockSize);
            byte[] encryptedBlock = CBCEncryptBlock(curBlock, lastEncryption, key);
            System.arraycopy(encryptedBlock, 0, res, curIndex, blockSize);
            lastEncryption = encryptedBlock;
            curIndex += blockSize;
        }
        return res;
    }

    private static byte[] xorBytes(byte[] one, byte[] two) {
        if (one.length != two.length)
            throw new RuntimeException("Must be same size "  + one.length + " " + two.length);

        byte[] res = new byte[one.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = (byte) (one[i] ^ two[i]);
        }
        return res;
    }

    static byte[] CBCDecrypt(byte[] data, byte[] key, byte[] IV)
            throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchPaddingException {

        int blockSize = key.length;

        if (data.length % blockSize != 0)
            throw new RuntimeException("Wrong data or block size");

        byte[] res = new byte[data.length];

        int curIndex = 0;
        byte[] lastEncrypted = IV;
        while (curIndex < data.length) {
            byte[] curBlock = Arrays.copyOfRange(data, curIndex, curIndex + blockSize);
            byte[] decryptedBlock = CBCDecryptBlock(curBlock, lastEncrypted, key);
            System.arraycopy(decryptedBlock, 0, res, curIndex, blockSize);
            lastEncrypted = curBlock;
            curIndex += blockSize;
        }

        return removePadding(res);
    }

    private static byte[] CBCDecryptBlock(byte[] block, byte[] lastEncryption, byte[] key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        byte[] res = ECBDecrypt(block, key);
        res = xorBytes(res, lastEncryption);
        return res;
    }

    private static byte[] ECBDecrypt(byte[] data, byte[] key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(data);
    }

    private static byte[] ECBEncrypt(byte[] data, byte[] key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(data);
    }


    private static byte[] removePadding(byte[] data) {
        byte[] res =  new byte[data.length - data[data.length - 1]];
        System.arraycopy(data, 0, res, 0, res.length);
        return res;
    }

    private static File getFileFromResources(String fileName) throws FileNotFoundException {
        ClassLoader classLoader = Utils.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null)
            throw new FileNotFoundException("FILE NOT FOUND " + fileName);
        return new File(resource.getFile());
    }

    static String readWholeFile(String fileName) throws FileNotFoundException {
        File fileFromResources = getFileFromResources(fileName);
        String str = "";
        Scanner sc = new Scanner(fileFromResources);
        while (sc.hasNext())
            str += sc.nextLine();
        sc.close();
        return str;
    }

    static byte[] encryptionOracle(byte[] data)
            throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchPaddingException {
        Random random = new Random();
        data = addRandomPadding(random, data);
        byte[] key = generateRandomBytes(random, 16);
        data = addPadding(data, key.length);
        if (random.nextBoolean()) {
            System.out.println("ECB");
            return ECBEncrypt(data, key);
        } else {
            byte[] IV = generateRandomBytes(random, key.length);
            System.out.println("CBC");
            return CBCEncrypt(data, key, IV);
        }
    }

    private static byte[] generateRandomBytes(Random random, int size) {
        byte[] res = new byte[size];
        random.nextBytes(res);
        return res;
    }

    private static byte[] addRandomPadding(Random random, byte[] data) {
        byte[] start = generateRandomBytes(random, 5 + random.nextInt(6));
        byte[] end = generateRandomBytes(random, 5 + random.nextInt(6));
        byte[] res = new byte[start.length + data.length + end.length];
        System.arraycopy(start, 0, res, 0, start.length);
        System.arraycopy(data, 0, res, start.length, data.length);
        System.arraycopy(end, 0, res, start.length + data.length, end.length);
        return res;
    }

    public static void main(String[] args) throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, FileNotFoundException {

    }

    static boolean detectOracleModeIsECB(byte[] encryption) {
        byte[][] slices = sliceBytes(encryption, 16);
        Set<String> set = new HashSet<>();
        for (byte[] slice : slices) {
            set.add(Hex.encodeHexString(slice));
        }
        return set.size() < slices.length / 2;
    }

    private static byte[][] sliceBytes(byte[] data, int sliceSize) {
        if (data.length % sliceSize != 0)
            throw new RuntimeException("wrong slice or data size");

        byte[][] res = new byte[data.length/sliceSize][sliceSize];
        for (int i = 0; i < res.length; i++) {
            System.arraycopy(data, i * sliceSize, res[i], 0, res[i].length);
        }
        return res;
    }
}
