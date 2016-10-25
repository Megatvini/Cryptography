import java.io.FileNotFoundException;

/**
 * Created by Nika Doghonadze
 */
public class BreakRepeatingKeyXOR {
    private static final String FILE_NAME = "6.txt";

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Task #6");
        String cypher = Utils.readWholeFile(FILE_NAME);
        String res = Utils.decryptRepeatingKeyXor(cypher);
        System.out.println(res);
    }
}
