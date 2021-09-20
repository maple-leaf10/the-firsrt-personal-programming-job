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
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入代码文件的路径");
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
    private Map<String, Integer> keymap = new HashMap<String, Integer>();//存放关键字及其对应数量
    private String[] words;//存放每行分割出的单词
    private int totalNum;//存放总的关键字数量
    private int[] caseNum = new int[1000];//存放每个 switch 对应的 case 数量
    private int if_elseNum;//存放 if else 结构的数量
    private int if_elseif_elseNum;//存放 if, else if, else 结构的数量
    boolean hasElseSign ;//判断是否有多行注释
    private int level;//存放要求输出的等级
    private Stack<Integer> stack = new Stack<Integer>();//用于等级 3，4 的计算

    KeywordsStatistics(String[] keywords, int level) {//构造函数
        for (String keyword : keywords) {
            keymap.put(keyword, 0);
        }
        this.level = level;
        totalNum = 0;
        if_elseNum = 0;
        if_elseif_elseNum = 0;
        hasElseSign = false;
    }

    public void handleWords(String line) throws IOException {//关键字统计函数，包含处理四种不同要求
        if (line != null) {
            if (level >= 3) {//用类似括号匹配的方法统计 if else 结构和 if, else if, else 结构数量
                if (line.contains("else if")) {
                    stack.push(2);
                } else if (line.contains("if")) {
                    stack.push(1);
                } else if (line.contains("else")) {//只有出现 else 才能进行匹配
                    if (stack.peek() == 1) {
                        if_elseNum ++;
                    } else {
                        if_elseif_elseNum ++;
                    }
                    while (!stack.empty() && stack.pop() != 1) ;
                }
            }

            words = line.split("[\\W]+");//根据非单词字符进行分割
            for (int i = 0; i < words.length; i ++) {
                if (keymap.containsKey(words[i])) {//keymap 中保存着全部关键字，与分割出的单词进行匹配
                    totalNum ++;
                    Integer count = (Integer) keymap.get(words[i]);
                    keymap.put(words[i], ++ count);//某个关键字匹配成功，其对应的数量加一

                    if (level >= 2) {//统计 switch case 结构，这个会出现少放入一次 case 数量进数组的情况，所以在后面的输出函数要进行调整
                        if (words[i].equals("switch")) {
                            int num = count;
                            if (num > 1) {//当出现非第一个的 switch 时将 case 的数量从 keymap 中取出放入数组中并置 0
                                caseNum[num - 2] = keymap.get("case");
                                keymap.put("case", 0);
                            }
                        }
                    }
                }
            }
        }
    }

    public void print(int level) {//输出函数，由等级低到高按要求逐步输出
        if (level >= 1) {//等级一只要求输出关键字数量
            System.out.println("total num: " + totalNum);
        }
        if (level >= 2) {//等级二还要求输出 switch case 结构数量
            System.out.println("switch num: " + keymap.get("switch"));
            if (keymap.get("switch") >= 1) {//由于前面的统计函数有瑕疵，当 switch 数量不为零时，将 case 当前的数量放入数组
                caseNum[keymap.get("switch") - 1] = keymap.get("case");
            }
            System.out.print("case num:");
            for (int i = 0; i < keymap.get("switch"); i ++) {
                System.out.print(" " + caseNum[i]);
            }
            if (keymap.get("switch") == 0) {//当 switch 数量为零时，直接输出零
                System.out.print(" 0");
            }
            System.out.println();
        }
        if (level >= 3) {//等级三还要求输出 if else 结构数量
            System.out.println("if-else num: " + if_elseNum);
        }
        if (level == 4) {//等级四还要求输出 if, else if, else 结构数量
            System.out.println("if-elseif-else num: " + if_elseif_elseNum);
        }
    }

    public void delete(BufferedReader reader) throws IOException {//文件删除函数，删除文件中的注释和字符串
        char ch;
        String str = "";
        int index;
        boolean hasElseSign = false;

        try {
            while ((str = reader.readLine()) != null) {
                str = str.trim();
                if (0 != str.length()) {
                    str = str.replaceAll("\".*\"","");//删除字符串
                    if (! hasElseSign) {//删除注释，如果没有多行注释
                        for (index = 0; index < str.length(); index ++) {
                            ch = str.charAt(index);
                            if ((ch == '/')) {
                                if (str.charAt(index + 1) == '/') {//是否有单行注释，如有，截取字符串
                                    str = str.substring(0, index);
                                    break;
                                } else if (str.charAt(index + 1) == '*') {
                                    if (! str.contains("*/")) {//是否有多行注释，如有，则标志
                                        str = str.substring(0, index);
                                        hasElseSign = true;
                                        handleWords(str);
                                        break;
                                    } else {//如果没有多行注释，则截取字符串
                                        str = str.substring(0, index);
                                        break;
                                    }
                                }
                            }
                        }
                        if (hasElseSign) continue;
                    } else {//有多行注释时
                        for (index = 0; index < str.length(); index ++) {
                            ch = str.charAt(index);
                            if ((ch == '*') && (index < str.length() - 1) && (str.charAt(index + 1) == '/')) {//找到多行注释结尾
                                if (index + 1 < str.length() - 1) {
                                    str = str.substring(index + 2, str.length());
                                    handleWords(str);
                                }
                                hasElseSign = false;
                                break;
                            }
                        }
                        continue;
                    }
                    handleWords(str);
                }
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }
}
