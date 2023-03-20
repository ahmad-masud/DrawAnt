# DrawMaven
DrawMaven is a simple drawing program designed for ease of use and versatility. With DrawMaven, you can create beautiful drawings with a variety of tools such as pencils, brushes, shapes, and text. You can also adjust the size, color, and opacity of your tools to create the perfect design. DrawMaven has an intuitive user interface that allows for easy navigation and use of its features.

# Features
- Supports drawing lines, shapes, and text
- Has a color picker tool
- Supports saving and opening files in PNG and JPG format
- Has undo and redo functionality
- Allows for zooming in and out on the canvas
- Can export drawings as a png or jpg file

# Stack
- Java
- Swing toolkit

![DrawMaven](https://user-images.githubusercontent.com/96448477/226243021-334245df-296e-4707-9f1f-3ff64dc4715b.gif)

# BUILD OUTPUT DESCRIPTION

When you build an Java application project that has a main class, the IDE
automatically copies all of the JAR
files on the projects classpath to your projects dist/lib folder. The IDE
also adds each of the JAR files to the Class-Path element in the application
JAR files manifest file (MANIFEST.MF).

To run the project from the command line, go to the dist folder and
type the following:

java -jar "DrawMaven.jar" 

To distribute this project, zip up the dist folder (including the lib folder)
and distribute the ZIP file.

Notes:

* If two JAR files on the project classpath have the same name, only the first
JAR file is copied to the lib folder.
* Only JAR files are copied to the lib folder.
If the classpath contains other types of files or folders, these files (folders)
are not copied.
* If a library on the projects classpath also has a Class-Path element
specified in the manifest,the content of the Class-Path element has to be on
the projects runtime path.
* To set a main class in a standard Java project, right-click the project node
in the Projects window and choose Properties. Then click Run and enter the
class name in the Main Class field. Alternatively, you can manually type the
class name in the manifest Main-Class element.

