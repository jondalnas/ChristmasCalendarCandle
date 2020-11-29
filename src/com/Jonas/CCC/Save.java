package com.Jonas.CCC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.Jonas.CCC.candle.Candle;

public class Save {
	public static final String SAVE_LOCATION = "candle.sve";
	
	public static void save(Candle candle) {
		new Thread(new Runnable() {
			public void run() {
				try {
					FileOutputStream bos = new FileOutputStream(new File(SAVE_LOCATION));
					ObjectOutputStream oos = new ObjectOutputStream(bos);
					
					oos.writeObject(candle);
					
					oos.flush();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, "Save Thread").start();
	}
	
	public static Candle load() {
		Candle candle;
		File saveFile = new File(SAVE_LOCATION);
		
		if (!saveFile.exists()) return null;
		
		try {
			FileInputStream fis = new FileInputStream(saveFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			candle = (Candle) ois.readObject();

			ois.close();
			fis.close();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		candle.load();
		
		return candle;
	}
}
