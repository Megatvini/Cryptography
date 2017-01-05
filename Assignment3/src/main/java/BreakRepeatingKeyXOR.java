import org.apache.commons.codec.binary.Base64;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Nika Doghonadze
 */
public class BreakRepeatingKeyXOR {
    private static String getBestEnglishText(List<String> candidates) {
        List<String> copy = new ArrayList<>(candidates);
        copy.sort(Comparator.comparing(BreakRepeatingKeyXOR::scoreEnglishText));
        Collections.reverse(copy);
        return copy.get(0);
    }

    private static String repeatingXorString(String text, String key) {
        byte[] textBytes = text.getBytes();
        byte[] keyBytes = key.getBytes();
        for (int i=0; i<textBytes.length; i++) {
            textBytes[i] = (byte) (textBytes[i] ^ keyBytes[i%keyBytes.length]);
        }
        return new String(textBytes);
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

    private static byte[] truncateBytes(List<byte[]> lines) {
        int totalLen = 0;
        for (byte[] line : lines) {
            totalLen += line.length;
        }
        byte[] res = new byte[totalLen];

        int curIndex = 0;
        for (byte[] line : lines) {
            System.arraycopy(line, 0, res, curIndex, line.length);
            curIndex += line.length;
        }

        return res;
    }

    private static List<byte[]> makeSameLength(List<byte[]> lines) {
        List<byte[]> res = new ArrayList<>();
        int minLength = findMinLength(lines);
        for (byte[] line : lines) {
            byte[] bytes = Arrays.copyOfRange(line, 0, minLength);
            res.add(bytes);
        }
        return res;
    }

    private static int findMinLength(List<byte[]> lines) {
        int res = Integer.MAX_VALUE;
        for (byte[] line : lines) {
            res = Math.min(res, line.length);
        }
        return res;
    }


    private static final String FILE_NAME = "4.txt";

    public static void main(String[] args) throws FileNotFoundException {
        List<String> lines = Utils.readFileLines(FILE_NAME);
        List<byte[]> byteLines = lines.stream().map(Base64::decodeBase64).collect(Collectors.toList());

        byteLines = makeSameLength(byteLines);
        byte[] cipher = truncateBytes(byteLines);
        String res = decryptRepeatingKeyXor(cipher, Base64.decodeBase64(lines.get(0)).length);
        System.out.println(res);
    }
}
