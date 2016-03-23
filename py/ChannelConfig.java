package <your packagename>;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.io.DataInput;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ChannelConfig {

    private static final String DEFAULT_CHANNEL = "portal";

    // ZIP文件注释长度字段的字节数
    private static final int SHORT_LENGTH = 2;
    // 文件最后用于定位的MAGIC字节
    private static final byte[] MAGIC = new byte[]{0x63, 0x68, 0x72, 0x69, 0x73}; // chris

    public static String getChannel(Context context) { //Application context
        ApplicationInfo info = context.getApplicationInfo();
        String sourceDir = info.sourceDir;

        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(sourceDir, "r");
            long index = raf.length();
            byte[] buffer = new byte[MAGIC.length];
            index -= MAGIC.length;
            // read magic bytes
            raf.seek(index);
            raf.readFully(buffer);
            // if magic bytes matched
            if (isMagicMatched(buffer)) {
                index -= SHORT_LENGTH;
                raf.seek(index);
                // read content length field
                int length = readShort(raf);
                if (length > 0) {
                    index -= length;
                    raf.seek(index);
                    // read content bytes
                    byte[] bytesComment = new byte[length];
                    raf.readFully(bytesComment);
                    return new String(bytesComment, "UTF-8");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (raf != null)
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return DEFAULT_CHANNEL;
    }

    private static boolean isMagicMatched(byte[] buffer) {
        if (buffer.length != MAGIC.length) {
            return false;
        }
        for (int i = 0; i < MAGIC.length; ++i) {
            if (buffer[i] != MAGIC[i]) {
                return false;
            }
        }
        return true;
    }

    private static short readShort(DataInput input) throws IOException {
        byte[] buf = new byte[SHORT_LENGTH];
        input.readFully(buf);
        ByteBuffer bb = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN);
        return bb.getShort(0);
    }
}
