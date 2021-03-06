package io.github.joshotake.timelogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UserManager {
	
	private static List<User> users = new ArrayList<User>();
	protected static LoggingType logging = LoggingType.NONE;
	protected static User loggingUser = null;
	protected static boolean editing = false;
	protected static User editingUser = null;
	
	public static void addUser(User u) {
		users.add(u);
		TimeLogger.outListModel.addElement(u);
	}
	
	public static void removeUser(User u) {
		users.remove(u);
		TimeLogger.outListModel.removeElement(u);
		TimeLogger.inListModel.removeElement(u);
	}
	
	public static List<User> getUsers() {
		return users;
	}
	
	public static List<User> getInUsers() {
		List<User> ret = new ArrayList<User>();
		for (User u : users) {
			if (u.logged) {
				ret.add(u);
			}
		}
		return ret;
	}
	
	public static List<User> getOutUsers() {
		List<User> ret = new ArrayList<User>();
		for (User u : users) {
			if (!u.logged) {
				ret.add(u);
			}
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static void deserializeUsers() {
		List<User> users = new ArrayList<User>();
		File usersFile = new File("users.ser");
		if (usersFile.exists()) {
			try {
				FileInputStream fis = new FileInputStream(usersFile);
				ObjectInputStream ois = new ObjectInputStream(fis);
				users = (ArrayList<User>) ois.readObject();
				ois.close();
				fis.close();
			} catch (FileNotFoundException fe) {
				return;
			} catch (IOException ioe) {
				ioe.printStackTrace();
				return;
			} catch (ClassNotFoundException c) {
				System.out.println("Class not found");
				c.printStackTrace();
				return;
			}
			for (User u : users) {
				UserManager.users.add(u);
			}
		}
		File importFile = new File("import.ser");
		if (importFile.exists()) {
			List<timeLogger.User> importUsers;
			try {
				FileInputStream fis = new FileInputStream("import.ser");
				ObjectInputStream ois = new ObjectInputStream(fis);
				importUsers = (ArrayList<timeLogger.User>) ois.readObject();
				ois.close();
				fis.close();
			} catch (FileNotFoundException fe) {
				return;
			} catch (IOException ioe) {
				ioe.printStackTrace();
				return;
			} catch (ClassNotFoundException c) {
				System.out.println("Class not found");
				c.printStackTrace();
				return;
			}
			for (timeLogger.User u : importUsers) {
				User newUser = new User(u.getName(), u.getPassword());
				newUser.setTimeTotal(u.getTimeTotal());
				newUser.setTotalMeetings(u.getTotalMeetings());
				UserManager.users.add(newUser);
			}
		}
		sort();
	}
	
	public static void sort() {
		if (users.size() > 0) {
			Collections.sort(users, new Comparator<User>() {
				@Override
				public int compare(final User object1, final User object2) {
					return object1.getName().toLowerCase().compareTo(object2.getName().toLowerCase());
				}
			});
		}
	}

	public static void serializeUsers() {
		try {
			FileOutputStream fos = new FileOutputStream("users.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(users);
			oos.close();
			fos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		File importFile = new File("import.ser");
		if (importFile.exists()) {
			importFile.renameTo(new File("imported.ser"));
		}
	}
	
	enum LoggingType {
		IN,
		OUT,
		NONE;
	}

}
