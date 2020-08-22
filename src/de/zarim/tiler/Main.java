package de.zarim.tiler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.npe.tga.TGAWriter;

public class Main {

    private static final int TOTAL_WIDTH = 991;
    private static final int TOTAL_HEIGHT = 743;
    private static final int TILE_WIDTH = 256;
    private static final int TILE_HEIGHT = 256;
    private static final int TILE_X = 4;
    private static final int TILE_Y = 3;
    private static final int ALPHA = 0;// (a<<24) | (r<<16) | (g<<8) | b

    private static final int[][] ID = { { 69, 42, 41 }, { 70, 44, 43 }, { 71, 46, 45 }, { 72, 48, 47 } };

    private BufferedImage image;
    private BufferedImage[][] tiles;

    public static void main(String[] args) {
        new Main(args);
    }

    public Main(String[] args) {
        if (args.length != 2) {
            System.err.println("Provide following arguments: source_path and destination_directory as strings.");
            System.exit(0);
        }
        try {
            loadInput(args[0]);
        } catch (IOException e) {
            System.err.println("First argument must be path to image file.");
        }
        if (image == null) {
            System.err.println("Unexpected error. No image loaded.");
            System.exit(2);
        }
        tiles = new BufferedImage[TILE_X][TILE_Y];
        processImage();
        try {
            saveTiles(args[1]);
        } catch (IOException e) {
            System.err.println("Error during image save.");
            System.exit(3);
        }
        System.out.println("Done");
    }

    private void loadInput(String path) throws IOException {
        image = ImageIO.read(new File(path));
        if (image.getWidth() != TOTAL_WIDTH || image.getHeight() != TOTAL_HEIGHT) {
            System.err.println("Input image must have dimensions " + TOTAL_WIDTH + " by " + TOTAL_HEIGHT);
            System.exit(1);
        }
    }

    private void processImage() {
        for (int i = 0; i < TILE_X; i++) {
            for (int j = 0; j < TILE_Y; j++) {
                tiles[i][j] = new BufferedImage(TILE_WIDTH, TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
                fillTile(i, j);
            }
        }
    }

    private void fillTile(int i, int j) {
        for (int x = 0; x < TILE_WIDTH; x++) {
            for (int y = 0; y < TILE_HEIGHT; y++) {
                if (y < 6 || (y == 6 && j == 0) || x >= 250) {
                    tiles[i][j].setRGB(x, y, ALPHA);
                } else {
                    int imgX = x + i * TILE_WIDTH - i * 9;
                    int imgY = y + j * TILE_HEIGHT - 7 - j * 9;
//                    System.out.println(imgX + " " + imgY);
                    tiles[i][j].setRGB(x, y, image.getRGB(imgX, imgY));
                }
            }
        }
    }

    private void saveTiles(String path) throws IOException {
        File dir = new File(path);
        for (int i = 0; i < TILE_X; i++) {
            for (int j = 0; j < TILE_Y; j++) {
                if (tiles[i][j] == null) {
                    System.err.println("Could not save tile " + ID[i][j] + ". Coordinates: " + i + " " + j);
                    continue;
                }
                int[] pixels = tiles[i][j].getRGB(0, 0, TILE_WIDTH, TILE_HEIGHT, null, 0, TILE_WIDTH);
                byte[] bytes = TGAWriter.write(pixels, TILE_WIDTH, TILE_HEIGHT, TGAWriter.ARGB);
                try (FileOutputStream fos = new FileOutputStream(new File(dir, "ui_mainmenu" + ID[i][j] + "_l8.tga"))) {
                    fos.write(bytes);
                }
            }
        }
    }

}
