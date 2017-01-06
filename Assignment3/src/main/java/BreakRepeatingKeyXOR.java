import java.util.*;

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

    private static String repeatingXor(byte[] text, byte[] key) {
        for (int i=0; i<text.length; i++) {
            text[i] = (byte) (text[i] ^ key[i%key.length]);
        }
        return new String(text);
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
        for (int b = 0; b < 256; b++) {
            byte[] copy = xorByteArray(bytes, (byte) b);
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
        byte[] key = findRepeatingKeyXor(cipher, keySize);
        return repeatingXor(cipher, key);
    }

    private static byte[] findRepeatingKeyXor(byte[] cipher, int keySize) {
        byte[][] blocks = breakToBlocks(cipher, keySize);
        byte[][] transposedBlocks = transposeBlocks(blocks);

        byte[] key = new byte[keySize];
        for (int i = 0; i < transposedBlocks.length; i++) {
            byte[] block = transposedBlocks[i];
            byte singleByteXorKey = findSingleByteXorKey(block);
            key[i] = singleByteXorKey;
        }
        return key;
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

    public static void main(String[] args) throws Exception {
        List<String> lines = Utils.readFileLines(FILE_NAME);
        String[] strings = lines.toArray(new String[lines.size()]);
        List<byte[]> ciphers =  Arrays.asList(BreakCTRWithSubstitutions.getCTREncrypts(strings));
        ciphers = makeSameLength(ciphers);
        byte[] cipher = truncateBytes(ciphers);
        String res = decryptRepeatingKeyXor(cipher, ciphers.get(0).length);
        System.out.println(res);
    }
}
