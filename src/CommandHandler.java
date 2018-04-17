import org.w3c.dom.Node;

import java.util.HashMap;

public class CommandHandler {

    private FAT32Reader fatReader;
    private HashMap<String, NodeInfo> dirInfo;
    private int currentDir;
    private HashMap<Integer, String> attributes;

    public CommandHandler(FAT32Reader f) {
        this.fatReader = f;
        this.currentDir = getRootDir();
        this.dirInfo = new HashMap<>();
        this.attributes = new HashMap<>();
        attributes.put(1, "ATTR_READ_ONLY");
        attributes.put(2, "ATTR_HIDDEN");
        attributes.put(4, "ATTR_SYSTEM");
        attributes.put(8, "ATTR_VOLUME_ID");
        attributes.put(16, "ATTR_DIRECTORY");
        attributes.put(32, "ATTR_ARCHIVE");

        gatherData(currentDir);
    }

    public int getRootDir() {
        int rootEntCnt = Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(17, 2)));
        int bytesPerSec = Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(11, 2)));
        int rootDirSectors = ((rootEntCnt * 32) + (bytesPerSec - 1)) / bytesPerSec;

        int resvdSecCnt = Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(14, 2)));
        int numFATS = Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(16, 1)));
        int FATSz = Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(36, 4)));
        int firstDataSector = resvdSecCnt + (numFATS * FATSz) + rootDirSectors;

        int n = Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(44, 4)));
        int secPerClus = Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(13, 1)));
        int firstSecOfClus = (n - 2) * secPerClus + firstDataSector;

        int root = firstSecOfClus * bytesPerSec;

        return root;

    }

    public void gatherData(int directory) {
        int i = 0;
        while(true) {
            int dirOffset = directory + i;

            if(fatReader.removeLeadingZeros(fatReader.getBytes(dirOffset, 32)).equals("0")) {
                break;
            }
            Integer check = Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(dirOffset, 1)));
            if(check == 0 || check == 229) {
                i += 64;
                continue;
            }

            String name = fatReader.convertHexToString(fatReader.getBytes(dirOffset, 11));
            name = name.replaceFirst(" ", ".");
            name = name.replaceAll(" ", "");
            name = name.toLowerCase();
            int attInt = Integer.parseInt(fatReader.convertHexToDec(
                    fatReader.getBytes(dirOffset + 11, 1)));
            String attribute = attributes.get(attInt);
            if(attInt == 16) {
                name = name.substring(0, name.length() - 1);
            }
            int hi = Integer.parseInt(fatReader.convertHexToDec(
                    fatReader.getBytes(dirOffset + 20, 2)));
            int lo = Integer.parseInt(fatReader.convertHexToDec(
                    fatReader.getBytes(dirOffset + 26, 2)));
            int size = Integer.parseInt(fatReader.convertHexToDec(
                    fatReader.getBytes(dirOffset + 28, 4)));

            NodeInfo node = new NodeInfo(name, attribute, lo, hi, size);
            dirInfo.put(name, node);

            i += 64;
        }
        System.out.println();
    }

    public void info() {
        StringBuilder result = new StringBuilder();

        result.append("BPB_BytesPerSec is 0x");
        String bpsHex = fatReader.getBytes(11, 2);
        result.append(bpsHex);
        result.append(", ");
        result.append(fatReader.convertHexToDec(bpsHex));

        result.append("\nBPB_SecPerClus is 0x");
        String spcHex = fatReader.getBytes(13, 1);
        result.append(spcHex);
        result.append(", ");
        result.append(fatReader.convertHexToDec(spcHex));

        result.append("\nBPB_RsvdSecCnt is 0x");
        String rscHex = fatReader.getBytes(14, 2);
        result.append(rscHex);
        result.append(", ");
        result.append(fatReader.convertHexToDec(rscHex));

        result.append("\nBPB_NumFATs is 0x");
        String nfHex = fatReader.getBytes(16, 1);
        result.append(nfHex);
        result.append(", ");
        result.append(fatReader.convertHexToDec(nfHex));

        result.append("\nBPB_FATSz32 is 0x");
        String fzHex = fatReader.getBytes(36, 4);
        result.append(fzHex);
        result.append(", ");
        result.append(fatReader.convertHexToDec(fzHex));

        System.out.println(result.toString());

    }

    public void stat(String cmd) {
        StringBuilder result = new StringBuilder();
        NodeInfo node = dirInfo.get(cmd);
        if(node == null) {
            System.out.println("Error: file/directory does not exist");
            return;
        }
        int nextClusNum = node.getHi() + node.getLo();

        result.append("Size is ");
        result.append(node.getSize());
        result.append("\nAttributes ");
        result.append(node.getAttributes());
        result.append("\nNext cluster number is ");
        result.append(fatReader.convertDecToHex(nextClusNum, 2));

        System.out.println(result.toString());

    }

    public void volume() {

    }

    public void size(String cmd) {

    }

    public void cd(String cmd) {

    }

    public void ls() {
        StringBuilder result = new StringBuilder();
        for(String node : dirInfo.keySet()) {
            if(!dirInfo.get(node).getAttributes().equals("ATTR_VOLUME_ID")){
                result.append(node);
                result.append("    ");
            }
        }
        result.delete(result.length() - 4, result.length());

        System.out.println(result.toString());
    }

    public void read(String cmd) {

    }
}
