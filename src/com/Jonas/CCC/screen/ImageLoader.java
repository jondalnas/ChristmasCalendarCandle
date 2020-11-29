package com.Jonas.CCC.screen;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageLoader {
	public static Bitmap load(String file) {
		BufferedImage bi;

		try {
			bi = ImageIO.read(ImageLoader.class.getResource(file));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		Bitmap img = new Bitmap(bi.getWidth(), bi.getHeight());
		
		bi.getRGB(0, 0, img.width, img.height, img.pixels, 0, img.width);
		
		for (int i = 0; i < img.pixels.length; i++) {
			img.pixels[i] = img.pixels[i] & 0xffffff;
		}
		
		return img;
	}
}
