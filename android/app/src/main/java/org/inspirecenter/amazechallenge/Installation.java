package org.inspirecenter.amazechallenge;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * @author Nearchos Paspallis
 */
public class Installation
{
    private static String serialID = null;
    private static final String INSTALLATION = "INSTALLATION";

    public synchronized static String id(Context context) {
        if (serialID == null) {
            final File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists()) {
                    writeInstallationFile(installation);
                }
                serialID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return serialID;
    }

    private static String readInstallationFile(File installation) throws IOException {
        final RandomAccessFile f = new RandomAccessFile(installation, "r");
        final byte [] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        final FileOutputStream out = new FileOutputStream(installation);
        final String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }

    private static long timestamp = 0L;
    private static final String FIRST_USE_TIMESTAMP = "TIMESTAMP";

    public synchronized static long firstUseTimestamp(Context context) {
        if (timestamp == 0) {
            final File firstUseTimestamp = new File(context.getFilesDir(), FIRST_USE_TIMESTAMP);
            try {
                if (!firstUseTimestamp.exists()) {
                    writeFirstUseTimestampFile(firstUseTimestamp);
                }
                timestamp = readFirstUseTimestampFile(firstUseTimestamp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return timestamp;
    }

    private static long readFirstUseTimestampFile(File firstUseTimestamp) throws IOException {
        final RandomAccessFile f = new RandomAccessFile(firstUseTimestamp, "r");
        final byte [] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return Long.parseLong((new String(bytes)));
    }

    private static void writeFirstUseTimestampFile(File firstUseTimestamp) throws IOException {
        final FileOutputStream out = new FileOutputStream(firstUseTimestamp);
        final String timestamp = Long.toString(System.currentTimeMillis());
        out.write(timestamp.getBytes());
        out.close();
    }
}