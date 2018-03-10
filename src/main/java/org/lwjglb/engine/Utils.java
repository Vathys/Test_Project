package org.lwjglb.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {
    public static String loadResources(String filename) throws Exception {
	String result;
	try (InputStream in = Class.forName(Utils.class.getName()).getResourceAsStream(filename);
		Scanner scanner = new Scanner(in, "UTF-8")) {
	    result = scanner.useDelimiter("\\A").next();
	}
	return result;
    }

    public static List<String> readAllLines(String filename) throws Exception {
	List<String> list = new ArrayList<>();
	try (BufferedReader br = new BufferedReader(
		new InputStreamReader(Class.forName(Utils.class.getName()).getResourceAsStream(filename)))) {
	    String line;
	    while ((line = br.readLine()) != null) {
		list.add(line);
	    }
	}
	return list;
    }
}
