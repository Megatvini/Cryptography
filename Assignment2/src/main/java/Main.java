import org.apache.commons.codec.DecoderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Nika Doghonadze
 */
public class Main {
    public static void main(String[] args) throws BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, FileNotFoundException, NoSuchPaddingException, InvalidKeyException, DecoderException {
        AddPadding.main(args);
        CBCMode.main(args);
        DetectionOracle.main(args);
        BreakEncryptionOracle.main(args);
        BreakUserRole.main(args);
        ByteAtATimeECB.main(args);
        CheckPadding.main(args);
        CBCBitFlipping.main(args);
    }
}
