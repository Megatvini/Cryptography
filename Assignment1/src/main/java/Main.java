import org.apache.commons.codec.DecoderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Nika Doghonadze
 */
public class Main {
    public static void main(String[] args) throws FileNotFoundException, DecoderException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        task1();
        task2();
        task3();
        task4();
        task5();
        task6();
        task7();
        task8();
    }

    private static void task8() {
        System.out.println("Task #8");
        System.out.println();
    }

    private static void task7() throws FileNotFoundException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        System.out.println("Task #7");
        File fileFromResources = Utils.getFileFromResources("7.txt");
        String str = "";
        String key = "YELLOW SUBMARINE";
        Scanner sc = new Scanner(fileFromResources);
        while (sc.hasNext())
            str += sc.nextLine();
        sc.close();
        String s = Utils.decryptAESData(str, key);
        System.out.println(s);
        System.out.println();
    }

    private static void task6() throws FileNotFoundException {
        System.out.println("Task #6");
        String filename = "6.txt";
        File file = Utils.getFileFromResources(filename);

        if (file == null)
            return;

        StringBuilder cypher = new StringBuilder();
        Scanner sc = new Scanner(file);
        while (sc.hasNext()) {
            String nextLine = sc.next();
            cypher.append(nextLine);
        }
        sc.close();

        String res = Utils.decryptRepeatingKeyXor(cypher.toString());
        System.out.println(res);
    }

    private static void task5() {
        System.out.println("Task #5");
        String text = "Burning 'em, if you ain't quick and nimble\nI go crazy when I hear a cymbal";
        String key = "ICE";
        System.out.println(Utils.repeatingXorHex(text, key));
        System.out.println();
    }

    private static void task4() throws FileNotFoundException, DecoderException {
        System.out.println("Task #4");

        String filename = "4.txt";
        File file = Utils.getFileFromResources(filename);

        if (file == null)
            return;

        List<String> candidates = new ArrayList<>();
        Scanner sc = new Scanner(file);
        while (sc.hasNext()) {
            candidates.add(Utils.decodeSingleByteXor(sc.nextLine()));
        }

        sc.close();
        System.out.println(Utils.getBestEnglishText(candidates));
    }

    private static void task3() throws DecoderException {
        System.out.println("Task #3");
        String cipher = "1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736";
        String res = Utils.decodeSingleByteXor(cipher);
        System.out.println(res);
        System.out.println();
    }

    private static void task2() throws DecoderException {
        System.out.println("Task #2");
        String one = "1c0111001f010100061a024b53535009181c";
        String two = "686974207468652062756c6c277320657965";
        String res = Utils.xorHexStrings(one, two);
        System.out.println(res);
        System.out.println();
    }

    private static void task1() throws DecoderException {
        System.out.println("Task #1");
        String hexString = "49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d";
        String res = Utils.hexToBase64(hexString);
        System.out.println(res);
        System.out.println();
    }
}
