import org.apache.commons.codec.DecoderException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Nika Doghonadze
 */
public class Main {
    public static void main(String[] args) throws FileNotFoundException, DecoderException {
        decipherFile();
    }

    private static void decipherFile() throws FileNotFoundException, DecoderException {
        File file = Utils.getFileFromResources("4.txt");
        if (file == null)
            return;
        List<String> candidates = new ArrayList<>();
        Scanner sc = new Scanner(file);
        while (sc.hasNext()) {
            candidates.add(Utils.decodeSingleByteXor(sc.nextLine()));
        }
        System.out.println(Utils.getBestEnglishText(candidates));
    }
}
