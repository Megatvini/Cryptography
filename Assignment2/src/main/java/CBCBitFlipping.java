import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Nika Doghonadze
 */
public class CBCBitFlipping {
    public static void main(String[] args) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        String s = "NIKA DOGHONADZE ?admin?true";
        byte[] bytes = Utils.encryptUrlCBC(s);
        bytes[32] ^= 4;
        bytes[38] ^= 2;
        System.out.println(Utils.decryptUrlCBC(bytes));
    }
}
