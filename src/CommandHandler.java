import org.w3c.dom.Node;

import java.util.*;

/**
 * This command handler accepts the commands from the main class and uses the FAT32Reader class to extract data.
 * Also creates NodeInfo instances to store information about files/directories.
 */

public class CommandHandler {

    private FAT32Reader fatReader;
    private HashMap<String, NodeInfo> dirInfo;

    private HashMap<Integer, String> attributes;

    private int resvdSecCnt;
    private int numFATS;
    private int FATSz;
    private int rootDirSectors;
    private int secPerClus;
    private int bytesPerSec;
    private int bytesPerClus;

    private String volumeID;

    private boolean dots = false;
    private boolean updatedN = false;
    private int levelsIn;

    private ArrayList<Integer> freeClusterIndices;

    /**
     * Starts up by setting the root directory as the current directory by calling getRootDir(), and then at the end
     * it loads up all information needed about that directory to process ls and stat commands
     * Also fills the "attributes" HashMap which stores the proper attribute string associated which each number,
     * for use when gathering data about each file/directory in the current directory
     * @param f
     */
    public CommandHandler(FAT32Reader f) {
        this.fatReader = f;
        this.dirInfo = new HashMap<>();
        this.attributes = new HashMap<>();
        attributes.put(1, "ATTR_READ_ONLY");
        attributes.put(2, "ATTR_HIDDEN");
        attributes.put(4, "ATTR_SYSTEM");
        attributes.put(8, "ATTR_VOLUME_ID");
        attributes.put(16, "ATTR_DIRECTORY");
        attributes.put(32, "ATTR_ARCHIVE");

        this.levelsIn = 0;
        freeClusterIndices = new ArrayList<>();

        gatherData(getRootDir());
        constructFreeListData();
    }

    /**
     * Called on startup to find the root directory. Does all the computations needed (which were listed in the FAT32
     * spec sheet).
     * @return the offset of the root.
     */
    private int getRootDir() {
        int rootEntCnt = Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(17, 2)));
        bytesPerSec = Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(11, 2)));
        rootDirSectors = ((rootEntCnt * 32) + (bytesPerSec - 1)) / bytesPerSec;

        resvdSecCnt = Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(14, 2)));
        numFATS = Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(16, 1)));
        FATSz = Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(36, 4)));
        int firstDataSector = resvdSecCnt + (numFATS * FATSz) + rootDirSectors;

        int n = Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(44, 4)));
        secPerClus = Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(13, 1)));
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
    private void gatherData(int directory) {
        int i = 0;
        if(updatedN) {
            i = 32;
        }
        while(true) {
            int dirOffset = directory + i;

            if(i >= 512) {   //fatReader.removeLeadingZeros(fatReader.getBytes(dirOffset, 32)).equals("0")) {
                break;
            }
            Integer check = Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(dirOffset, 1)));
            if(check == 0 || check == 229) {
                i += 64;
                continue;
            }

            String name = fatReader.convertHexToString(fatReader.getBytes(dirOffset, 11));
            if(!name.startsWith(".")) {
                name = name.replaceFirst(" ", ".");
            }
            name = name.replaceAll(" ", "");
            name = name.toLowerCase();
            int attInt = Integer.parseInt(fatReader.convertHexToDec(
                    fatReader.getBytes(dirOffset + 11, 1)));
            String attribute = attributes.get(attInt);
            if(attInt == 8) {
                this.volumeID = name.toUpperCase().replace(".", "");
            }
            if(attInt == 16 && !name.startsWith(".")) {
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

            if(this.dots) {
                if(name.equals(".")) {
                    this.dots = false;
                }
                i += 32;
            }
            else {
                i += 64;
            }

        }
    }

    private void constructFreeListData() {
        int n = Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(44, 4))); //2
        int fatOffset = n * 4;
        int thisFATSecNum = resvdSecCnt + (fatOffset / bytesPerSec);
        int thisFATEntOffset = fatOffset % bytesPerSec;
        this.bytesPerClus = bytesPerSec * this.secPerClus;
        int fatTable = thisFATSecNum * bytesPerClus;

        int fatTable2Index = (thisFATSecNum + Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(36, 4)))) * bytesPerClus;

        int fatIndex = 0;
        for(int i = fatTable; i < fatTable2Index; i+=4){
            int bytes = Integer.parseInt(this.fatReader.convertHexToDec(this.fatReader.getBytes(i, 4)));
            if(bytes == 0){
                this.freeClusterIndices.add(fatIndex);
            }
            fatIndex++;
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
            System.out.println("Error: file/directory does not exist.");
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

    /**
     * Prints out the volume id of the volume which was collected when object was created.
     */
    public void volume() {
        System.out.println(this.volumeID);
    }

    /**
     * Prints the size of the node.
     * @param cmd the node whose size is desired
     */
    public void size(String cmd) {
        StringBuilder result = new StringBuilder();
        NodeInfo node = dirInfo.get(cmd);
        if(node == null) {
            System.out.println("Error: file/directory does not exist.");
            return;
        }
        result.append("Size is ");
        result.append(node.getSize());
        System.out.println(result.toString());
    }

    /**
     * Changes the current directory to the a child directory
     * @param cmd the directory to be entered
     */
    public void cd(String cmd) {
        NodeInfo node = dirInfo.get(cmd);
        if(node == null) {
            System.out.println("Error: file/directory does not exist.");
            return;
        }
        if(!node.getAttributes().equals("ATTR_DIRECTORY")) {
            System.out.println("Error: cannot cd into a file.");
            return;
        }
        if(cmd.equals("..")) {
            levelsIn--;
            if(levelsIn == 0) {
                dirInfo.clear();
                gatherData(getRootDir());
                return;
            }
        }
        else {
            levelsIn++;
        }
        this.dots = true;

        int n = getN(node.getHi(), node.getLo());

        dirInfo.clear();

        //decimal value of EOC (end of cluster)
        while(n < 268435448) {
            int firstDataSec = resvdSecCnt + (numFATS * FATSz) + rootDirSectors;
            int firstSecOfClus = (n - 2) * secPerClus + firstDataSec;
            int dir = firstSecOfClus * bytesPerSec;
            gatherData(dir);
            n = updateN(n);
            updatedN = true;
        }

        this.dots= false;
        updatedN = false;
    }

    /**
     * Uses the keyset from "dirInfo" to return the names of all files/directories in the current directory.  Checks to
     * make sure they are not the root directory of the file tree before returning them.
     */
    public void ls() {
        StringBuilder result = new StringBuilder();
        List<String> sortedNames = new ArrayList<>(dirInfo.keySet());
        Collections.sort(sortedNames);
        for(String node : sortedNames) {
            if(!dirInfo.get(node).getAttributes().equals("ATTR_VOLUME_ID")){
                result.append(node);
                result.append("    ");
            }
        }
        result.delete(result.length() - 4, result.length());

        System.out.println(result.toString().toUpperCase());
    }

    /**
     * Parses cmd to read from a file at supplied position value for supplied number of bytes, and prints it to the screen.
     * e.g. read file.txt 100 34 will read from file.txt starting at byte 100 and will print out the next 34 bytes.
     * @param cmd the command to read from a file.
     */
    public void read(String cmd) {
        try {
            String[] split = cmd.split(" ");
            if (split.length < 3) {
                System.out.println("Error: please enter file, position, number of bytes.");
                return;
            }

            String name = split[0];
            int position = Integer.parseInt(split[1]);
            int bytes = Integer.parseInt(split[2]);
            NodeInfo node = dirInfo.get(name);
            if (Integer.parseInt(split[2]) + Integer.parseInt(split[1]) > node.getSize()) {
                System.out.println("Attempting to read beyond the end of the file.");
                return;
            }
            int n = getN(node.getHi(), node.getLo());
            int firstDataSec = resvdSecCnt + (numFATS * FATSz) + rootDirSectors;
            int firstSecOfClus = (n - 2) * secPerClus + firstDataSec;
            int file = firstSecOfClus * bytesPerSec;

            String result = readBytes(file, position, bytes);
            System.out.println(result);
        } catch(Exception e) {
            uhOh();
        }
    }

    /**
     * Updates the value of N that is used for finding the offset of a directory to determine if it spans multiple
     * clusters. If the value of N that is returned is >= 268435448, then the end of the cluster has been reached.
     * @param n the previous value of N
     * @return the new value of N found in the FAT table
     */
    private int updateN(int n) {
        int fatOffset = n * 4;
        int thisFATSecNum = resvdSecCnt + (fatOffset / bytesPerSec);
        int thisFATEntOffset = fatOffset % bytesPerSec;
        int fatTable = thisFATSecNum * bytesPerSec;

        return Integer.parseInt(fatReader.convertHexToDec
                (this.fatReader.getBytes(fatTable + thisFATEntOffset, 4)));
    }

    /**
     * Gets the value of N from the supplied hi and lo values of a node
     * @param hi the hi value of the node
     * @param lo the lo value of the node
     * @return the corresponding value of N
     */
    private int getN(int hi, int lo) {
        String hiHex = fatReader.convertDecToHex(hi, 2);
        String loHex = fatReader.convertDecToHex(lo, 2);
        //gets rid of 0x
        String hex = hiHex.substring(2) + loHex.substring(2);
        return Integer.parseInt(fatReader.convertHexToDec(hex));
    }

    /**
     * Reads bytes from a file represented as an integer offset found in the FAT32 img. Reading starts at
     * the position supplied, and reads the bytes (supplied) number of bytes starting from position
     * @param file the offset of the file to be read
     * @param position where to start reading
     * @param bytes how many bytes to read
     * @return a String containing the bytes read from the file
     */
    private String readBytes(int file, int position, int bytes) {
        StringBuilder result = new StringBuilder();
        for(int count = 0; count < bytes; count++) {
            String letter = fatReader.getBytes(file + position + count, 1);
            if(Integer.parseInt(fatReader.convertHexToDec
                    (fatReader.getBytes(file + position + count, 4))) == 268435448) {
                file = updateN(file);
            }
            result.append(fatReader.convertHexToString(letter));
        }
        return result.toString();
    }

    public void freeList() {
        System.out.println("First free cluster: " + this.freeClusterIndices.get(0));
        System.out.println("Second free cluster: " + this.freeClusterIndices.get(1));
        System.out.println("Three free cluster: " + this.freeClusterIndices.get(2));
        System.out.println("Number of free clusters: " + this.freeClusterIndices.size());
    }

    public void newFile(String cmd){
        String[] split = cmd.split(" ");
        if(split.length > 2){
            System.out.println("newfile command takes in only a filename and a size");
            return;
        }
        String fileName = split[0];
        if(fileName.length() > 8){
            System.out.println("filename must be 8 characters or less");
            return;
        }
        if(fileName.length() < 8 ){
            int toAdd = 8 - fileName.length();
            for(int i = 0; i < toAdd; i++){
                fileName = fileName + " ";
            }
        }

        int size = 0;
        try {
            size = Integer.parseInt(split[1]);
        } catch (NumberFormatException e){
            System.out.println("Second argument must be an integer");
            return;
        }

        createNewFile(fileName, size);
    }

    private void createNewFile(String fileName, int size){
        //Part 1: write to fat tables:
        int n = Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(44, 4))); //2
        int fatOffset = n * 4;
        int thisFATSecNum = resvdSecCnt + (fatOffset / bytesPerSec);
        int thisFATEntOffset = fatOffset % bytesPerSec;
        int bytesPerClus = bytesPerSec * this.secPerClus;
        int fatTable = thisFATSecNum * bytesPerClus;

        int fatTable2Index = (thisFATSecNum + Integer.parseInt(fatReader.convertHexToDec(fatReader.getBytes(36, 4)))) * bytesPerClus;

        int bytesToWrite = size;
        int firstN = 0;

        while(size > 0){
            int firstFreeCluster = this.freeClusterIndices.remove(0);
            firstN = firstFreeCluster;
            int toWrite = 0;
            if(size > this.bytesPerClus){
                toWrite = size - this.bytesPerClus;
                size -= this.bytesPerClus;
                byte[] bytes = this.fatReader.convertDecToHexBytes(this.freeClusterIndices.get(0));
                this.fatReader.writeToImage(fatTable + firstFreeCluster, bytes);
                this.fatReader.writeToImage(fatTable + firstFreeCluster + fatTable2Index, bytes);
            }
            else{
                byte[] bytes = this.fatReader.convertDecToHexBytes(268435448);
                this.fatReader.writeToImage(fatTable + firstFreeCluster, bytes);
                this.fatReader.writeToImage(fatTable + firstFreeCluster + fatTable2Index, bytes);
                break;
            }
        }

        //Part 2: actually create file:
        byte[] bytes = this.fatReader.convertDecToHexBytes(firstN);
        String hi = "" + bytes[3] + bytes[2];
        hi = this.fatReader.convertHexToDec(hi);
        int nodeHi = Integer.parseInt(hi);

        String lo = "" + bytes[1] + bytes[0];
        lo = this.fatReader.convertHexToDec(lo);
        int nodeLo = Integer.parseInt(hi);

        NodeInfo node = new NodeInfo(fileName, "ATTR_ARCHIVE", nodeLo, nodeHi, bytesToWrite);
        this.dirInfo.put(fileName, node);

        int bytesLeft = bytesToWrite;
        for(int count = 0; count < bytesToWrite; count += this.bytesPerClus){

            byte[] b = getValueBytes("New File.\r\n");
            if(bytesLeft > this.bytesPerClus){
                this.fatReader.writeBytes(firstN, this.bytesPerClus, b);
                bytesLeft -= this.bytesPerClus;
            }
            else{
                this.fatReader.writeBytes(firstN, bytesLeft, b);
            }
            firstN = updateN(firstN);
        }
    }

    private byte[] getValueBytes(String val){
        String toAdd = val;
        int loop = this.bytesPerClus % val.length() / 2;// because every two is one byte.
        for(int i = 0; i < loop; i++){
            val += toAdd;
        }
        val += "New F";
        return val.getBytes();
    }

    private void uhOh() {
        System.out.println("Sorry professor, we aren't sure why this failed.");
        for (int codePoint = 0x1F600; codePoint <= 0x1F64F;) {
            System.out.print(Character.toChars(codePoint));
            codePoint++;
            if (codePoint % 16 == 0) {
                System.out.println();
            }
        }
        System.out.println("But the password is: b@n@n@S!");
        System.out.println("And we thought you might enjoy this video on pointers: http://www.youtube.com/watch?v=6pmWojisM_E");
    }

}
