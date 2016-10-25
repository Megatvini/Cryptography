/**
 * Created by Nika Doghonadze
 */
public class RepeatingKeyXOR {
    private static final String TEXT = "Burning 'em, if you ain't quick and nimble\nI go crazy when I hear a cymbal";
    private static final String KEY = "ICE";

    public static void main(String[] args) {
        System.out.println("Task #5");
        System.out.println(Utils.repeatingXorHex(TEXT, KEY));
        System.out.println();
    }
}
