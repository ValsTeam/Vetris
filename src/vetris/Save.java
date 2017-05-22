package vetris;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javafx.scene.paint.Color;

public class Save {

	public static int loadScore() {
		DataInputStream in;
		int score;
		try {
			in = new DataInputStream(new FileInputStream(new File(System.getProperty("user.home") + "/.vetris/score")));
			score = in.readInt();
			in.close();
		} catch (FileNotFoundException e) {
			saveScore(0);
			return 0;
		} catch (IOException e) {
			return 0;
		}
		return score;
	}

	public static Color loadColor() {
		DataInputStream in;
		Color color;
		try {
			in = new DataInputStream(new FileInputStream(new File(System.getProperty("user.home") + "/.vetris/color")));
			color = Color.color(in.readDouble(), in.readDouble(), in.readDouble());
			in.close();
		} catch (FileNotFoundException e) {
			saveColor(Color.rgb(255, 63, 63));
			return Color.rgb(255, 63, 63);
		} catch (IOException e) {
			return Color.rgb(255, 63, 63);
		}
		return color;
	}

	public static void saveScore(int score) {
		DataOutputStream out;
		try {
			File file = new File(System.getProperty("user.home") + "/.vetris");
			file.mkdirs();
			out = new DataOutputStream(
					new FileOutputStream(new File(System.getProperty("user.home") + "/.vetris/score")));
			out.writeInt(score);
			out.close();
		} catch (IOException e) {
			return;
		}
	}

	public static void saveColor(Color color) {
		DataOutputStream out;
		try {
			File file = new File(System.getProperty("user.home") + "/.vetris");
			file.mkdirs();
			out = new DataOutputStream(
					new FileOutputStream(new File(System.getProperty("user.home") + "/.vetris/color")));
			out.writeDouble(color.getRed());
			out.writeDouble(color.getGreen());
			out.writeDouble(color.getBlue());
			out.close();
		} catch (IOException e) {
			return;
		}
	}
}