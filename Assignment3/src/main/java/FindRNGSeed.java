import java.util.Random;

/**
 * Created by Nika Doghonadze
 */
public class FindRNGSeed {
    public static void main(String[] args) throws InterruptedException {
        int minWaitTime = 40, maxWaitTime = 1000;
        long startTime = System.currentTimeMillis();
        long waitTime = new Random().nextInt(maxWaitTime - minWaitTime) + minWaitTime;
        Thread.sleep(waitTime);
        long seed = System.currentTimeMillis();
        MT_RNG mt_rng = new MT_RNG(seed);

        long randNumber = mt_rng.extractNumber();


        long foundSeed = findSeed(startTime, randNumber);
        System.out.println("Found seed " + foundSeed + " actual seed " + seed);
    }

    private static long findSeed(long startTime, long randNumber) {
        for (int waitTime = 0; waitTime < 2000; waitTime++) {
            long curNum = new MT_RNG(startTime + waitTime).extractNumber();
            if (curNum == randNumber)
                return startTime + waitTime;
        }
        return 0;
    }
}
