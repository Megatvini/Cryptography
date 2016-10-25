import org.apache.commons.codec.DecoderException;

/**
 * Created by Nika Doghonadze
 */
public class FixedXOR {
    private static final String STRING_ONE = "1c0111001f010100061a024b53535009181c";
    private static final String STRING_TWO = "686974207468652062756c6c277320657965";

    public static void main(String[] args) throws DecoderException {
        System.out.println("Task #2");
        String res = Utils.xorHexStrings(STRING_ONE, STRING_TWO);
        System.out.println(res);
    }
}
