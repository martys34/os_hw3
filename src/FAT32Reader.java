import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class interacts with the FAT32.img. It starts up by reading the bytes from FAT32.img into a byte array and then
 * interacts only with that byte array afterwards.
 * This class also handles simple conversions between hex, decimal, and string.
 */
public class FAT32Reader {
    private byte[] contents;
    private String pathToFile;

    public FAT32Reader(String pathToFile){
        this.pathToFile = pathToFile;
        try {
            Path path = Paths.get(pathToFile);
            this.contents = Files.readAllBytes(path);
        } catch (IOException e){
            System.out.println(e.getStackTrace());
        }
    }


    /**
     * Returns bytes from the FAT32.img.  An offset and size are passed in and the bytes in that range are returned
     * in a String form.
     * @param offset
     * @param size
     * @return String representation of the bytes requested.
     */
    public String getBytes(int offset, int size) {
        StringBuilder result = new StringBuilder();

        for(int i = 0; i < size; i++) {
            result.insert(0, String.format("%02X", this.contents[offset + i]));
        }

        String withLeadingZeros = result.toString();

        return removeLeadingZeros(withLeadingZeros);
    }

    /**
     * Writes numBytes bytes to disk starting at offset
     * @param offset the offset to start writing at
     * @param numBytes the number of bytes to write
     * @param contents the bytes to write
     */
    public void writeBytes(int offset, int numBytes, byte[] contents) {
        int i = offset;
        for(byte b : contents) {
            if(i - offset >= numBytes) {
                break;
            }
            this.contents[i] = b;
            i++;
        }
    }

    /**
     * Removes leading zeroes from a String representing a hexadecimal number.
     * @param withLeadingZeros
     * @return String representation of hex number without leading zeroes.
     */
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
     * Converts a String representing a hexadecimal number into a decimal number.
     * @param hex
     * @return String representation of decimal value.
     */
    public String convertHexToDec(String hex){
        int decimal = Integer.parseInt(hex, 16);
        return "" + decimal;
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

    /**
     * Converts an int into a String representation of a hexadecimal number with a given number of bytes.
     * @param dec
     * @param bytes
     * @return String representation of hex number
     */
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

    /**
     * Converts the supplied int to a size 4 byte array
     * @param dec the int to be converted
     * @return a byte[] containing the 4 byte representation of the supplied int
     */
    public byte[] convertDecToHexBytes(int dec) {
        return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(dec).array();
    }

    /**
     * Writes bytes[] to disk at index in reverse order
     * @param index the index to start writing at
     * @param bytes the bytes to be written in reverse order
     */
    public void writeToImage(int index, byte[] bytes){
        this.contents[index++] = bytes[3];
        this.contents[index++] = bytes[2];
        this.contents[index++] = bytes[1];
        this.contents[index] = bytes[0];
    }

    /**
     * Writes one byte to disk at offset
     * @param offset the offset to write the byte at
     * @param b the byte to be written
     */
    public void writeOneByte(int offset, byte b) {
        this.contents[offset] = b;
    }

    /**
     * Actually writes the contents of the contents array containing the FAT 32 image to a file for persistence.
     */
    public void flushImage() {
        try {
            Path path = Paths.get(pathToFile);
            Files.write(path, this.contents);
        } catch (IOException e){
            System.out.println(e.getStackTrace());
        }
    }

}
