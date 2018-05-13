<h3>Names: Avraham Amon and Marty Spiewak</h4>

<h4>File names and descriptions:</h4>
<ul>
    <li>main.java: starts up the program, sets up data structures to be used by ls, stat, cd, volume, read, and size commands by calling FAT32Reader.java and CommandHandler.java</li>
    <li>FAT32Reader.java: pulls all data from the fat32.img file and stores it into a byte array for later retreival. Contails useful methods to interpret the contents of the array</li>
    <li>CommandHandler.java: handles the commands passed by user such as info, ls, stat, cd, read, size and volume and calls the appropriate methods in FAT32Reader.java with the proper offsets defined in the FAT32 spec</li>
    <li>NodeInfo.java: useful struct like class to keep information about each file/directory in the file system</li>
    <li>fat32.img: the img file, which must be passed as a command line argument, whose contents will be read</li>
</ul>

<h4>Instructions for Compilation:</h4>
Compile the main.java as the main program passing in the fat32.img as the sole command line argument when executing.

<h4>Instructions for Running Program:</h4>
When the program starts, it will mimic a basic terminal. The commands for info, ls, stat, cd, read, size, volume, freelist, newfile, and delete work properly.
The stat command takes an argument in the current directory. Examples of stat commands are:
<li><code>stat const.txt</code></li>
<li><code>stat empty.txt</code></li>
<li><code>stat fsinfo.txt</code></li>
<li><code>stat dir</code></li>
This command, like all others will work in any directory.

The cd, and size function exactly like stat as well.

The info, freelist and ls work by just typing in "info" or "ls".

If the working directory has other directories one could cd into, doing an ls on the directory will show the contents of the directory.
Example: <code>ls dir</code> will do an ls on the directory called dir without changing the working directory. 

The read command works by typing "read" followed by the file to be read, the starting byte position, and the number of bytes to read.
Example: <code>read const.txt 0 1000</code> will read the from const.txt starting from the first byte to the 1000th byte.

The newfile command works by typing "newfile" followed by the name of the file to be created. File name must be maximum of 8 chars long, and a 3 character extension name must be supplied. User must also specify the size of the file. The program will create a file with the supplied name, containing the string "New file.\r\n" repeating.
Example: <code>newfile file.txt 5</code> will create a new file called file.txt in the working directory, and "New F" (size 5) will be written to it.

The delete command works by typing "delete" followed by the file to be deleted. Directories cannot be deleted.
Example: <code>delete file.txt</code> will delete the file called file.txt from the working directory if it exists there.

Type "quit to exit the program."

<h4>Challenges:</h4>
We encountered many challenges, but after reading through the fat32 spec, as well as some slides online, we got a better grasp of the information necessary to complete part 1. We especially struggled with getting the ls and stat command to print the correct data. Part 2 posed some challenges as well, such as figuring out how to read more than one cluster at a time, but ultimately, the data structures that we built in part 1 helped save a lot of time.
newfile gave us a lot of problems because of the way the at we wrote our code. We ended up having to rethink the way we did a lot of our previously made methods to get it to work properly.

<h4>Sources Used:</h4>
We used bless hexeditor as a reference, and we also took a method or two from the web to help with hex to decimal and hex to string conversions. They are sourced in the javadoc.

