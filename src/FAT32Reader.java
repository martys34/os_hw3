import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FAT32Reader {
    private byte[] contents;

    public FAT32Reader(String pathToFile){
        try {
            Path path = Paths.get(pathToFile);
            contents = Files.readAllBytes(path);
        } catch (IOException e){
            System.out.println(e.getStackTrace());
        }
    }

    public byte getByteContents(int index){
        return contents[index];
    }
}
