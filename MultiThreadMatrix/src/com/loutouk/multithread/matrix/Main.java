package com.loutouk.multithread.matrix;

import java.util.Random;

public class Main {

    public static final int MATRIX_HEIGHT = 15;
    public static final int MATRIX_LENGTH = 15;

    public static void main(String[] args){

        double[][] a =  {
                            {1,2,3},
                            {4,5,6}
                        };
        double[][] b =  {
                            {7,8},
                            {9,10},
                            {11,12}
                        };

        double[][] c = new double[MATRIX_HEIGHT][MATRIX_LENGTH];
        double[][] d = new double[MATRIX_HEIGHT][MATRIX_LENGTH];
        for(int line=0 ; line<MATRIX_HEIGHT ; line++) {
            for(int col=0 ; col<MATRIX_LENGTH ; col++) {
                c[line][col] = new Random().nextDouble();
                d[line][col] = new Random().nextDouble();
            }
        }


        Matrix m = new Matrix();
        double[][] result = null;

        try {
            long startTime = System.currentTimeMillis();
            result = m.dotProductSequential(c,d);
            long stopTime = System.currentTimeMillis();
            System.out.println("Time (millisec) for dot product sequential: " + (stopTime - startTime));
        } catch (Matrix.MatrixFormatException e) {
            e.printStackTrace();
        }

        String resA = m.matrixToSting(result);
        System.out.println(resA);

        try {
            long startTime = System.currentTimeMillis();
            try {
                result = m.dotProductDistributed(c,d);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long stopTime = System.currentTimeMillis();
            System.out.println("Time (millisec) for dot product distributed: " + (stopTime - startTime));
        } catch (Matrix.MatrixFormatException e) {
            e.printStackTrace();
        }

        String resB = m.matrixToSting(result);
        System.out.println(resB);

        assert resA.equals(resB);

    }
}
