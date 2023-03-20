import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.*;
import javax.swing.JColorChooser;
import javax.imageio.ImageIO;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DrawMaven extends JFrame {
    private JPanel canvas;
    private Color color = Color.BLACK;
    private int state = States.PEN; // 1 for pen, 2 for eraser, 3 for paintbucket
    private int brushSize = 2;
    private int startX;
    private int startY;
    private int xOffset = 0;
    private int yOffset = 0;
    private int translateX;
    private int translateY;
    private double zoom = 1.0;
    private BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);;
    private List<BufferedImage> undoList = new ArrayList<>();
    private List<BufferedImage> redoList = new ArrayList<>();
    private final JSlider zoomSlider;
    private final String[][] SELECTIONCOLORS = {
        {"red", "#FF0000"}, 
        {"orange", "#FFA500"}, 
        {"yellow", "#FFFF00"}, 
        {"green", "#00FF00"}, 
        {"blue", "#0000FF"}, 
        {"purple", "#800080"}
    };

    public DrawMaven() {
        super("DrawMaven");
        setSize(800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
        }
        
        canvas = new JPanel() {;
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (img != null) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2;
                    translateX = centerX - (int) (img.getWidth() * zoom) / 2;
                    translateY = centerY - (int) (img.getHeight() * zoom) / 2;
                    g2.translate(translateX, translateY);
                    g2.scale(zoom, zoom);
                    g2.drawImage(img, xOffset, yOffset, null);
                }
            }
        };
        canvas.setSize(800, 600);
        add(canvas, BorderLayout.CENTER);
        
        clear();

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                redoList.clear();
                undoList.add(new BufferedImage(img.getWidth(), img.getHeight(), img.getType()));
                Graphics gClone = undoList.get(undoList.size() - 1).getGraphics();
                gClone.drawImage(img, 0, 0, null);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                if (img == null) {
                    img = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
                }       
                int x = (int) ((e.getX()-translateX) / zoom)-xOffset;
                int y = (int) ((e.getY()-translateY) / zoom)-yOffset;
                Graphics2D g = (Graphics2D) img.createGraphics();
                switch (state) {
                    case States.PAINTBUCKET -> {
                        paintBucket(x, y, color);
                        undoList.add(img);
                    }
                    case States.EYEDROPTOOL -> {
                        g.setColor(color = new Color(img.getRGB(x, y)));
                        state = States.PEN;
                    }
                    default ->  {
                        switch (state) {
                            case States.PEN -> g.setColor(color);
                            case States.PENCIL -> g.setColor(color);
                            case States.ERASER -> g.setColor(Color.WHITE);
                            case States.BRUSH -> g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 10));
                            default -> {
                            }
                        }       
                        g.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
                        if (state == States.PENCIL) {
                            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                        }
                        g.drawLine(x, y, x, y);
                        startX = x;
                        startY = y;
                        g.dispose();
                        canvas.repaint();
                    }
                }
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (state != States.PAINTBUCKET && state != States.EYEDROPTOOL) {
                    if (img == null) {
                        img = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    }
                    int x = (int) ((e.getX()-translateX) / zoom)-xOffset;
                    int y = (int) ((e.getY()-translateY) / zoom)-yOffset;

                    Graphics2D g = (Graphics2D) img.createGraphics();
                    switch (state) {
                        case States.PEN -> g.setColor(color);
                        case States.PENCIL -> g.setColor(color);
                        case States.ERASER -> g.setColor(Color.WHITE);
                        case States.BRUSH -> g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 10));
                        default -> {
                        }
                    }
                    g.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    if (state == States.PENCIL) {
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                    }
                    g.drawLine(startX, startY, x, y);
                    startX = x;
                    startY = y;
                    g.dispose();
                    canvas.repaint();
                }
            }
        });
        
        JPanel topBar = new JPanel(new FlowLayout());
        JScrollPane topScrollPane = new JScrollPane(topBar);
        add(topScrollPane, BorderLayout.NORTH);
        //add(topBar, BorderLayout.NORTH);
        
        JPanel bottomBar = new JPanel(new FlowLayout());
        add(bottomBar, BorderLayout.SOUTH);
        
        JPanel rightBar = new JPanel(new FlowLayout());
        add(rightBar, BorderLayout.EAST);
        
        JPanel stateBar = new JPanel(new FlowLayout());
        stateBar.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        topBar.add(stateBar);
        
        JPanel colorBar = new JPanel(new FlowLayout());
        colorBar.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        topBar.add(colorBar);
        
        JPanel toolBar = new JPanel(new FlowLayout());
        toolBar.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        topBar.add(toolBar);

        // add buttons for shapes
        JButton penButton = new JButton();
        setIcon(penButton, "pen.png");
        penButton.setToolTipText("Pen");
        penButton.addActionListener((ActionEvent e) -> {
            state = States.PEN;
        });
        stateBar.add(penButton);
        
        JButton pencilButton = new JButton();
        setIcon(pencilButton, "pencil.png");
        pencilButton.setToolTipText("Pencil");
        pencilButton.addActionListener((ActionEvent e) -> {
            state = States.PENCIL;
        });
        stateBar.add(pencilButton);
        
        JButton brushButton = new JButton();
        setIcon(brushButton, "paintbrush.png");
        brushButton.setToolTipText("Paint Brush");
        brushButton.addActionListener((ActionEvent e) -> {
            state = States.BRUSH;
        });
        stateBar.add(brushButton);

        JButton paintBucketButton = new JButton();
        setIcon(paintBucketButton, "paintbucket.png");
        paintBucketButton.setToolTipText("Paint Bucket");
        paintBucketButton.addActionListener((ActionEvent e) -> {
            state = States.PAINTBUCKET;
        });
        stateBar.add(paintBucketButton);
        
        JButton eyeDropToolButton = new JButton();
        setIcon(eyeDropToolButton, "eyedroptool.png");
        eyeDropToolButton.setToolTipText("Eye Drop Tool");
        eyeDropToolButton.addActionListener((ActionEvent e) -> {
            state = States.EYEDROPTOOL;
        });
        stateBar.add(eyeDropToolButton);
        
        JButton eraserButton = new JButton();
        setIcon(eraserButton, "eraser.png");
        eraserButton.setToolTipText("Eraser");
        eraserButton.addActionListener((ActionEvent e) -> {
            state = States.ERASER;
        });
        stateBar.add(eraserButton);
        
        
        for (String[] selectionColor : SELECTIONCOLORS) {
            JButton button = new JButton();
            setIcon(button, selectionColor[0]+".png");
            button.setToolTipText(selectionColor[0]);
            button.addActionListener((ActionEvent e) -> {
                color = Color.decode(selectionColor[1]);
            });
            colorBar.add(button);
        }
             
        JButton colorButton = new JButton("Color");
        setIcon(colorButton, "color.png");
        colorButton.setToolTipText("Color");
        colorButton.addActionListener((ActionEvent e) -> {
            Color oldColor = color;
            color = JColorChooser.showDialog(DrawMaven.this, "Choose a color", color);
            if (color == null) {
                color = oldColor;
            }
        });
        colorBar.add(colorButton);
        
        JLabel brushSizeLabel = new JLabel("2px");
        toolBar.add(brushSizeLabel);
        
        JSlider brushSizeSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, brushSize);
        brushSizeSlider.addChangeListener(e -> {
            brushSize = (Integer) brushSizeSlider.getValue();
            brushSizeLabel.setText(brushSize+"px");
        });
        brushSizeSlider.setToolTipText("Brush Size");
        toolBar.add(brushSizeSlider);
        
        JLabel zoomSliderLabel = new JLabel("100%");
        zoomSliderLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        toolBar.add(zoomSliderLabel);
        
        zoomSlider = new JSlider(JSlider.HORIZONTAL, 1, 1000, 100);
        zoomSlider.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            int zoomLevel = (int) source.getValue();
            zoom = zoomLevel / 100.0;
            zoomSliderLabel.setText(zoomLevel+"%");
            repaint();
        });
        zoomSlider.setToolTipText("Zoom");
        toolBar.add(zoomSlider);
        
        JSlider posXSlider = new JSlider(JSlider.HORIZONTAL, (int) (-img.getWidth()*zoom), (int) (img.getWidth()*zoom), 0);
        posXSlider.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            xOffset = -source.getValue();
            repaint();
        });
        bottomBar.add(posXSlider);
        
        JSlider posYSlider = new JSlider(JSlider.VERTICAL, (int) (-img.getHeight()*zoom), (int) (img.getHeight()*zoom), 0);
        posYSlider.setInverted(true);
        posYSlider.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            int maxHeight = getContentPane().getHeight();
            int imgHeight = img.getHeight();
            yOffset = (int) (((-source.getValue())*Math.abs(maxHeight-imgHeight)*zoom)/1000);
            repaint();
        });
        rightBar.add(posYSlider);
        
        
        if (System.getProperty("os.name").startsWith("Mac")) {
            try {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
            } catch (Exception e) {

            }
        }
        

        // create menu bar
        JMenuBar menuBar = new JMenuBar();
        
        // create "File" menu
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
        
        // create "Edit" menu
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
        
        // add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(imageMenu);
        
        setJMenuBar(menuBar);
        
        flipHorizontalMenuItem.addActionListener((ActionEvent e) -> flipHorizontal());
        
        flipVerticalMenuItem.addActionListener((ActionEvent e) -> flipVertical());
        
        rotate90MenuItem.addActionListener((ActionEvent e) -> rotate90());
        
        newMenuItem.addActionListener((ActionEvent e) -> createNewCanvas());
        
        exitMenuItem.addActionListener((ActionEvent e) -> System.exit(0));
        
        undoMenuItem.addActionListener((ActionEvent e) -> {
            if (!undoList.isEmpty()) {
                redoList.add(new BufferedImage(img.getWidth(), img.getHeight(), img.getType()));
                Graphics g = redoList.get(redoList.size() - 1).getGraphics();
                g.drawImage(img, 0, 0, null);
                img = new BufferedImage(undoList.get(undoList.size() - 1).getWidth(), undoList.get(undoList.size() - 1).getHeight(), undoList.get(undoList.size() - 1).getType());
                g = img.getGraphics();
                g.drawImage(undoList.get(undoList.size() - 1), 0, 0, null);
                undoList.remove(undoList.size() - 1);
                canvas.repaint();
            }
        });

        redoMenuItem.addActionListener((ActionEvent e) -> {
            if (!redoList.isEmpty()) {
                undoList.add(new BufferedImage(img.getWidth(), img.getHeight(), img.getType()));
                Graphics g = undoList.get(undoList.size() - 1).getGraphics();
                g.drawImage(img, 0, 0, null);
                img = new BufferedImage(redoList.get(redoList.size() - 1).getWidth(), redoList.get(redoList.size() - 1).getHeight(), redoList.get(redoList.size() - 1).getType());
                g = img.getGraphics();
                g.drawImage(redoList.get(redoList.size() - 1), 0, 0, null);
                redoList.remove(redoList.size() - 1);
                canvas.repaint();
            }
        });
        
        clearMenuItem.addActionListener((ActionEvent e) -> clear());
        
        saveMenuItem.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG and JPG files", "png", "jpg");
            fileChooser.setFileFilter(filter);
            if (fileChooser.showSaveDialog(DrawMaven.this) == JFileChooser.APPROVE_OPTION) {
                try {
                    ImageIO.write(img, "png", fileChooser.getSelectedFile());
                } catch (IOException ex) {
                    dialogBox(this, "Error");
                    System.out.println("Error");
                }
            }
        });
        
        openMenuItem.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG and JPG Images", "png", "jpg");
            fileChooser.setFileFilter(filter);
            if (fileChooser.showOpenDialog(DrawMaven.this) == JFileChooser.APPROVE_OPTION) {
                try {
                    img = ImageIO.read(fileChooser.getSelectedFile());
                    // Adjust zoom level to fit image in maximum window area
                    rescaleImage();
                    repaint();
                } catch (IOException ex) {
                    dialogBox(this, "Error");
                    System.out.println("Error");
                }
            }
        });


        setVisible(true);
    }
  
    private void paintBucket(int x, int y, Color color) {
        int oldColor = img.getRGB(x, y);
        if (oldColor != color.getRGB()) {
            Queue<Point> q = new LinkedList<>();
            q.add(new Point(x, y));
            while (!q.isEmpty()) {
                Point point = q.poll();
                x = point.x;
                y = point.y;
                if (img.getRGB(x, y) == oldColor) {
                    img.setRGB(x, y, color.getRGB());
                    if (x > 0) {
                        q.add(new Point(x - 1, y));
                    }
                    if (x < img.getWidth() - 1) {
                        q.add(new Point(x + 1, y));
                    }
                    if (y > 0) {
                        q.add(new Point(x, y - 1));
                    }
                    if (y < img.getHeight() - 1) {
                        q.add(new Point(x, y + 1));
                    }
                }
            }
            canvas.repaint();
        }
    }


    
    private void dialogBox(JFrame owner, String text) {
        JDialog dialog = new JDialog(owner, "dialog Box");
        JLabel label = new JLabel(text);
        dialog.add(label);
        dialog.setSize(100, 100);
        dialog.setVisible(true);
    }
    
    private void clear() {
        for (int x=0; x<img.getWidth(); x++) {
            for (int y=0; y<img.getHeight(); y++) {
                img.setRGB(x, y, Color.WHITE.getRGB());
            }
        }
        canvas.repaint();
    }
  
    private void setIcon(Component component, String iconName) {
        ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/images/"+iconName));
        Image image = icon.getImage();
        Image newImage = image.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
        ImageIcon newIcon = new ImageIcon(newImage);
        if (component instanceof JButton button) {
            button.setIcon(newIcon);
            button.setMargin(new Insets(5, 5, 5, 5));
        } else if (component instanceof JMenuItem menuItem) {
            menuItem.setIcon(newIcon);
        }
    }
    
    private void createNewCanvas() {
        // create dialog box
        JDialog dialog = new JDialog(this, "New Canvas", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new GridLayout(4, 2));
        dialog.setSize(200, 130);

        // add width and height labels and text fields to dialog box
        JLabel widthLabel = new JLabel("Width:");
        JTextField widthField = new JTextField();
        JLabel heightLabel = new JLabel("Height:");
        JTextField heightField = new JTextField();
        dialog.add(widthLabel);
        dialog.add(widthField);
        dialog.add(heightLabel);
        dialog.add(heightField);

        // add OK button to dialog box
        JButton okButton = new JButton("OK");
        okButton.addActionListener((ActionEvent e) -> {
            // get the width and height values from the text fields
            int width1 = Integer.parseInt(widthField.getText());
            int height1 = Integer.parseInt(heightField.getText());
            // create a new BufferedImage with the specified width and height
            img = new BufferedImage(width1, height1, BufferedImage.TYPE_INT_ARGB);
            rescaleImage();
            // clear the canvas and dispose of the dialog box
            clear();
            dialog.dispose();
        });
        dialog.add(okButton);

        // add cancel button to dialog box
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((ActionEvent e) -> {
            // dispose of the dialog box
            dialog.dispose();
        });
        dialog.add(cancelButton);

        // show the dialog box
        dialog.setVisible(true);
    }
    
    public void rescaleImage() {
        int maxWidth = (int) (getContentPane().getWidth()*0.75);
        int maxHeight = (int) (getContentPane().getHeight()*0.75);
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        double zoomX = (double) maxWidth / (double) imgWidth;
        double zoomY = (double) maxHeight / (double) imgHeight;
        zoom = Math.min(zoomX, zoomY);
        zoomSlider.setValue((int) (zoom * 100));
    } 
    
    public void flipHorizontal() {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage flippedImage = new BufferedImage(w, h, img.getType());
        Graphics2D g = flippedImage.createGraphics();
        g.drawImage(img, w, 0, -w, h, null);
        g.dispose();
        img = flippedImage;
        repaint();
    }

    public void flipVertical() {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage flippedImage = new BufferedImage(w, h, img.getType());
        Graphics2D g = flippedImage.createGraphics();
        g.drawImage(img, 0, h, w, -h, null);
        g.dispose();
        img = flippedImage;
        repaint();
    }
    
    public void rotate90() {
        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage newImg = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = newImg.createGraphics();
        g2d.rotate(Math.toRadians(90), height / 2, height / 2);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        img = newImg;
        repaint();
    }



    public static void main(String[] args) {
        DrawMaven drawMaven = new DrawMaven();
    }
}