package com.impress.Infection.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

/**
 * File-related tools
 * @author 1mpre55
 */
public class FileTools {
	/**
	 * Whether or not to overwrite a file if one already exists.
	 */
	public static boolean overwrite = true;
	
	/**
	 * Saves a serializable object to a file.
	 * @param object - an object to be saved. Must implement {@link Serializable}.
	 * @param file - file to save the object to.
	 * @return true if the file was successfully saved, false otherwise.
	 * @throws NotSerializableException if the object is not serializable.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static boolean saveObject(Object object, File file) throws IOException {
		if (file.isFile())
			if (overwrite)
				file.delete();
			else
				return false;
		
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(object);
		oos.flush();
		oos.close();
		return true;
	}
	/**
	 * Loads a serializable object from file.
	 * @param file - file to load the object from.
	 * @param type - type of the object. May be null.
	 * @return the loaded object
	 * @throws IOException if an I/O error has occurred.
	 * @throws ClassNotFoundException Class of a serialized object cannot be found.
	 */
	public static Object loadObject(File file, Class<?> type) throws IOException, ClassNotFoundException {
		if (!file.isFile()) return null;
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		Object result = ois.readObject();
		ois.close();
		if (type == null)
			return result;
		else
			return type.cast(result);
	}
	
	public static boolean saveHashMap(HashMap<?, ?> hashMap, File file) {
		if (file.isFile())
			if (overwrite)
				file.delete();
			else
				return false;
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(hashMap);
			oos.flush();
			oos.close();
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	@SuppressWarnings("unchecked")
	public static <T, K> HashMap<T, K> loadHashMap(File file, HashMap<T, K> type) {
		if (!file.isFile()) return null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			Object result = ois.readObject();
			ois.close();
			return (HashMap<T, K>)result;
		} catch(Exception e) {
			return null;
		}
	}
}