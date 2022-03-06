package cub;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class Frame extends JFrame {
    private final int width = 1000;
    private final int height = 1000;
    private final BufferedImage image;
    private final JPanel panel;

    public enum Color{
        WHITE,
        BLACK,
        GREEN
    }

    public Frame(){
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };
        panel.setBounds(0, 0, width, height);

        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setVisible(true);
        add(panel);
    }

    public void setColor(int x, int y, int color){
        image.setRGB(x, y, color);
    }
    public void setColor(int x, int y, Color color){
        setColor(x, y, getColor(color));
    }

    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    public BufferedImage getImage(){
        return image;
    }
    public int getColor(Color color){
        switch (color){
            case BLACK:
                return 0x000000;
            case WHITE:
                return 0xFFFFFF;
            case GREEN:
                return 0x00FF00;
            default:
                return 0x000000;
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Frame frame = new Frame();
    }
}
