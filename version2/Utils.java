package version2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class Utils {// TODO: generic resultset process function
    public <T> void printArray(ArrayList<T> array, String name) {
        System.out.printf("%s list:\n", name);
        for (T element : array) {
             System.out.printf("%s ",element);
        }
        System.out.println();
    }
    public static  <T>ArrayList<ArrayList<String>> transfor(ArrayList<ArrayList<T>> input){
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        int numRows = input.size();
        int numCols = input.get(0).size();

        for (int j = 0; j < numCols; j++) {
            ArrayList<String> newRow = new ArrayList<>();
            for (int i = 0; i < numRows; i++) {
                newRow.add(input.get(i).get(j).toString());
            }
            result.add(newRow);
        }
        
        return result;
    }

    public <T> void printArray2(ArrayList<ArrayList<T>> array2, String name, boolean[] print, String[] column) {
        System.out.printf("%s lists\n", name);
        int numOfPost = array2.get(0).size();
        String[] outputList = new String[numOfPost];
        int col = 0;
        for (ArrayList<T> array : array2) {
            int row = 0;
                for (T element : array){
                    if (col == 0) outputList[row] = String.format("\npost#%s\n%s: %s ", row+1, column[col], element);
                    else {
                        if (print[col]) outputList[row] += String.format("\n%s: \"%s\" ", column[col], element);
                    }
                    row += 1;
                }
            col += 1;
        }
        for (String post : outputList){
            System.out.println(post);
            System.out.println();
        }
    }

    public String generateRandomPhone(int length) {
        String numberChar = "0123456789";
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(numberChar.charAt(random.nextInt(numberChar.length())));
        }
        return sb.toString();
    }

    public String generateRandomID(int length) {
        String numberChar = "0123456789X";
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(numberChar.charAt(random.nextInt(numberChar.length())));
        }
        return sb.toString();
    }

    public static String generateRandomName() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            char c = characters.charAt(random.nextInt(characters.length()));
            sb.append(c);
        }
        return sb.toString();
    }

    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ';l,.!@#$%^&*(){}[],.<>?/??》";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    public static String generateRandomCity() {
        String[] cities = { "New York", "London", "Tokyo", "Paris", "Berlin", "Sydney", "Toronto", "Dubai" ,
            "北京", "上海", "广州", "深圳", "杭州", "南京", "重庆", "成都", "武汉", "西安",
            "天津", "苏州", "济南", "青岛", "大连", "沈阳", "长春", "哈尔滨", "长沙", "南昌",
            "福州", "厦门", "南宁", "珠海", "佛山", "东莞", "海口", "三亚", "兰州", "银川",
            "太原", "西宁", "贵阳", "昆明", "拉萨", "乌鲁木齐", "香港", "澳门", "台北", "高雄",
            "桂林", "黄山", "张家界", "九寨沟", "凤凰古城", "西塘", "苏州园林", "乌镇", "周庄", "壶口瀑布",
            "敦煌", "莫高窟", "泰山", "庐山", "衡山", "黄山", "峨眉山", "武当山", "五台山", "恒山",
            "青岛", "大连", "天津", "烟台", "威海", "潍坊", "济宁", "泰安", "临沂", "滨州",
            "菏泽", "日照", "德州", "聊城", "东营", "枣庄", "淄博", "莱芜", "赣州", "上饶",
            "景德镇", "萍乡", "九江", "新余", "鹰潭", "宜春", "吉安", "抚州", "南通", "常州",
            "无锡", "扬州", "镇江", "泰州", "盐城", "连云港", "徐州", "淮安", "宿迁", "湖州"};
        Random random = new Random();
        int index = random.nextInt(cities.length);
        return cities[index];
    }

    public static String[] generateRandomForumPostTypes(int numTypes) {
        String[] types = new String[numTypes];
        String[] postTypes = { "问题讨论", "技术分享", "新闻资讯", "经验分享", "资源推荐", "学术研究", "行业动态", "活动公告" };
        Random random = new Random();
        for (int i = 0; i < numTypes; i++) {
            types[i] = postTypes[i];
        }
        return types;
    }

    public String getRandomTime(String startDate, String endDate) {
        //时间的格式
        String dateTime = null;
        long result = 0;
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        //转化
        java.util.Date start = null;
        try {
            start = format1.parse(startDate);
            Date end = format1.parse(endDate);
            //获取建立时间
            result = start.getTime() + (long) (Math.random() * (end.getTime() - start.getTime()));
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateTime = format2.format(result);
            return dateTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getWord(Scanner scanner){
        String action = "";
        while (action == ""){
            action = scanner.next();
            if (action == "") scanner.nextLine();
        }
        scanner.nextLine();
        return action;
    }
    public String getLine(Scanner scanner){
        String action = "";
        while (action == ""){
            action = scanner.nextLine();
        }
        return action;
    }
}
