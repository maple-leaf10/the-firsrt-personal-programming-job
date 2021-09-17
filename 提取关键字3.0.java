import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class Work_1 {
    public static void main (String[] args) throws IOException {
        String keywords[] = {"auto", "break", "case", "char", "const", "continue", "default", "do",
                             "double", "else", "enum", "extern", "float", "for", "goto", "if", "int",
                             "long", "register", "return", "short", "signed", "sizeof", "static", "struct",
                             "switch", "typedef", "union", "unsigned", "void", "volatile", "while"};
        System.out.println("请输入代码文件的路径");
        Scanner scanner = new Scanner(System.in);
        String fileName = scanner.nextLine();
        System.out.println("请输入完成等级");
        int level = scanner.nextInt();
        File file = new File(fileName);
        KeywordsStatistics k = new KeywordsStatistics(keywords);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        k.handleWords(reader, level);
        k.printLevelOne(level);
    }
}

class KeywordsStatistics {
    private Map<String, Integer> keymap = new HashMap<String, Integer>();
    private String line;
    private String[] words;
    private int totalNum;
    private int[] caseNum = {0};
    private int if_elseNum;
    private int if_elseif_elseNum;
    KeywordsStatistics(String[] keywords){
        for (int i = 0; i < keywords.length; i++) {
            keymap.put(keywords[i], 0);
        }
        totalNum = 0;
        if_elseNum = 0;
        if_elseif_elseNum = 0;
    }
    public void handleWords(BufferedReader reader, int level) throws IOException {
        int flag;
        Stack <Integer> stack = new Stack <Integer>();
        while ((line = reader.readLine()) != null) {
            if (level >=3) {
                if (line.contains("else if")) {
                    stack.push(2);
                } else if (line.contains("if")) {
                    stack.push(1);
                } else if (line.contains("else")) {
                    if (stack.peek() == 1) {
                        if_elseNum++;
                    } else {
                        if_elseif_elseNum++;
                    }
                    while (!stack.empty() && stack.pop() != 1) ;
                }
            }
            String temp = line.replace("\"", "a");
            words = temp.trim().split("[\\W]+");
            for (int i = 0; i < words.length; i++) {
                if (keymap.containsKey(words[i])) {
                    totalNum++;
                    Integer count = (Integer) keymap.get(words[i]);
                    keymap.put(words[i], ++count);
                    if (level >= 2){
                        if (words[i].equals("switch")) {
                            int num = count.intValue();
                            if (num > 1) {
                                caseNum[num - 2] = keymap.get("case");
                                keymap.put("case", 0);
                            }
                        }
                    }
                }
            }
        }
    }
    public void printLevelOne(int level) {
        if (level >= 1) {
            System.out.println("total num: "+totalNum);
        }
        if (level >= 2) {
            System.out.println("switch num: "+keymap.get("switch"));
            System.out.print("case num:");
            for (int i = 0; i < keymap.get("switch")-1; i++) {
                System.out.print(" "+caseNum[i]);
            }
            System.out.println(" "+keymap.get("case"));
        }
        if (level >= 3) {
            System.out.println("if-else num: "+if_elseNum);
        }
        if (level == 4) {
            System.out.println("if-elseif-else num: "+if_elseif_elseNum);
        }
    }
}

