public class CommandHandler {

    private FAT32Reader fatReader;

    public CommandHandler(FAT32Reader f) {
        this.fatReader = f;
    }

    public void info() {
        StringBuilder result = new StringBuilder();

        result.append("BPB_BytesPerSec is 0x");
        String bpsHex = fatReader.getBytes(11, 2);
        result.append(bpsHex);
        result.append(", ");
        result.append(convertHexToDec(bpsHex));

        result.append("\nBPB_SecPerClus is 0x");
        String spcHex = fatReader.getBytes(13, 1);
        result.append(spcHex);
        result.append(", ");
        result.append(convertHexToDec(spcHex));

        result.append("\nBPB_RsvdSecCnt is 0x");
        String rscHex = fatReader.getBytes(14, 2);
        result.append(rscHex);
        result.append(", ");
        result.append(convertHexToDec(rscHex));

        result.append("\nBPB_NumFATs is 0x");
        String nfHex = fatReader.getBytes(16, 1);
        result.append(nfHex);
        result.append(", ");
        result.append(convertHexToDec(nfHex));

        result.append("\nFATSz32 is 0x");
        String fzHex = fatReader.getBytes(36, 4);
        result.append(fzHex);
        result.append(", ");
        result.append(convertHexToDec(fzHex));

        System.out.println(result.toString());

    }

    public void stat(String cmd) {
        StringBuilder result = new StringBuilder();


    }

    public void volume() {

    }

    public void size(String cmd) {

    }

    public void cd(String cmd) {

    }

    public void ls() {

    }

    public void read(String cmd) {

    }
}
