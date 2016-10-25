import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
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
import java.util.stream.Collectors;

class Utils {
    private static final int MAX_KEY_SIZE = 40;
    private static final int NUM_KEYS_TO_CHECK = 30;

    static String hexToBase64(String hexString) throws DecoderException {
        byte[] bytes = Hex.decodeHex(hexString.toCharArray());
        return Base64.encodeBase64String(bytes);
    }

    static String xorHexStrings(String one, String two) throws DecoderException {
        if (one.length() != two.length()) {
            throw new DecoderException("Strings must be equal length");
        }

        byte[] bytes1 = Hex.decodeHex(one.toCharArray());
        byte[] bytes2 = Hex.decodeHex(two.toCharArray());

        byte[] xorRes = new byte[bytes1.length];
        for (int i=0; i<xorRes.length; i++) {
            xorRes[i] = (byte) (bytes1[i] ^ bytes2[i]);
        }

        char[] chars = Hex.encodeHex(xorRes);
        return new String(chars);
    }

    static String decodeSingleByteXor(String hexString) throws DecoderException {
        byte[] bytes = Hex.decodeHex(hexString.toCharArray());
        return decodeSingleByteXor(bytes);
    }

    private static String decodeSingleByteXor(byte[] bytes) {
        List<String> candidates = generateCandidates(bytes);
        return getBestEnglishText(candidates);
    }

    static String getBestEnglishText(List<String> candidates) {
        List<String> copy = new ArrayList<>(candidates);
        Collections.sort(copy, (o1, o2) -> scoreEnglishText(o1).compareTo(scoreEnglishText(o2)));
        Collections.reverse(copy);
        return copy.get(0);
    }

    static File getFileFromResources(String fileName) throws FileNotFoundException {
        ClassLoader classLoader = Utils.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null)
            throw new FileNotFoundException("FILE NOT FOUND " + fileName);
        return new File(resource.getFile());
    }


    static String repeatingXorHex(String text, String key) {
        return Hex.encodeHexString(repeatingXorString(text, key).getBytes());
    }

    private static String repeatingXorString(String text, String key) {
        byte[] textBytes = text.getBytes();
        byte[] keyBytes = key.getBytes();
        for (int i=0; i<textBytes.length; i++) {
            textBytes[i] = (byte) (textBytes[i] ^ keyBytes[i%keyBytes.length]);
        }
        return new String(textBytes);
    }

    static int hammingDistance(String one, String two) {
        if (one.length() != two.length())
            throw new RuntimeException("Strings have unequal length");
        return hammingDistance(one.getBytes(), two.getBytes());
    }

    private static int hammingDistance(byte[] one, byte[] two) {
        if (one.length != two.length)
            throw new RuntimeException("Strings have unequal length");

        int res = 0;
        for (int i=0; i<one.length; i++) {
            res += hammingDistance(one[i], two[i]);
        }
        return res;
    }

    private static int hammingDistance(byte one, byte two) {
        return Integer.bitCount(one ^ two);
    }

    private static Integer scoreEnglishText(String text) {
        int resScore = 0;
        for (int i=0; i<text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLetter(c) || Character.isSpaceChar(c))
                resScore++;
        }
        return resScore;
    }

    private static List<String> generateCandidates(byte[] bytes) {
        List<String> res = new ArrayList<>();
        for (byte b = 0; b < Byte.MAX_VALUE; b++) {
            byte[] copy = xorByteArray(bytes, b);
            res.add(new String(copy));
        }
        return res;
    }

    private static byte[] xorByteArray(byte[] bytes, byte byteToXor) {
        byte[] res = new byte[bytes.length];
        for (int i=0; i<res.length; i++) {
            res[i] = (byte) (bytes[i] ^ byteToXor);
        }
        return res;
    }


    static String decryptRepeatingKeyXor(String base64Cipher) {
        byte[] encryptedBytes = Base64.decodeBase64(base64Cipher);
        List<Integer> keySizes = getKeySizes(encryptedBytes, NUM_KEYS_TO_CHECK);
        return decryptRepeatingKeyXor(base64Cipher, keySizes);
    }

    private static String decryptRepeatingKeyXor(String base64Cipher, List<Integer> keySizes) {
        List<String> candidates = keySizes.stream().map(keySize -> decryptRepeatingKeyXor(base64Cipher, keySize)).collect(Collectors.toList());
        Collections.sort(candidates, (o1, o2) -> scoreEnglishText(o1).compareTo(scoreEnglishText(o2)));
        Collections.reverse(candidates);
        return candidates.get(0);
    }

    private static String decryptRepeatingKeyXor(String base64Cypher, Integer keySize) {
        byte[] cipher = Base64.decodeBase64(base64Cypher);
        return decryptRepeatingKeyXor(cipher, keySize);
    }

    private static String decryptRepeatingKeyXor(byte[] cipher, int keySize) {
        String key = findRepeatingKeyXor(cipher, keySize);
        String cipherString = new String(cipher);
        return repeatingXorString(cipherString, key);
    }

    private static String findRepeatingKeyXor(byte[] cipher, int keySize) {
        byte[][] blocks = breakToBlocks(cipher, keySize);
        byte[][] transposedBlocks = transposeBlocks(blocks);
        StringBuilder res = new StringBuilder();
        for (byte[] block: transposedBlocks) {
            byte singleByteXorKey = findSingleByteXorKey(block);
            res.append((char) singleByteXorKey);
        }
//        System.out.println("KEY FOR KEYSIZE: " + keySize + " is " + res.toString());
        return res.toString();
    }

    private static byte findSingleByteXorKey(byte[] block) {
        List<String> candidates = generateCandidates(block);
        String bestEnglishText = getBestEnglishText(candidates);
        return (byte) candidates.indexOf(bestEnglishText);
    }

    private static byte[][] transposeBlocks(byte[][] blocks) {
        byte[][] res = new byte[blocks[0].length][blocks.length];
        for (int i=0; i<res.length; i++) {
            for (int j=0; j<res[i].length; j++) {
                res[i][j] = blocks[j][i];
            }
        }
        return res;
    }

    private static byte[][] breakToBlocks(byte[] cipher, int blockSize) {
        int numBlocks = cipher.length/blockSize + ((cipher.length % blockSize) > 0 ? 1 : 0);
        byte[][] res = new byte[numBlocks][];
        for (int i=0; i<numBlocks; i++) {
            res[i] = Arrays.copyOfRange(cipher, i*blockSize, (i+1) * blockSize);
        }
        return res;
    }

    private static List<Integer> getKeySizes(byte[] encryptedBytes, int numKeys) {
        List<Integer> res = new ArrayList<>();

        for (int keySize = 1; keySize < MAX_KEY_SIZE; keySize++) {
            res.add(keySize);
        }
        Collections.sort(res, (one, two) -> {
            Double oneDistance = distanceForKey(encryptedBytes, one);
            Double twoDistance = distanceForKey(encryptedBytes, two);
            return oneDistance.compareTo(twoDistance);
        });
        return res.subList(0, numKeys);
    }

    private static Double distanceForKey(byte[] encryptedBytes, Integer keySize) {
        if (encryptedBytes.length < 2*keySize)
            throw new RuntimeException("TOO SMALL encryptedBytes");

        byte[] firstBlock = Arrays.copyOfRange(encryptedBytes, 0, keySize);
        byte[] secondBlock = Arrays.copyOfRange(encryptedBytes, keySize, 2*keySize);
        return (double) (Utils.hammingDistance(firstBlock, secondBlock)/keySize);
    }

    static String decryptAESData(String base64String, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] encryptedBytes = Base64.decodeBase64(base64String.getBytes());
        byte[] decrypted = cipher.doFinal(encryptedBytes);
        return new String(decrypted);
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

    public static void main(String[] args) throws DecoderException, FileNotFoundException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

    }
}
