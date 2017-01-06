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
public class BreakCTRWithSubstitutions {
    private static String[] base64Strings = {
            "SSBoYXZlIG1ldCB0aGVtIGF0IGNsb3NlIG9mIGRheQ==",
            "Q29taW5nIHdpdGggdml2aWQgZmFjZXM=",
            "RnJvbSBjb3VudGVyIG9yIGRlc2sgYW1vbmcgZ3JleQ==",
            "RWlnaHRlZW50aC1jZW50dXJ5IGhvdXNlcy4=",
            "SSBoYXZlIHBhc3NlZCB3aXRoIGEgbm9kIG9mIHRoZSBoZWFk",
            "T3IgcG9saXRlIG1lYW5pbmdsZXNzIHdvcmRzLA==",
            "T3IgaGF2ZSBsaW5nZXJlZCBhd2hpbGUgYW5kIHNhaWQ=",
            "UG9saXRlIG1lYW5pbmdsZXNzIHdvcmRzLA==",
            "QW5kIHRob3VnaHQgYmVmb3JlIEkgaGFkIGRvbmU=",
            "T2YgYSBtb2NraW5nIHRhbGUgb3IgYSBnaWJl",
            "VG8gcGxlYXNlIGEgY29tcGFuaW9u",
            "QXJvdW5kIHRoZSBmaXJlIGF0IHRoZSBjbHViLA==",
            "QmVpbmcgY2VydGFpbiB0aGF0IHRoZXkgYW5kIEk=",
            "QnV0IGxpdmVkIHdoZXJlIG1vdGxleSBpcyB3b3JuOg==",
            "QWxsIGNoYW5nZWQsIGNoYW5nZWQgdXR0ZXJseTo=",
            "QSB0ZXJyaWJsZSBiZWF1dHkgaXMgYm9ybi4=",
            "VGhhdCB3b21hbidzIGRheXMgd2VyZSBzcGVudA==",
            "SW4gaWdub3JhbnQgZ29vZCB3aWxsLA==",
            "SGVyIG5pZ2h0cyBpbiBhcmd1bWVudA==",
            "VW50aWwgaGVyIHZvaWNlIGdyZXcgc2hyaWxsLg==",
            "V2hhdCB2b2ljZSBtb3JlIHN3ZWV0IHRoYW4gaGVycw==",
            "V2hlbiB5b3VuZyBhbmQgYmVhdXRpZnVsLA==",
            "U2hlIHJvZGUgdG8gaGFycmllcnM/",
            "VGhpcyBtYW4gaGFkIGtlcHQgYSBzY2hvb2w=",
            "QW5kIHJvZGUgb3VyIHdpbmdlZCBob3JzZS4=",
            "VGhpcyBvdGhlciBoaXMgaGVscGVyIGFuZCBmcmllbmQ=",
            "V2FzIGNvbWluZyBpbnRvIGhpcyBmb3JjZTs=",
            "SGUgbWlnaHQgaGF2ZSB3b24gZmFtZSBpbiB0aGUgZW5kLA==",
            "U28gc2Vuc2l0aXZlIGhpcyBuYXR1cmUgc2VlbWVkLA==",
            "U28gZGFyaW5nIGFuZCBzd2VldCBoaXMgdGhvdWdodC4=",
            "VGhpcyBvdGhlciBtYW4gSSBoYWQgZHJlYW1lZA==",
            "QSBkcnVua2VuLCB2YWluLWdsb3Jpb3VzIGxvdXQu",
            "SGUgaGFkIGRvbmUgbW9zdCBiaXR0ZXIgd3Jvbmc=",
            "VG8gc29tZSB3aG8gYXJlIG5lYXIgbXkgaGVhcnQs",
            "WWV0IEkgbnVtYmVyIGhpbSBpbiB0aGUgc29uZzs=",
            "SGUsIHRvbywgaGFzIHJlc2lnbmVkIGhpcyBwYXJ0",
            "SW4gdGhlIGNhc3VhbCBjb21lZHk7",
            "SGUsIHRvbywgaGFzIGJlZW4gY2hhbmdlZCBpbiBoaXMgdHVybiw=",
            "VHJhbnNmb3JtZWQgdXR0ZXJseTo=",
            "QSB0ZXJyaWJsZSBiZWF1dHkgaXMgYm9ybi4="
    };

    private static Random random = new Random(1);
    private static byte[] key = Utils.generateRandomBytes(random, 16);
    private static int nonce = 0;

    static byte[][] getCTREncrypts(String[] strings) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[][] res = new byte[strings.length][];
        for (int i = 0; i < strings.length; i++) {
            byte[] data = Base64.decodeBase64(strings[i]);
            res[i] = CTR.encrypt(data, key, nonce);
        }
        return res;
    }

    private static byte[][] guessEncrypts(byte[][] encrypts) {
        byte[][] res = new byte[encrypts.length][];
        for (int i = 0; i < res.length; i++) {
            res[i] = guessEncrypts(i, encrypts);
        }
        return res;
    }

    private static byte[] guessEncrypts(int encryptIndex, byte[][] encrypts) {
        byte[][] xorData = getXorData(encryptIndex, encrypts);
        byte[] res = new byte[encrypts[encryptIndex].length];
        for (int i = 0; i < res.length; i++) {
            byte guessedByte = guessByte(i, xorData);
            res[i] = guessedByte;
        }
        return res;
    }

    private static byte guessByte(int byteIndex, byte[][] xorData) {
        int[] score = new int[256];
        for (byte[] xor : xorData) {
            for (int c = 30; c < 150; c++) {
                if (byteIndex < xor.length) {
                    int ch = xor[byteIndex] ^ c;
                    if (Character.isLetter(ch) || Character.isSpaceChar(ch))
                        score[c]++;
                }
            }
        }
        return (byte) maxValueIndex(score);
    }

    private static int maxValueIndex(int[] score) {
        int maxIndex = 0;
        for (int i = 0; i < 256; i++) {
            if (score[i] > score[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private static byte[][] getXorData(int encryptIndex, byte[][] encrypts) {
        byte[][] res = new byte[encrypts.length][];
        for (int i = 0; i < res.length; i++) {
            int minLength = Math.min(encrypts[encryptIndex].length, encrypts[i].length);
            byte[] one = Arrays.copyOfRange(encrypts[encryptIndex], 0, minLength);
            byte[] two = Arrays.copyOfRange(encrypts[i], 0, minLength);
            res[i] = Utils.xorBytes(one, two);
        }
        return res;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException {
        byte[][] encrypts = getCTREncrypts(base64Strings);
        byte[][] guessedEncrypts = guessEncrypts(encrypts);
        for (byte[] guessedEncrypt : guessedEncrypts) {
            System.out.println(new String(guessedEncrypt));
        }
    }
}
