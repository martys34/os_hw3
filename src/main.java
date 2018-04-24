import java.util.Scanner;

/**
 * This class starts by creating a FAT32Reader instance which takes in the FAT32 img.  It also creates a CommandHandler
 * instance.  It then accepts commands from the user and checks which command was made and sends it to the CommandHandler
 * to process.
 */

public class main {

    public static void main(String[] args) {
        String cmd_line = "";
        Scanner input = new Scanner(System.in);

        FAT32Reader f = new FAT32Reader(args[0]);
        CommandHandler ch = new CommandHandler(f);

        /* Parse args and open our image file */

        /* Parse boot sector and get information */

        /* Get root directory address */

	    /* Main loop.  You probably want to create a helper function
            for each command besides quit. */

        while(true) {
            System.out.print("/] ");

            cmd_line = input.nextLine();

            /* Start comparing input */
            if(cmd_line.startsWith("info")) {
                ch.info();
            }

            else if(cmd_line.startsWith("stat ")) {
                ch.stat(cmd_line.substring(5).toLowerCase());
            }

            else if(cmd_line.startsWith("volume")) {
                ch.volume();
            }

            else if(cmd_line.startsWith("size ")) {
                ch.size(cmd_line.substring(5));
            }

            else if(cmd_line.startsWith("cd ")) {
                ch.cd(cmd_line.substring(3));
            }

            else if(cmd_line.startsWith("ls")) {
                ch.ls();
            }

            else if(cmd_line.startsWith("read ")) {
                ch.read(cmd_line.substring(5).trim().toLowerCase());
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
