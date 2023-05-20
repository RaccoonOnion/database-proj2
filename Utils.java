import java.util.ArrayList;
import java.util.Objects;

public class Utils {
    public <T> void printArray(ArrayList<T> array, String name) {
        System.out.printf("%s list:\n", name);
        for (T element : array) {
            System.out.printf("%s ",element);
        }
        System.out.println();
    }

    public <T> void printArray2(ArrayList<ArrayList<T>> array2, String name) {
        System.out.printf("%s lists\n", name);
        String[] column = {"postID","title","content","datetime","city"};
        int numOfPost = array2.get(0).size();
        String[] outputList = new String[numOfPost];
        int col = 0;
        for (ArrayList<T> array : array2) {
            int row = 0;
            for (T element : array){
                if (col == 0) outputList[row] = String.format("post#%s\n%s: %s ", row, column[col], element);
                else outputList[row] += String.format("%s: \"%s\" ", column[col], element);
                row += 1;
            }
            col += 1;
        }
        for (String post : outputList){
            System.out.println(post);
        }
    }

}

