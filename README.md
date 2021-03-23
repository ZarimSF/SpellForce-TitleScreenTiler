# SpellForce-TitleScreenTiler
For some reason, Phenomic made it quite complicated to replace the title screen of SpellForce Platinum-Edition. This program takes care of that.

# Prequisites
- Install Java 14 first
- Download the latest release .jar-File (Java executable)
- Design a new title screen as you like but with a width of 1026 and a height of 770 pixels
- Save it as a .png image
- Create a folder where the output tiles will be saved

# Usage
- Open command line (cmd)
- Type `java -jar ` (with whitespaces)
- Drag the downloaded .jar into the window
- Press space
- Drag your prepared image into cmd window
- Press space
- Drag your output folder into the window
- Done.

# TLDR
- Create 991x743 .png image
- run .jar with path to image as first and output folder as second argument

# Credits
It would have been more complicated without npedotnet's [TGAReader](https://github.com/npedotnet/TGAReader), as Java does not natively support the .tga format SpellForce requires.
