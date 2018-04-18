/**
 * This class is used by the CommandHandler to store data about each file/directory in a directory to be used to process
 * commands.
 */
public class NodeInfo {
    private String name;
    private String attributes;
    private int lo;
    private int hi;
    private int size;

    public NodeInfo(String name, String attr, int lo, int hi, int size){
        this.name = name;
        this.attributes = attr;
        this.lo = lo;
        this.hi = hi;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public String getAttributes() {
        return attributes;
    }

    public int getLo() {
        return lo;
    }

    public int getHi() {
        return hi;
    }

    public int getSize() {
        return size;
    }


}
