import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by Nika Doghonadze
 */
public class DetectAESInECBMode {
    private static final String FILE_NAME = "8.txt";

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Task #8");
        File fileFromResources = Utils.getFileFromResources(FILE_NAME);
        Scanner sc = new Scanner(fileFromResources);
        while (sc.hasNext()) {
            String next = sc.nextLine();
            if (Utils.isEncryptedWithECB(next)) {
                System.out.println(next);
            }
        }
        sc.close();
    }
}
