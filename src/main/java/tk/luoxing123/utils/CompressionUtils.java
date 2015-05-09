package tk.luoxing123.utils;

import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.List;
import java.nio.channels.FileChannel.MapMode;

import it.unimi.dsi.io.ByteBufferInputStream;

import org.iq80.snappy.SnappyInputStream;
import org.iq80.snappy.SnappyOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;

public class CompressionUtils {
    public static InputStream mmapedStream(String filename)
        throws IOException{ return mmapedStream(new File(filename));}
    public static InputStream mmapedStream(File f)
        throws IOException{
        @SuppressWarnings("resource")
		FileInputStream is = new FileInputStream(f);
        return ByteBufferInputStream.map(is.getChannel(),MapMode.READ_ONLY);
    }
    //
    public static List<String> readSnappyCompressedLines(String file)
        throws IOException{

        @SuppressWarnings("resource")
		SnappyInputStream ss =  new SnappyInputStream
            (ByteBufferInputStream.map
             (new FileInputStream(file).getChannel(),MapMode.READ_ONLY)); 
        
        List<String> lines = IOUtils.readLines(ss);
        return lines;
    }
    public static InputStream readSnappyCompressed(String file)
        throws IOException{
        @SuppressWarnings("resource")
		InputStream ss =  new SnappyInputStream
            (ByteBufferInputStream.map
             (new FileInputStream(file).getChannel(),MapMode.READ_ONLY)); 
        return ss;
    }
    public static void writeSnappyCompressed(String src,String dest)
        throws IOException{
        SnappyOutputStream os
            = new SnappyOutputStream(new FileOutputStream(dest));
        FileInputStream fis = new FileInputStream(src);
        ByteBufferInputStream is
            = ByteBufferInputStream.map(fis.getChannel(),
                                        MapMode.READ_ONLY);
        IOUtils.copy(is,os);
        fis.close();
        os.close();
    }
}
