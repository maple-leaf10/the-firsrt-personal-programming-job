import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Work_1 {
		public static void main (String[] args) throws IOException {
			String keywords[] = {"auto", "break", "case", "char", "const", "continue", "default", "do",
				"double", "else", "enum", "extern", "float", "for", "goto", "if", "int",
				"long", "register", "return", "short", "signed", "sizeof", "static", "struct",
				"switch", "typedef", "union", "unsigned", "void", "volatile", "while"
			};
			System.out.println("请输入代码文件的路径");
			Scanner scanner = new Scanner(System.in);
			String fileName = scanner.nextLine();
			File file = new File(fileName);
			KeywordsStatistics k = new KeywordsStatistics(keywords);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			k.handleWords(reader);
			k.printNum();
		}
}

class KeywordsStatistics {
		private Map<String, Integer> keymap = new HashMap<String, Integer>();
		private String line;
		private String[] words;
		private int totalNum;
		KeywordsStatistics(String[] keywords) {
			for (int i = 0; i < keywords.length; i++) {
				keymap.put(keywords[i], 0);
			}
			totalNum = 0;
		}
		public void handleWords(BufferedReader reader) throws IOException {
			while ((line = reader.readLine()) != null) {
				String temp = line.replace("\"", "a");
				words = temp.trim().split("[\\W]+");
				for (int i = 0; i < words.length; i++) {
					System.out.println(words[i]);
					if (keymap.containsKey(words[i])) {
						totalNum++;
					}
				}
			}
		}
		public void printNum() {
			System.out.println("total num: " + totalNum);
		}
}
