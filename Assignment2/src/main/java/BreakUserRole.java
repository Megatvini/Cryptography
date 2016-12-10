import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Nika Doghonadze
 */
public class BreakUserRole {
    public static void main(String[] args) throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        Utils.Profile profile = Utils.profileFor("ndogh13@fr.eu");
        byte[] encryption = Utils.encryptECBRandomKey(profile);

        byte[] encryptionWithRole = new byte[32];
        System.arraycopy(encryption, 0, encryptionWithRole, 0, encryptionWithRole.length);

        String adminWithPadding = new String(Utils.addPadding("admin".getBytes(), 16));
        Utils.Profile profile1 = Utils.profileFor("          " + adminWithPadding);

        byte[] encryption1 = Utils.encryptECBRandomKey(profile1);
        byte[] adminWithPaddingBlock = new byte[16];
        System.arraycopy(encryption1, adminWithPaddingBlock.length, adminWithPaddingBlock, 0, adminWithPaddingBlock.length);

        byte[] adminProfileEncryption = new byte[48];
        System.arraycopy(encryptionWithRole, 0, adminProfileEncryption, 0, encryptionWithRole.length);
        System.arraycopy(adminWithPaddingBlock, 0, adminProfileEncryption, encryptionWithRole.length, adminWithPaddingBlock.length);

        Utils.Profile admin = Utils.decryptECBProfileRandomKey(adminProfileEncryption);
        System.out.println(admin.toString());
    }
}
