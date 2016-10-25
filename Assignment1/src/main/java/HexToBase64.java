import org.apache.commons.codec.DecoderException;

/**
 * Created by Nika Doghonadze
 */
public class HexToBase64 {
    private static final String HEX_STRING = "49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d";

    public static void main(String[] args) throws DecoderException {
        System.out.println("Task #1");
        String res = Utils.hexToBase64(HEX_STRING);
        System.out.println(res);
    }
}
