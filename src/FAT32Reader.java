import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FAT32Reader {
    private byte[] contents;

    public FAT32Reader(String pathToFile){
        try {
//            File file = new File(pathToFile);
            Path path = Paths.get(pathToFile);
            this.contents = Files.readAllBytes(path);
//            List<String> hexVals =  new ArrayList<>(this.contents.length);
//            for(Byte b : this.contents){
//                hexVals.add(String.format("%02X", b));
//            }
//            System.out.println("");
//            InputStream is = new FileInputStream(file);
//            int value = 0;
//            while ((value = is.read()) != -1) {
//                String hex = Integer.toHexString(value).toUpperCase();
////              System.out.println(hex);
//                hexVals.add(hex);
//            }
//            is.close();

        } catch (IOException e){
            System.out.println(e.getStackTrace());
        }
    }

    public byte getByteContents(int index){
        return contents[index];
    }

    public String convertHexToDec(String hex){
        int decimal = Integer.parseInt(hex, 16);
        return "" + decimal;
    }

    public String getBytes(int index, int offset) {
        StringBuilder result = new StringBuilder();

        for(int i = 0; i < offset; i++) {
            result.insert(0, String.format("%02X", this.contents[index + i]));
        }

        String withLeadingZeros = result.toString();
        result = new StringBuilder();
        int i;
        for(i = 0; i < withLeadingZeros.length(); i++){
            if(withLeadingZeros.charAt(i) != '0')
                break;
        }
        return withLeadingZeros.substring(i);
    }
}
