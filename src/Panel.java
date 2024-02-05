import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

public class Panel {
    
    private final JSlider zoomSlider;
    private final String[][] SELECTIONCOLORS = {
        {"red", "#FF0000"}, 
        {"orange", "#FFA500"}, 
        {"yellow", "#FFFF00"}, 
        {"green", "#00FF00"}, 
        {"blue", "#0000FF"}, 
        {"purple", "#800080"}
    };
    
    public Panel(Canvas canvas) {
        JPanel topBar = new JPanel(new FlowLayout());
        JScrollPane topScrollPane = new JScrollPane(topBar);
        canvas.add(topScrollPane, BorderLayout.NORTH);

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
            canvas.setState(States.PEN);
        });
        stateBar.add(penButton);

        JButton pencilButton = new JButton();
        setIcon(pencilButton, "pencil.png");
        pencilButton.setToolTipText("Pencil");
        pencilButton.addActionListener((ActionEvent e) -> {
            canvas.setState(States.PENCIL);
        });
        stateBar.add(pencilButton);

        JButton brushButton = new JButton();
        setIcon(brushButton, "paintbrush.png");
        brushButton.setToolTipText("Paint Brush");
        brushButton.addActionListener((ActionEvent e) -> {
            canvas.setState(States.BRUSH);
        });
        stateBar.add(brushButton);

        JButton paintBucketButton = new JButton();
        setIcon(paintBucketButton, "paintbucket.png");
        paintBucketButton.setToolTipText("Paint Bucket");
        paintBucketButton.addActionListener((ActionEvent e) -> {
            canvas.setState(States.PAINTBUCKET);
        });
        stateBar.add(paintBucketButton);

        JButton eyeDropToolButton = new JButton();
        setIcon(eyeDropToolButton, "eyedroptool.png");
        eyeDropToolButton.setToolTipText("Eye Drop Tool");
        eyeDropToolButton.addActionListener((ActionEvent e) -> {
            canvas.setState(States.EYEDROPTOOL);
        });
        stateBar.add(eyeDropToolButton);

        JButton eraserButton = new JButton();
        setIcon(eraserButton, "eraser.png");
        eraserButton.setToolTipText("Eraser");
        eraserButton.addActionListener((ActionEvent e) -> {
            canvas.setState(States.ERASER);
        });
        stateBar.add(eraserButton);


        for (String[] selectionColor : SELECTIONCOLORS) {
            JButton button = new JButton();
            setIcon(button, selectionColor[0]+".png");
            button.setToolTipText(selectionColor[0]);
            button.addActionListener((ActionEvent e) -> {
                canvas.setColor(Color.decode(selectionColor[1]));
            });
            colorBar.add(button);
        }

        JButton colorButton = new JButton("Color");
        setIcon(colorButton, "color.png");
        colorButton.setToolTipText("Color");
        colorButton.addActionListener((ActionEvent e) -> {
            Color color = JColorChooser.showDialog(canvas, "Choose a color", canvas.getColor());
            if (color != null) {
                canvas.setColor(color);
            }
        });
        colorBar.add(colorButton);

        JLabel brushSizeLabel = new JLabel("2px");
        toolBar.add(brushSizeLabel);

        JSlider brushSizeSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 2);
        brushSizeSlider.addChangeListener(e -> {
            int brushSize = (Integer) brushSizeSlider.getValue();
            canvas.setBrushSize(brushSize);
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
            canvas.setZoom(zoomLevel / 100.0);
            zoomSliderLabel.setText(zoomLevel+"%");
            canvas.repaint();
        });
        zoomSlider.setToolTipText("Zoom");
        toolBar.add(zoomSlider);

    }
    
    public void setZoomSliderValue(int value) {
        zoomSlider.setValue(value);
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
}
