package cub;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

class Frame extends JFrame {
    public enum Color{
        WHITE,
        BLACK,
        GREEN
    }

    private final int width = 1000;
    private final int height = 1000;
    private final BufferedImage image;
    private final JPanel panel;

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

    public static int getColor(Color color){
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

class Map {
    public enum Cell {
        WALL,
        EMPTY,
        VOID
    }
    public static class Player {
        double x;
        double y;
        double angle;

        public Player(){
            x = 0;
            y = 0;
            angle = 0;
        }
        public Player(double x, double y, double angle){
            this.x = x;
            this.y = y;
            this.angle = angle;
        }
        public Player(Player another){
            x = another.x;
            y = another.y;
            angle = another.angle;
        }
    }

    private final ArrayList<ArrayList<Cell>> map;
    private final Player player;

    public Map(){
        map = new ArrayList<>();
        player = new Player();
    }

    public ArrayList<ArrayList<Cell>> getMap(){
        return map;
    }
    public Player getPlayer(){
        return player;
    }

    public static void setCell(Map map, int x, int y, char c) throws Exception {
        if (c == '1') {
            map.getMap().get(y).add(Cell.WALL);
        } else if (c == '0') {
            map.getMap().get(y).add(Cell.EMPTY);
        } else if (c == 'N' || c == 'E' || c == 'S' || c == 'W'){
            double angle;
            if (c == 'N'){
                angle = 0;
            } else if (c == 'E'){
                angle = 90;
            } else if (c == 'S'){
                angle = 180;
            } else {
                angle = 270;
            }
            map.getPlayer().x = x;
            map.getPlayer().y = y;
            map.getPlayer().angle = angle;
            map.getMap().get(y).add(Cell.EMPTY);
        } else if (c == ' ') {
            map.getMap().get(y).add(Cell.VOID);
        } else {
            throw new Exception("Bad character (" + c + ")" + " at position {" + (x + 1) + ", " + (y + 1) + "}");
        }
    }
    public static Map parseMap(String file_path) throws Exception {
        String line;
        Map map = new Map();
        BufferedReader reader = new BufferedReader(new FileReader(file_path));
        while ((line = reader.readLine()) != null){
            map.getMap().add(new ArrayList<>());
            for (int x = 0; x < line.length(); x++) {
                setCell(map, x, map.getMap().size() - 1, line.charAt(x));
            }
        }
        return map;
    }
}

public class Main {
    public static void main(String[] args) {
        if (args.length != 1){
            System.out.println("Wrong arguments");
            System.exit(1);
        }

        try{
            Map map = Map.parseMap(args[0]);
            Frame frame = new Frame();
        } catch (Exception e){
            System.out.print("Error: ");
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
