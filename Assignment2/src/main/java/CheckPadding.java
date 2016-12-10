/**
 * Created by Nika Doghonadze
 */
public class CheckPadding {
    public static void main(String[] args) {
        String good = "ICE ICE BABY" + new String(new byte[]{4, 4, 4, 4});
        String bad1 = "ICE ICE BABY" + new String(new byte[]{5, 5, 5, 5});
        String bad2 = "ICE ICE BABY" + new String(new byte[]{1, 2, 3, 4});
        System.out.println(Utils.hasValidPadding(good.getBytes()));
        System.out.println(Utils.hasValidPadding(bad1.getBytes()));
        System.out.println(Utils.hasValidPadding(bad2.getBytes()));
    }
}
