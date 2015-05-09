package tk.luoxing123.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class Keyborad {
	public static BufferedReader standard = new BufferedReader(
			new InputStreamReader(System.in));

	public static String readLine() throws IOException {
		return standard.readLine();
	}
}
