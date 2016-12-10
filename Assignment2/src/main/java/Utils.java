import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.*;

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


    static byte[] removePadding(byte[] data) {
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

    private static final byte[] oracleKey = generateRandomBytes(new Random(), 16);
    private static byte[] oraclePadding = org.apache.commons.codec.binary.Base64.decodeBase64(
            "Um9sbGluJyBpbiBteSA1LjAKV2l0aCBteSByYWctdG9wIGRvd24gc28gbXkg\n" +
                    "aGFpciBjYW4gYmxvdwpUaGUgZ2lybGllcyBvbiBzdGFuZGJ5IHdhdmluZyBq\n" +
                    "dXN0IHRvIHNheSBoaQpEaWQgeW91IHN0b3A/IE5vLCBJIGp1c3QgZHJvdmUg\n" +
                    "YnkK");

    private static byte[] encryptionOracleWithPadding(byte[] data)
            throws DecoderException, InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        byte[] newData = new byte[data.length + oraclePadding.length];
        System.arraycopy(data, 0, newData, 0, data.length);
        System.arraycopy(oraclePadding, 0, newData, data.length, oraclePadding.length);
        newData = addPadding(newData, oracleKey.length);
        return ECBEncrypt(newData, oracleKey);
    }

//    private static byte[] randomPrefix = generateRandomBytes(new Random(), new Random().nextInt(100));
    private static byte[] randomPrefix = generateRandomBytes(new Random(), 15);
    static byte[] encryptionOracleWithPrefixAndPadding(byte[] data)
            throws BadPaddingException, DecoderException, IllegalBlockSizeException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        byte[] newData = new byte[randomPrefix.length + data.length];
        System.arraycopy(randomPrefix, 0, newData, 0, randomPrefix.length);
        System.arraycopy(data, 0, newData, randomPrefix.length, data.length);
        return encryptionOracleWithPadding(newData);
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

    static String breakEncryptionOracle()
            throws BadPaddingException, DecoderException, IllegalBlockSizeException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        int blockSize = findBlockSize();
        if (!isEcb(blockSize))
            return "";

        byte[] data = new byte[blockSize];
        int numDecoded = 0;
        Map<String, Byte> map = new HashMap<>();
        while (true) {
            for (int i = 0; i < 256; i++) {
                byte b = (byte) i;
                data[data.length - 1] = b;
                byte[] encryption = encryptionOracleWithPadding(data);
                byte[] firstBlocks = Arrays.copyOfRange(encryption, 0, blockSize * data.length/blockSize);
                String hex = Hex.encodeHexString(firstBlocks);
                map.put(hex, b);
            }

            byte[] dataWithoutLastByte = Arrays.copyOfRange(data, 0, data.length - numDecoded - 1);
            byte[] bytes = encryptionOracleWithPadding(dataWithoutLastByte);
            byte[] firstBlock = Arrays.copyOfRange(bytes, 0, blockSize * data.length/blockSize);
            String hex = Hex.encodeHexString(firstBlock);
            Byte lastByte = map.get(hex);
            if (lastByte == null)
                break;

            data[data.length - 1] = lastByte;
            numDecoded++;

            if (numDecoded % blockSize == 0) {
                byte[] newData = new byte[data.length + blockSize];
                System.arraycopy(data, 0, newData, blockSize - 1, data.length);
                data = newData;
            } else {
                System.arraycopy(data, 1, data, 0, data.length - 1);
            }
        }
        byte[] res = Arrays.copyOfRange(data, data.length - numDecoded - 1, data.length - 1);
        return new String(removePadding(res));
    }

    private static boolean isEcb(int blockSize)
            throws BadPaddingException, DecoderException, IllegalBlockSizeException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        byte[] data = new byte[2*blockSize];
        byte[] encrypted = encryptionOracleWithPadding(data);
        for (int i = 0; i < blockSize; i++) {
            if (encrypted[i] != encrypted[i+blockSize])
                return false;
        }
        return true;
    }

    private static int findBlockSize()
            throws BadPaddingException, DecoderException, IllegalBlockSizeException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        int size = encryptionOracleWithPadding(new byte[0]).length;
        int i = 1;
        while (true){
            int curSize = encryptionOracleWithPadding(new byte[i]).length;
            if (curSize != size)
                return curSize - size;
            i++;
        }
    }

    private static byte[] ecbProfileRandomKey = generateRandomBytes(new Random(), 16);
    static byte[] encryptECBRandomKey(Profile profile)
            throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchPaddingException {
        String message = profile.toString();
        byte[] messageBytes = addPadding(message.getBytes(), ecbProfileRandomKey.length);
        return ECBEncrypt(messageBytes, ecbProfileRandomKey);
    }

    static Profile decryptECBProfileRandomKey(byte[] encryption)
            throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchPaddingException {
        byte[] decrypt = ECBDecrypt(encryption, ecbProfileRandomKey);
        decrypt = removePadding(decrypt);
        return Profile.fromString(new String (decrypt));
    }

    static Profile profileFor(String email) {
        return new Profile(email, "10", "user");
    }

    public static class Profile {
        String email;
        String id;
        String role;

        private Profile(String email, String id, String role) {
            this.email = email;
            this.id = id;
            this.role = role;
        }

        @Override
        public String toString() {
            return "email="+ email + "&uid=" + id + "&role=" + role;
        }

        static Profile fromString(String str) {
            Map<String, String> map = new HashMap<>();
            String[] params = str.split("&");
            for (String param : params) {
                String[] pair = param.split("=");
                String key = pair[0];
                String val = pair[1];
                map.put(key, val);
            }
            return new Profile(map.get("email"), map.get("uid"), map.get("role"));
        }
    }

    public static void main(String[] args) throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        System.out.println(randomPrefix.length);
    }
}
