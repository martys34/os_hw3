import java.util.Scanner;

public class main {

    public static void main(String[] args) {
        //char cmd_line[MAX_CMD];
        String cmd_line = "";
        Scanner input = new Scanner(System.in);

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

            else if(cmd_line.startsWith("open")) {
                System.out.println("Going to open!\n");
            }

            else if(cmd_line.startsWith("close")) {
                System.out.println("Going to close!\n");
            }

            else if(cmd_line.startsWith("size")) {
                System.out.println("Going to size!\n");
            }

            else if(cmd_line.startsWith("cd")) {
                System.out.println("Going to cd!\n");
            }

            else if(cmd_line.startsWith("ls")) {
                System.out.println("Going to ls.\n");
            }

            else if(cmd_line.startsWith("read")) {
                System.out.println("Going to read!\n");
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
