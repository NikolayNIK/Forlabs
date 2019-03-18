package ru.kollad.forlabs.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import androidx.annotation.Nullable;

/**
 * Created by NikolayNIK on 08.11.2018.
 */
public class SerializableUtil {

	@Nullable
	public static Serializable tryRead(File file) {
		try {
			return read(file);
		} catch (Exception e) {
			Log.e("Forlabs", "Unable to read serializable: " + file, e);
			return null;
		}
	}

	public static Serializable read(File file) throws IOException {
		ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
		try {
			Serializable object = (Serializable) input.readObject();
			input.close();
			return object;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			input.close();
			throw e;
		}
	}

	public static void write(File file, Serializable object) throws IOException {
		ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
		try {
			output.writeObject(object);
		} catch (Exception e) {
			output.close();
			throw e;
		}
		output.close();
	}
}
