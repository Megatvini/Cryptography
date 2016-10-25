import org.apache.commons.codec.DecoderException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Nika Doghonadze
 */
public class DetectSingleCharacterXOR {
    private static final String FILE_NAME = "4.txt";

    public static void main(String[] args) throws FileNotFoundException, DecoderException {
        System.out.println("Task #4");

        File file = Utils.getFileFromResources(FILE_NAME);

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
}
