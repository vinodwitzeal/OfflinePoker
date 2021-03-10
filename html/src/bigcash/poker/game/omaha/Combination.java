package bigcash.poker.game.omaha;

// Java program to print all combination of size r in an array of size n
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Combination {

    private static void helper(List<ArrayList<Integer>> combinations, Integer[] data, int start, int end, int index,List<Integer> list) {
        if (index == data.length) {
            ArrayList<Integer> arr = new ArrayList<Integer>();
            Collections.addAll(arr, data);
            combinations.add(arr);
        } else if (start <= end) {
            data[index] = list.get(start);
            helper(combinations, data, start + 1, end, index + 1,list);
            helper(combinations, data, start + 1, end, index,list);
        }
    }

    public static List<ArrayList<Integer>> generate(List<Integer> list,int n, int r) {
        List<ArrayList<Integer>> combinations = new ArrayList<ArrayList<Integer>>();
        helper(combinations, new Integer[r], 0, n-1, 0,list);
        return combinations;
    }

}
