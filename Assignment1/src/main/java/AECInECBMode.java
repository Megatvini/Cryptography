import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Nika Doghonadze
 */
public class AECInECBMode {
    private static final String KEY = "YELLOW SUBMARINE";
    private static final String FILE_NAME = "7.txt";

    public static void main(String[] args) throws FileNotFoundException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        System.out.println("Task #7");
        String str = Utils.readWholeFile(FILE_NAME);
        String s = Utils.decryptAESData(str, KEY);
        System.out.println(s);
    }
}
