package sia.tcloud3.service.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ImageUtils {
    public static final int BYTE_SIZE = 4 * 1024;

    public static byte[] compressImage(byte[] data) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] temp = new byte[BYTE_SIZE];

        while (! deflater.finished()) {
            int size = deflater.deflate(temp);
            outputStream.write(temp, 0, size);
        }

        outputStream.close();
        return outputStream.toByteArray();
    }

    public static byte[] decompressImage(byte[] data) throws DataFormatException, IOException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] temp = new byte[BYTE_SIZE];

        while (! inflater.finished()) {
            int size = inflater.inflate(temp);
            outputStream.write(temp, 0, size);
        }

        outputStream.close();
        return outputStream.toByteArray();
    }
}
