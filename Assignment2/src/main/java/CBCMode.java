import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Nika Doghonadze
 */
public class CBCMode {
    public static void main(String[] args) throws FileNotFoundException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        byte[] key = "YELLOW SUBMARINE".getBytes();
        byte[] IV = new byte[key.length];

        byte[] encrypt = Base64.decodeBase64(Utils.readWholeFile("2.txt").getBytes());
        byte[] decrypt = Utils.CBCDecrypt(encrypt, key, IV);
        System.out.println(new String(decrypt));
    }
}