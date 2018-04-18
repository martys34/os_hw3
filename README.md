<h3>Names: Avraham Amon and Marty Spiewak</h4>

<h4>File names and descriptions:</h4>
<ul>
    <li>main.java: starts up the program, sets up data structures to be used by ls and stat commands by calling FAT32Reader.java and CommandHandler.java</li>
    <li>FAT32Reader.java: pulls all data from the fat32.img file and stores it into a byte array for later retreival. Contails useful methods to interpret the contents of the array</li>
    <li>CommandHandler.java: handles the commands passed by user such as info, ls, and stat, and calls the appropriate methods in FAT32Reader.java with the proper offsets defined in the FAT32 spec</li>
    <li>NodeInfor.java: useful struct like class to keep information about each file/directory in the file system</li>
    <li>fat32.img: the img file, which must be passed as a command line argument, whose contents will be read</li>
</ul>

<h4>Instructions for Compilation:</h4>
Compile the main.java as the main program passing in the fat32.img as the sole command line argument.

<h4>Instructions for Running Program:</h4>
When the program starts, it will mimic a basic terminal. The commands for info, ls, and stat work properly.
The stat command takes an argument, and that argument must be in the root directory at this time. The current working stat commands are:
<li><code>stat const.txt</code></li>
<li><code>stat empty.txt</code></li>
<li><code>stat fsinfo.txt</code></li>
<li><code>stat dir</code></li>

The info and ls work by just typing in "info" or "ls".

<h4>Challenges:</h4>
We encountered many challenges, but after reading through the fat32 spec, as well as some slides online, we got a better grasp of the information necessary to complete part 1. We especially struggled with getting the ls and stat command to print the correct data.

<h4>Sources Used:</h4>
We used bless hexeditor as a reference, and we also took a method or two from the web to help with hex to decimal and hex to string conversions. They are sourced in the javadoc.




