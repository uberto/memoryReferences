package com.citi.memoryReferences;


import java.lang.ref.PhantomReference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class Main{

    public static void main(String [] args){

        //try with -XX:+UseG1GC

        System.out.println("Start!");

        List<SoftReference<Heavy>> prevSoftRefs = new ArrayList<>();
        List<WeakReference<Heavy>> prevWeakRefs = new ArrayList<>();
        List<PhantomReference<Heavy>> prevPhanRefs = new ArrayList<>();

        printMem();
        System.out.println("Press ^C to break!");
        System.out.println("\n\nUsed mem before    After GC call");

        long start = System.currentTimeMillis();

        int t = 0;
        while (t++ < 100) {


            List<Heavy> lh = allocate();

            for (Heavy h : lh) {

//            prevSoftRefs.add(new SoftReference<>(h));
                prevWeakRefs.add(new WeakReference<>(h));
//                prevPhanRefs.add(new PhantomReference<>(h, null));

            }



            long before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            System.gc();


            long after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            System.out.println(before + "       " + after);

            if (prevPhanRefs.size() > 2000) {
                prevPhanRefs.clear();
                System.gc();

                System.out.println("after phantom clear  " +
                        (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
            }


        }

        System.out.println("Total time " + (System.currentTimeMillis() - start));
    }

    private static void printMem() {
         /* Total number of processors or cores available to the JVM */
        System.out.println("Available processors (cores): " +
                Runtime.getRuntime().availableProcessors());

        /* Total amount of free memory available to the JVM */
        System.out.println("Free memory (bytes): " +
                Runtime.getRuntime().freeMemory());

        /* This will return Long.MAX_VALUE if there is no preset limit */
        long maxMemory = Runtime.getRuntime().maxMemory();
        /* Maximum amount of memory the JVM will attempt to use */
        System.out.println("Maximum memory (bytes): " +
                (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

        /* Total memory currently in use by the JVM */
        System.out.println("Total memory (bytes): " +
                Runtime.getRuntime().totalMemory());

    }

    private static List<Heavy> allocate() {

        List<Heavy> r = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            r.add(new Heavy(i));
        }
        return r;

    }

    public static class Heavy{

        byte[] mega = new byte[1_000_000];

        public Heavy(int number) {
            for (int i = 0; i < mega.length; i++) {
                mega[i] = (byte) (number % 256);
            }
        }
    }
}
