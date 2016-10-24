import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

/**
 * Created by Nika Doghonadze
 */
public class MainTest {
    @Test
    public void testHexToBase64() throws DecoderException {
        String hexString = "49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d";
        String res = Main.hexToBase64(hexString);
        String expected = "SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t";
        assertTrue(res.equals(expected));
    }

    @Test
    public void testXorHexString() throws DecoderException {
        String one = "1c0111001f010100061a024b53535009181c";
        String two = "686974207468652062756c6c277320657965";
        String expected = "746865206b696420646f6e277420706c6179";
        String res = Main.xorHexStrings(one, two);
        assertTrue(expected.equals(res));
    }

    @Test
    public void testDecodeSingleByteXor() throws DecoderException {
        String cipher = "1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736";
        String expected = "Cooking MC's like a pound of bacon";
        String res = Main.decodeSingleByteXor(cipher);
        assertTrue(res.equals(expected));
    }

}