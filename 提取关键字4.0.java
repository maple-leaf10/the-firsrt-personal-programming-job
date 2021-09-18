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
        KeywordsStatistics k = new KeywordsStatistics(keywords, level);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        k.delete(reader);
        k.print(level);
    }
}

class KeywordsStatistics {
    private Map<String, Integer> keymap = new HashMap<String, Integer>();
    private String[] words;
    private int totalNum;
    private int[] caseNum = {0};
    private int if_elseNum;
    private int if_elseif_elseNum;
    boolean hasElseSign ;
    private int level;
    private Stack <Integer> stack = new Stack <Integer>();
    KeywordsStatistics(String[] keywords, int level){
        for (int i = 0; i < keywords.length; i++) {
            keymap.put(keywords[i], 0);
        }
        this.level = level;
        totalNum = 0;
        if_elseNum = 0;
        if_elseif_elseNum = 0;
        hasElseSign = false;
    }
    public void handleWords(String line) throws IOException {
        if (line != null) {
            if (level >= 3) {
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
            words = temp.split("[\\W]+");
            for (int i = 0; i < words.length; i++) {
                if (keymap.containsKey(words[i])) {
                    totalNum++;
                    Integer count = (Integer) keymap.get(words[i]);
                    keymap.put(words[i], ++count);
                    if (level >= 2) {
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
    public void print(int level) {
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
    public void delete(BufferedReader reader) throws IOException {
        char ch;
        String str = "";
        int index;
        boolean hasElseSign = false;
        try {
            while ((str = reader.readLine().trim()) != null) {
                if (0 != str.length()) {
                    str = str.replaceAll("\".*\"","");
                    if(!hasElseSign){//如果没有多行注释
                        for(index = 0;index < str.length();index++){
                            ch = str.charAt(index);
                            if((ch  == '/')){
                                if(str.charAt(index+1) == '/'){//是否有单行注释，如有，截取字符串
                                    str =  str.substring(0,index);
                                    break;
                                } else if (str.charAt(index+1)=='*') {
                                    if(!str.contains("*/") ){//是否有多行注释
                                        hasElseSign = true;
                                        break;
                                    } else {
                                        str =  str.substring(0,index);
                                        break;
                                    }
                                }
                            }
                        }
                        if(hasElseSign) continue;
                    }
                    else{//有多行注释时
                        for(index = 0;index < str.length();index++){
                            ch = str.charAt(index);
                            if((ch  == '*')&&(index<str.length()-1)&&(str.charAt(index+1) == '/')){
                                hasElseSign = false;
                                break;
                            }
                        }
                        continue;
                    }
                    handleWords(str);
                }
            }
        } catch (Exception ioe){
            ioe.printStackTrace();
        }
    }
}
