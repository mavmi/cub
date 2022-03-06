package cub;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

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
    public Cell getCell(int x, int y){
        return map.get(y).get(x);
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
    private static void setCell(Map map, int x, int y, char c) throws Exception {
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
            map.getPlayer().x = x + 0.5;
            map.getPlayer().y = y + 0.5;
            map.getPlayer().angle = angle;
            map.getMap().get(y).add(Cell.EMPTY);
        } else if (c == ' ') {
            map.getMap().get(y).add(Cell.VOID);
        } else {
            throw new Exception("Bad character (" + c + ")" + " at position {" + (x + 1) + ", " + (y + 1) + "}");
        }
    }
}

class Frame extends JFrame {
    public enum Color{
        WHITE,
        BLACK,
        GREEN

    }
    public static class Vector{
        public double x;
        public double y;

        public Vector(){
            x = 0.0;
            y = 0.0;
        }
        public Vector(Vector another){
            x = another.x;
            y = another.y;
        }
    }

    private double mouse_position;

    private final Vector dir;
    private final Vector plane;

    private final int width = 800;
    private final int height = 800;
    private final double step_delta = 0.31;
    private final double angle_delta = 3.5;
    private final Map map;
    private final BufferedImage image;

    private final JPanel panel;

    public Frame(Map map){
        dir = new Vector();
        plane = new Vector();

        mouse_position = MouseInfo.getPointerInfo().getLocation().getX();
        getContentPane().setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB),
                new Point(0, 0), "blank cursor"));

        if (cmpDoubles(map.getPlayer().angle, 0)){
            dir.x = 0;
            dir.y = -1;
        } else if (cmpDoubles(map.getPlayer().angle, 90)){
            dir.x = 1;
            dir.y = 0;
        } else if (cmpDoubles(map.getPlayer().angle, 180)){
            dir.x = 0;
            dir.y = 1;
        } else {
            dir.x = -1;
            dir.y = 0;
        }
        if (dir.x == 0){
            plane.x = 0.66;
            plane.y = 0;
        } else {
            plane.x = 0;
            plane.y = 0.66;
        }

        this.map = map;
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
        setLocationRelativeTo(null);
        setVisible(true);
        add(panel);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key_code = e.getKeyCode();
                if (key_code == KeyEvent.VK_ESCAPE){
                    System.exit(0);
                } else if (key_code == KeyEvent.VK_RIGHT){
                    rotateVector(dir, angle_delta);
                    rotateVector(plane, angle_delta);
                } else if (key_code == KeyEvent.VK_LEFT){
                    rotateVector(dir, -angle_delta);
                    rotateVector(plane, -angle_delta);
                } else if (key_code == KeyEvent.VK_UP || key_code == KeyEvent.VK_DOWN
                            || key_code == KeyEvent.VK_W || key_code == KeyEvent.VK_S){
                    double delta_x = dir.x * step_delta;
                    double delta_y = dir.y * step_delta;
                    if (key_code == KeyEvent.VK_DOWN || key_code == KeyEvent.VK_S){
                        delta_x *= -1;
                        delta_y *= -1;
                    }
                    if (map.getCell((int)(map.getPlayer().x + delta_x), (int)(map.getPlayer().y + delta_y)) != Map.Cell.EMPTY){
                        return;
                    }
                    map.getPlayer().x += delta_x;
                    map.getPlayer().y += delta_y;
                } else if (key_code == KeyEvent.VK_A || key_code == KeyEvent.VK_D){
                    Vector vector = new Vector(dir);
                    if (key_code == KeyEvent.VK_A){
                        rotateVector(vector, -90);
                    } else {
                        rotateVector(vector, 90);
                    }
                    if (map.getCell((int)(map.getPlayer().x + vector.x * step_delta), (int)(map.getPlayer().y + vector.y * step_delta)) != Map.Cell.EMPTY){
                        return;
                    }
                    map.getPlayer().x += vector.x * step_delta;
                    map.getPlayer().y += vector.y * step_delta;
                }
                draw();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                double move_delta = 5.0;
                double new_position = MouseInfo.getPointerInfo().getLocation().getX();

                double delta = mouse_position - new_position;
                if (delta < 0.0){
                    delta *= -1.0;
                }
                if (delta < move_delta){
                    return;
                }

                if (new_position < mouse_position){
                    rotateVector(dir, -angle_delta);
                    rotateVector(plane, -angle_delta);
                } else if (new_position > mouse_position){
                    rotateVector(dir, angle_delta);
                    rotateVector(plane, angle_delta);
                }
                mouse_position = new_position;
                draw();
            }
        });
    }

    public void draw(){
        for (int x = 0; x < width; x++){
            double camera_x = 2 * x / (double)width - 1;
            double ray_dir_x = dir.x + plane.x * camera_x;
            double ray_dir_y = dir.y + plane.y * camera_x;

            int map_x = (int)map.getPlayer().x;
            int map_y = (int)map.getPlayer().y;

            double side_dist_x;
            double side_dist_y;

            double delta_dist_x = (ray_dir_x == 0) ? 1e30 : Math.abs(1 / ray_dir_x);
            double delta_dist_y = (ray_dir_y == 0) ? 1e30 : Math.abs(1 / ray_dir_y);

            double wall_dist;

            int step_x;
            int step_y;

            int hit = 0;
            int side = 0;
            if (ray_dir_x < 0)
            {
                step_x = -1;
                side_dist_x = (map.getPlayer().x - map_x) * delta_dist_x;
            }
            else
            {
                step_x = 1;
                side_dist_x = (map_x + 1.0 - map.getPlayer().x) * delta_dist_x;
            }
            if(ray_dir_y < 0)
            {
                step_y = -1;
                side_dist_y = (map.getPlayer().y - map_y) * delta_dist_y;
            }
            else
            {
                step_y = 1;
                side_dist_y = (map_y + 1.0 - map.getPlayer().y) * delta_dist_y;
            }
            
            while (hit == 0)
            {
                if(side_dist_x < side_dist_y)
                {
                    side_dist_x += delta_dist_x;
                    map_x += step_x;
                    side = 0;
                }
                else
                {
                    side_dist_y += delta_dist_y;
                    map_y += step_y;
                    side = 1;
                }
                if(map.getCell(map_x, map_y) == Map.Cell.WALL){
                    hit = 1;
                }
            }

            if(side == 0) {
                wall_dist = (side_dist_x - delta_dist_x);
            } else {
                wall_dist = (side_dist_y - delta_dist_y);
            }

            int line_height = (int)(height / wall_dist);
            int draw_start = height / 2 - line_height / 2;
            if (draw_start < 0) {
                draw_start = 0;
            }
            int draw_end = line_height / 2 + height / 2;
            if (draw_end >= height) {
                draw_end = height - 1;
            }

            drawLine(x, draw_start, draw_end);
        }
        repaint();
    }
    private void drawLine(int x, int start, int end){
        int y = 0;
        while (y < start){
            setColor(x, y++, Color.BLACK);
        }
        while (y < end){
            setColor(x, y++, Color.GREEN);
        }
        while (y < height){
            setColor(x, y++, Color.BLACK);
        }
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

    private boolean cmpDoubles(double a, double b){
        double c = a - b;
        if (c < 0){
            c *= -1.0;
        }
        return c < 1e-3;
    }
    private void rotateVector(Vector vector, double angle){
        double x;
        double y;

        x = vector.x * Math.cos(degreeToRad(angle)) - vector.y * Math.sin(degreeToRad(angle));
        y = vector.y * Math.cos(degreeToRad(angle)) + vector.x * Math.sin(degreeToRad(angle));

        vector.x = x;
        vector.y = y;
    }
    private double degreeToRad(double angle){
        return angle / 180.0 * Math.PI;
    }
}

public class Main {
    public static void main(String[] args) {
        if (args.length != 1){
            System.out.println("Wrong arguments");
            System.exit(1);
        }

        try{
            Frame frame = new Frame(Map.parseMap(args[0]));
            frame.draw();
        } catch (Exception e){
            System.out.print("Error: ");
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
