import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsTest {
    @Test
    public void testHexToBase64() throws DecoderException {
        String hexString = "49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d";
        String res = Utils.hexToBase64(hexString);
        String expected = "SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t";
        assertTrue(res.equals(expected));
    }

    @Test
    public void testXorHexString() throws DecoderException {
        String one = "1c0111001f010100061a024b53535009181c";
        String two = "686974207468652062756c6c277320657965";
        String expected = "746865206b696420646f6e277420706c6179";
        String res = Utils.xorHexStrings(one, two);
        assertTrue(expected.equals(res));
    }

    @Test
    public void testDecodeSingleByteXor() throws DecoderException {
        String cipher = "1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736";
        String expected = "Cooking MC's like a pound of bacon";
        String res = Utils.decodeSingleByteXor(cipher);
        assertTrue(res.equals(expected));
    }

    @Test
    public void testRepeatingXor() throws DecoderException {
        String text = "Burning 'em, if you ain't quick and nimble\nI go crazy when I hear a cymbal";
        String key = "ICE";
        String expected = "0b3637272a2b2e63622c2e69692a23693a2a3c6324202d623d633" +
                "43c2a26226324272765272a282b2f20430a652e2c652a3124333a653e2b2027" +
                "630c692b20283165286326302e27282f";
        String res = Utils.repeatingXorHex(text, key);
        assertTrue(res.equals(expected));
    }

    @Test
    public void testHammingDistance() {
        String one = "this is a test";
        String two = "wokka wokka!!!";
        assertEquals(37, Utils.hammingDistance(one, two));
    }

}