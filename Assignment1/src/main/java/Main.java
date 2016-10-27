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
    public static void main(String[] args) throws FileNotFoundException, DecoderException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        HexToBase64.main(args);
        FixedXOR.main(args);
        SingleByteXORCiphre.main(args);
        DetectSingleCharacterXOR.main(args);
        RepeatingKeyXOR.main(args);
        BreakRepeatingKeyXOR.main(args);
        AECInECBMode.main(args);
        DetectAESInECBMode.main(args);
    }
}
