package de.zarim.tiler;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.npe.tga.TGAWriter;

public class Main {

    private static final int TOTAL_WIDTH = 1026;
    private static final int TOTAL_HEIGHT = 770;
    private static final int TILE_WIDTH = 256;
    private static final int TILE_HEIGHT = 256;
    private static final int TILE_X = 4;
    private static final int TILE_Y = 3;
    private static final int ALPHA = 0; // (a<<24) | (r<<16) | (g<<8) | b

    private static final int[][] ID = { { 69, 42, 41 }, { 70, 44, 43 }, { 71, 46, 45 }, { 72, 48, 47 } };

    // input image
    private BufferedImage image;
    // basic tiles (41 to 48 and 69 to 72)
    private BufferedImage[][] tiles;
    // tiles 74, 75, 78 need special treatment
    private BufferedImage[] edgeTiles;

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
        edgeTiles = new BufferedImage[3];
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
        edgeTiles[0] = new BufferedImage(TILE_WIDTH, TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        fillTile74();
        edgeTiles[1] = new BufferedImage(TILE_WIDTH, TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        fillTile75();
        edgeTiles[2] = new BufferedImage(TILE_WIDTH, TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        fillTile78();
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

    private void fillTile74() {
        for (int x = 0; x < TILE_WIDTH; x++) {
            for (int y = 0; y < TILE_HEIGHT; y++) {
                if (y < 6 || x < 152 || x > 227) {
                    edgeTiles[0].setRGB(x, y, ALPHA);
                } else if (x < 190) {
                    int imgX = 988 + x - 152;
                    int imgY = 246 + y - 6;
                    edgeTiles[0].setRGB(x + 38, y, image.getRGB(imgX, imgY));
                } else {
                    int imgX = 988 + x - 190;
                    int imgY = 493 + y - 6;
                    edgeTiles[0].setRGB(x - 38, y, image.getRGB(imgX, imgY));
                }
            }
        }
    }

    private void fillTile75() {
        for (int x = 0; x < TILE_WIDTH; x++) {
            for (int y = 0; y < TILE_HEIGHT; y++) {
                if (y < 7 || x < 190 || x > 227) {
                    edgeTiles[1].setRGB(x, y, ALPHA);
                } else {
                    int imgX = 988 + x - 190;
                    int imgY = 0 + y - 7;
                    edgeTiles[1].setRGB(x, y, image.getRGB(imgX, imgY));
                }
            }
        }
    }

    private void fillTile78() {
        for (int x = 0; x < TILE_WIDTH; x++) {
            for (int y = 0; y < TILE_HEIGHT; y++) {
                if (y < 106 || x > 249 || (x > 227 && y < 136) || (x < 190 && y < 136)) {
                    edgeTiles[2].setRGB(x, y, ALPHA);
                } else if (y < 136) {
                    int imgX = 988 + x - 190;
                    int imgY = 740 + y - 106;
                    edgeTiles[2].setRGB(x, y, image.getRGB(imgX, imgY));
                } else if (y < 166) {
                    int imgX = 741 + x - 0;
                    int imgY = 740 + y - 136;
                    edgeTiles[2].setRGB(x, y, image.getRGB(imgX, imgY));
                } else if (y < 196) {
                    int imgX = 494 + x - 0;
                    int imgY = 740 + y - 166;
                    edgeTiles[2].setRGB(x, y, image.getRGB(imgX, imgY));
                } else if (y < 226) {
                    int imgX = 247 + x - 0;
                    int imgY = 740 + y - 196;
                    edgeTiles[2].setRGB(x, y, image.getRGB(imgX, imgY));
                } else {
                    int imgX = 0 + x - 0;
                    int imgY = 740 + y - 226;
                    edgeTiles[2].setRGB(x, y, image.getRGB(imgX, imgY));
                }
            }
        }
    }

    private void saveTiles(String path) throws IOException {
        File dir = new File(path);
        int[] pixels;
        byte[] bytes;
        // normal tiles
        for (int i = 0; i < TILE_X; i++) {
            for (int j = 0; j < TILE_Y; j++) {
                if (tiles[i][j] == null) {
                    System.err.println("Could not save tile " + ID[i][j] + ". Coordinates: " + i + " " + j);
                    continue;
                }
                pixels = tiles[i][j].getRGB(0, 0, TILE_WIDTH, TILE_HEIGHT, null, 0, TILE_WIDTH);
                bytes = TGAWriter.write(pixels, TILE_WIDTH, TILE_HEIGHT, TGAWriter.ARGB);
                try (FileOutputStream fos = new FileOutputStream(new File(dir, "ui_mainmenu" + ID[i][j] + "_l8.tga"))) {
                    fos.write(bytes);
                }
            }
        }
        // edge tiles (74, 75, 78)
        pixels = edgeTiles[0].getRGB(0, 0, TILE_WIDTH, TILE_HEIGHT, null, 0, TILE_WIDTH);
        bytes = TGAWriter.write(pixels, TILE_WIDTH, TILE_HEIGHT, TGAWriter.ARGB);
        try (FileOutputStream fos = new FileOutputStream(new File(dir, "ui_mainmenu" + 74 + "_l8.tga"))) {
            fos.write(bytes);
        }
        pixels = edgeTiles[1].getRGB(0, 0, TILE_WIDTH, TILE_HEIGHT, null, 0, TILE_WIDTH);
        bytes = TGAWriter.write(pixels, TILE_WIDTH, TILE_HEIGHT, TGAWriter.ARGB);
        try (FileOutputStream fos = new FileOutputStream(new File(dir, "ui_mainmenu" + 75 + "_l8.tga"))) {
            fos.write(bytes);
        }
        pixels = edgeTiles[2].getRGB(0, 0, TILE_WIDTH, TILE_HEIGHT, null, 0, TILE_WIDTH);
        bytes = TGAWriter.write(pixels, TILE_WIDTH, TILE_HEIGHT, TGAWriter.ARGB);
        try (FileOutputStream fos = new FileOutputStream(new File(dir, "ui_mainmenu" + 78 + "_l8.tga"))) {
            fos.write(bytes);
        }
    }

}
