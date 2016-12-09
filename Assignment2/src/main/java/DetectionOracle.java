import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Nika Doghonadze
 */
public class DetectionOracle {
    public static void main(String[] args) throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            str.append("X");
        }
        byte[] data = str.toString().getBytes();
        byte[] encryption = Utils.encryptionOracle(data);
        boolean isECB = Utils.detectOracleModeIsECB(encryption);
        if (isECB) {
            System.out.println("ECB");
        } else {
            System.out.println("CBC");
        }
    }
}
