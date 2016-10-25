import org.apache.commons.codec.DecoderException;

/**
 * Created by Nika Doghonadze
 */
public class SingleByteXORCiphre {

    public static final String HEX_CIPHRE = "1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736";

    public static void main(String[] args) throws DecoderException {
        System.out.println("Task #3");
        String res = Utils.decodeSingleByteXor(HEX_CIPHRE);
        System.out.println(res);
    }
}
