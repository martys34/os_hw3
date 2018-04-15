import java.util.Scanner;

public class main {

    public static void main(String[] args) {
        String cmd_line = "";
        Scanner input = new Scanner(System.in);

        FAT32Reader f = new FAT32Reader(args[0]);
        System.out.println(f.getByteContents(11));

        /* Parse args and open our image file */

        /* Parse boot sector and get information */

        /* Get root directory address */
        //System.out.println("Root addr is 0x%x\n", root_addr);


	/* Main loop.  You probably want to create a helper function
       for each command besides quit. */

        while(true) {
            //bzero(cmd_line, MAX_CMD);
            System.out.print("/] ");
            //fgets(cmd_line,MAX_CMD,stdin);

            cmd_line = input.nextLine();

            /* Start comparing input */
            if(cmd_line.startsWith("info")) {
                System.out.println("Going to display info.\n");
            }

            else if(cmd_line.startsWith("stat ")) {
                System.out.println("Going to open!\n");
                stat(cmd_line.substring(5));
            }

            else if(cmd_line.startsWith("volume")) {
                System.out.println("Going to close!\n");
                volume();
            }

            else if(cmd_line.startsWith("size ")) {
                System.out.println("Going to size!\n");
                size(cmd_line.substring(5));
            }

            else if(cmd_line.startsWith("cd ")) {
                System.out.println("Going to cd!\n");
                cd(cmd_line.substring(3));
            }

            else if(cmd_line.startsWith("ls")) {
                System.out.println("Going to ls.\n");
                ls();
            }

            else if(cmd_line.startsWith("read ")) {
                System.out.println("Going to read!\n");
                read(cmd_line.substring(5));
            }

            else if(cmd_line.startsWith("quit")) {
                System.out.println("Quitting.\n");
                break;
            }
            else
                System.out.println("Unrecognized command.\n");
        }
    }

    private static void stat(String cmd) {

    }

    private static void volume() {

    }

    private static void size(String cmd) {

    }

    private static void cd(String cmd) {

    }

    private static void ls() {

    }

    private static void read(String cmd) {

    }

}
