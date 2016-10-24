import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Nika Doghonadze
 */
public class Main {
    public static String hexToBase64(String hexString) throws DecoderException {
        byte[] bytes = Hex.decodeHex(hexString.toCharArray());
        return Base64.encodeBase64String(bytes);
    }

    public static String xorHexStrings(String one, String two) throws DecoderException {
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

    public static String decodeSingleByteXor(String hexString) throws DecoderException {
        byte[] bytes = Hex.decodeHex(hexString.toCharArray());
        List<String> candidates = generateCandidates(bytes);
        return getBestCandidate(candidates);
    }

    private static Integer scoreEnglishText(String text) {
        int resScore = 0;
        for (int i=0; i<text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLetter(c))
                resScore++;
        }
        return resScore;
    }

    private static String getBestCandidate(List<String> candidates) {
        Collections.sort(candidates, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return scoreEnglishText(o1).compareTo(scoreEnglishText(o2));
            }
        });
        Collections.reverse(candidates);
        return candidates.get(0);
    }

    private static List<String> generateCandidates(byte[] bytes) {
        List<String> res = new ArrayList<String>();
        for (char c = 0; c < Character.MAX_VALUE; c++) {
            byte[] copy = cloneByteArrayXor(bytes, (byte) c);
            res.add(new String(copy));
        }
        return res;
    }

    private static byte[] cloneByteArrayXor(byte[] bytes, byte byteToXor) {
        byte[] res = new byte[bytes.length];
        for (int i=0; i<res.length; i++) {
            res[i] = (byte) (bytes[i] ^ byteToXor);
        }
        return res;
    }


    public static void main(String[] args) {
    }
}
