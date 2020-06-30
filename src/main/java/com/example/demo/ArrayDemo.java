package com.example.demo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author：lanjy
 * @date：2020/6/24
 * @description：
 */
public class ArrayDemo {
    public static void main(String[] args) {
        String[] m = { "1","2","3","4"};
        String[] n = { "2","3","5","6"};
        String[] b = getJ(m, n);
        for (int i = 0; i < b.length; i++) {
            System.out.println(b[i]);
        }
    }

    /**
     * 求数组的交集
     * @param m
     * @param n
     * @return
     */
    private static String[] getJ(String[] m, String[] n){
        HashSet<String> set = new HashSet<>();
        List<String> mList = Arrays.asList(m);
        for (int i = 0; i < n.length; i++) {
            if (mList.contains(n[i])){
                set.add(n[i]);
            }
        }
        String[] arr = {};
        return set.toArray(arr);
    }

    /**
     * 求数组的并集
     * @param m
     * @param n
     * @return
     */
    private static String[] getB(String[] m, String[] n){
        // 将数组转换为set集合
        Set<String> set1 = new HashSet<String>(Arrays.asList(m));
        Set<String> set2 = new HashSet<String>(Arrays.asList(n));
        set1.addAll(set2);
        String[] arr = {};
        return  set1.toArray(arr);
    }
}
