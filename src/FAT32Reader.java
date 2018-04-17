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

    public String getBytes(int index, int offset) {
        StringBuilder result = new StringBuilder();

        for(int i = 0; i < offset; i++) {
            result.insert(0, contents[index + i]);
        }

        return result.toString();
    }
}
