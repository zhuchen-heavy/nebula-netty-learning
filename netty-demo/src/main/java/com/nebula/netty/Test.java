package com.nebula.netty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Test {

    public static void main(String[] args) {
//        ArrayList<Integer> list = new ArrayList<Integer>();
//        list.add(2);
//        Iterator<Integer> iterator = list.iterator();
//        while (iterator.hasNext()) {
//            Integer integer = iterator.next();
//            if (integer == 2) {
//                list.remove(integer);
//            }
//        }

        List<String> list = new ArrayList<String>();
        list.add("A");
        list.add("B");

        Iterator<String> iter = list.iterator();
        while(iter.hasNext()){
            String str = iter.next();
            if( str.equals("B") )
            {
                iter.remove();
            }
        }
        System.out.println(list);
    }

}
