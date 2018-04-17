import java.util.Scanner;

public class main {

    public static void main(String[] args) {
        String cmd_line = "";
        Scanner input = new Scanner(System.in);

        FAT32Reader f = new FAT32Reader(args[0]);
        CommandHandler ch = new CommandHandler(f);

        System.out.println(f.getByteContents(11));
        System.out.println(f.getBytes(11, 2));

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
                ch.info();
            }

            else if(cmd_line.startsWith("stat ")) {
                System.out.println("Going to open!\n");
                ch.stat(cmd_line.substring(5));
            }

            else if(cmd_line.startsWith("volume")) {
                System.out.println("Going to close!\n");
                ch.volume();
            }

            else if(cmd_line.startsWith("size ")) {
                System.out.println("Going to size!\n");
                ch.size(cmd_line.substring(5));
            }

            else if(cmd_line.startsWith("cd ")) {
                System.out.println("Going to cd!\n");
                ch.cd(cmd_line.substring(3));
            }

            else if(cmd_line.startsWith("ls")) {
                System.out.println("Going to ls.\n");
                ch.ls();
            }

            else if(cmd_line.startsWith("read ")) {
                System.out.println("Going to read!\n");
                ch.read(cmd_line.substring(5));
            }

            else if(cmd_line.startsWith("quit")) {
                System.out.println("Quitting.\n");
                break;
            }
            else
                System.out.println("Unrecognized command.\n");
        }
    }

}
