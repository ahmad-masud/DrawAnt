import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Menu {
       
    public Menu(Canvas canvas) {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem openMenuItem = new JMenuItem("Open");
        JMenuItem saveMenuItem = new JMenuItem("Save As");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoMenuItem = new JMenuItem("Undo");
        JMenuItem redoMenuItem = new JMenuItem("Redo");
        editMenu.add(undoMenuItem);
        editMenu.add(redoMenuItem);

        JMenu imageMenu = new JMenu("Image");
        JMenuItem clearMenuItem = new JMenuItem("Clear");
        JMenuItem flipHorizontalMenuItem = new JMenuItem("Flip Horizontal");
        JMenuItem flipVerticalMenuItem = new JMenuItem("Flip Vertical");
        JMenuItem rotate90MenuItem = new JMenuItem("Rotate 90° Clockwise");
        imageMenu.add(clearMenuItem);
        imageMenu.add(flipHorizontalMenuItem);
        imageMenu.add(flipVerticalMenuItem);
        imageMenu.add(rotate90MenuItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(imageMenu);
        
        saveMenuItem.addActionListener((ActionEvent e) -> canvas.save());

        openMenuItem.addActionListener((ActionEvent e) -> canvas.open());
        
        exitMenuItem.addActionListener((ActionEvent e) -> System.exit(0));
        
        undoMenuItem.addActionListener((ActionEvent e) -> canvas.undo());

        redoMenuItem.addActionListener((ActionEvent e) -> canvas.redo());
        
        clearMenuItem.addActionListener((ActionEvent e) -> canvas.clear());

        flipHorizontalMenuItem.addActionListener((ActionEvent e) -> canvas.flipHorizontal());

        flipVerticalMenuItem.addActionListener((ActionEvent e) -> canvas.flipVertical());

        rotate90MenuItem.addActionListener((ActionEvent e) -> canvas.rotate90());

        newMenuItem.addActionListener((ActionEvent e) -> canvas.createNewCanvas());
        
        canvas.setJMenuBar(menuBar);
    }
}
