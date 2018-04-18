import org.w3c.dom.Node;

import java.util.HashMap;

/**
 * This command handler accepts the commands from the main class and uses the FAT32Reader class to extract data.
 * Also creates NodeInfo instances to store information about files/directories.
 */

public class CommandHandler {

    private FAT32Reader fatReader;
    private HashMap<String, NodeInfo> dirInfo;
    private int currentDir;
    private HashMap<Integer, String> attributes;

    /**
     * Starts up by setting the root directory as the current directory by calling getRootDir(), and then at the end
     * it loads up all information needed about that directory to process ls and stat commands
     * Also fills the "attributes" HashMap which stores the proper attribute string associated which each number,
     * for use when gathering data about each file/directory in the current directory
     * @param f
     */
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

    /**
     * Called on startup to find the root directory. Does all the computations needed (which were listed in the FAT32
     * spec sheet).
     * @return the offset of the root.
     */
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

    /**
     * Gathers the data about whatever directory was passed in.
     * Fills "dirInfo" HashMap with "NodeInfo" objects which each represent on file or directory within the directory
     * passed in.
     * Uses offsets to find the name, attribute, hi, lo, and size of each file/directory and then create the NodeInfo
     * object
     * @param directory
     */
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
    }

    /**
     * Uses offsets to find all information needed to be returned in info.
     */
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

    /**
     * Takes in the file/directory name which "stat" is being called on. Uses this name as the key into the "dirInfo"
     * HashMap to get the NodeInfo object which corresponds to that file/directory.  Then pulls out any data that needs
     * to be returned.
     * @param cmd
     */
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

    /**
     * Uses the keyset from "dirInfo" to return the names of all files/directories in the current directory.  Checks to
     * make sure they are not the root directory of the file tree before returning them.
     */
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
