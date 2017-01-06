import org.apache.commons.codec.binary.Base64;

/**
 * Created by Nika Doghonadze
 */
public class test {
    public static void main(String[] args) {
        System.out.println(new String(Base64.decodeBase64("T3IgcG9saXRlIG1lYW5pbmdsZXNzIHdvcmRzLA==")));
    }
}
