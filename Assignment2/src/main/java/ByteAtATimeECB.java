import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by Nika Doghonadze
 */
public class ByteAtATimeECB {
    public static void main(String[] args) throws BadPaddingException, DecoderException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        int blockSize = findBlockSize();
        int prefixSize = findPrefixSize(blockSize);
        String suffix = breakSuffix(prefixSize, blockSize);
        System.out.println(suffix);
    }

    private static String breakSuffix(int prefixSize, int blockSize) throws BadPaddingException, DecoderException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        int numPrefixBlocks = prefixSize/16;
        if (prefixSize % 16 != 0)
            numPrefixBlocks++;

        byte[] data = new byte[numPrefixBlocks % 16 + blockSize];
        for (int i = 0; i < numPrefixBlocks % 16; i++) {
            data[i] = 1;
        }

        int numDecoded = 0;
        Map<String, Byte> map = new HashMap<>();
        while (true) {
            int dataLen = data.length - numPrefixBlocks % 16;
            for (int i = 0; i < 256; i++) {
                byte b = (byte) i;
                data[data.length - 1] = b;
                byte[] encryption = Utils.encryptionOracleWithPrefixAndPadding(data);
                byte[] firstDataBlock = Arrays.copyOfRange(encryption, numPrefixBlocks*blockSize, numPrefixBlocks*blockSize + blockSize * dataLen/blockSize);
                String hex = Hex.encodeHexString(firstDataBlock);
                map.put(hex, b);
            }

            byte[] dataWithoutLastByte = Arrays.copyOfRange(data, 0, data.length - numDecoded - 1);
            byte[] bytes = Utils.encryptionOracleWithPrefixAndPadding(dataWithoutLastByte);
            byte[] firstDataBlock = Arrays.copyOfRange(bytes, numPrefixBlocks*blockSize, numPrefixBlocks*blockSize + blockSize * dataLen/blockSize);
            String hex = Hex.encodeHexString(firstDataBlock);
            Byte lastByte = map.get(hex);
            if (lastByte == null)
                break;

            data[data.length - 1] = lastByte;
            numDecoded++;

            if (numDecoded % blockSize == 0) {
                byte[] newData = new byte[data.length + blockSize];
                System.arraycopy(data, 0, newData, blockSize - 1, data.length);
                data = newData;
            } else {
                System.arraycopy(data, 1, data, 0, data.length - 1);
            }
        }
        byte[] res = Arrays.copyOfRange(data, data.length - numDecoded - 1, data.length - 1);
        return new String(Utils.removePadding(res));
    }

    private static final int maxSize = 1000;

    private static int findPrefixSize(int blockSize) throws BadPaddingException, DecoderException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            str.append("0");
        }
        String zeroBlocks = str.toString();

        for (int size = 0; size < maxSize; size++) {
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < size; i++) {
                builder.append("1");
            }

            String curString = builder.toString() + zeroBlocks;
            byte[] bytes = Utils.encryptionOracleWithPrefixAndPadding(curString.getBytes());
            int sameBlockIndex = findSameBlocks(bytes, blockSize);
            if (sameBlockIndex != -1)
                return (sameBlockIndex - 1) * blockSize - size + 1;
        }
        return -1;
    }

    private static int findBlockSize() throws BadPaddingException, DecoderException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        int size = Utils.encryptionOracleWithPrefixAndPadding(new byte[0]).length;
        int i = 1;
        while (true){
            int curSize = Utils.encryptionOracleWithPrefixAndPadding(new byte[i]).length;
            if (curSize != size)
                return curSize - size;
            i++;
        }
    }

    private static int findSameBlocks(byte[] bytes, int blockSize) {
        Set<String> set = new HashSet<>();
        for (int curBlockIndex = 0; curBlockIndex < bytes.length / blockSize; curBlockIndex++) {
            byte[] curBlock  = new byte[blockSize];
            System.arraycopy(bytes, curBlockIndex * blockSize, curBlock, 0, blockSize);
            String hexBlock = Hex.encodeHexString(curBlock);
            if (set.contains(hexBlock))
                return curBlockIndex;
            set.add(hexBlock);
        }
        return -1;
    }
}
