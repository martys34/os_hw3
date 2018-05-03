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

    /**
     * Constructs NodeInfo object from the parameters provided.
     * @param name the name of the node
     * @param attr the attribute of the node
     * @param lo the lo value of the node
     * @param hi the hi value of the node
     * @param size the size of the node
     */
    public NodeInfo(String name, String attr, int lo, int hi, int size){
        this.name = name;
        this.attributes = attr;
        this.lo = lo;
        this.hi = hi;
        this.size = size;
    }

    /**
     * Returns the name of the node
     * @return the name of the node
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the attribute of the node
     * @return the attribute of the node
     */
    public String getAttributes() {
        return attributes;
    }

    /**
     * Returns the lo value of the node
     * @return the lo value of the node
     */
    public int getLo() {
        return lo;
    }

    /**
     * Returns the hi value of the node
     * @return the hi value of the node
     */
    public int getHi() {
        return hi;
    }

    /**
     * Returns the size of the node
     * @return the size of the node
     */
    public int getSize() {
        return size;
    }


}
