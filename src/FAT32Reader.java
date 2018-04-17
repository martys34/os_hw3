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
            Path path = Paths.get(pathToFile);
            this.contents = Files.readAllBytes(path);
        } catch (IOException e){
            System.out.println(e.getStackTrace());
        }
    }

    public byte getByteContents(int index){
        return contents[index];
    }

    public String convertHexToDec(String hex){
//        String digits = "0123456789ABCDEF";
//        s = s.toUpperCase();
//        int val = 0;
//        for (int i = 0; i < s.length(); i++) {
//            char c = s.charAt(i);
//            int d = digits.indexOf(c);
//            val = 16*val + d;
//        }
//        return val;
//        return "" + val;
        int decimal = Integer.parseInt(hex, 16);
        return "" + decimal;
    }

    public String getBytes(int offset, int size) {
        StringBuilder result = new StringBuilder();

        for(int i = 0; i < size; i++) {
            result.insert(0, String.format("%02X", this.contents[offset + i]));
        }

        String withLeadingZeros = result.toString();
//        System.out.println(withLeadingZeros);

        return removeLeadingZeros(withLeadingZeros);
    }

    public String removeLeadingZeros(String withLeadingZeros){
        int i;
        for(i = 0; i < withLeadingZeros.length(); i++){
            if(withLeadingZeros.charAt(i) != '0')
                break;
        }
        if(withLeadingZeros.substring(i).equals(""))
            return "0";
        return withLeadingZeros.substring(i);
    }

    /**
     * Converts Hex values to Strings
     * Taken from https://www.mkyong.com/java/how-to-convert-hex-to-ascii-in-java/
     * @param hex The hex to be converted
     * @return The String representation of the hex
     */
    public String convertHexToString(String hex){
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        for( int i=0; i<hex.length()-1; i+=2 ){

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char)decimal);

            temp.append(decimal);
        }

        hex = sb.toString();
        sb = new StringBuilder();

        for(int i = hex.length() - 1; i >= 0; i--){
            sb.append(hex.charAt(i));
        }
        hex = sb.toString();

        return hex;
    }

    public String convertDecToHex(int dec, int bytes) {
        StringBuilder result = new StringBuilder();
        result.append("0x");
        String hex = Integer.toHexString(dec);
        int zeroes = (bytes * 2) - hex.toCharArray().length;
        for(int i = 0; i < zeroes; i ++) {
            result.append("0");
        }
        result.append(hex);
        return result.toString();
    }
}
