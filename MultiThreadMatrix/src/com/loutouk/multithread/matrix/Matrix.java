package com.loutouk.multithread.matrix;

import java.util.concurrent.TimeUnit;

public class Matrix {
    public double[][] dotProductDistributed(double[][] a, double[][] b) throws MatrixFormatException, InterruptedException {
        if(a==null || b==null) return null;
        if(a[0].length != b.length) throw new MatrixFormatException("Inner dimension numbers do not match between matrices.");
        double[][] result = new double[a.length][b[0].length]; // (a,b)*(c,d) = (a,d)

        // create a thread for each cell of the result matrix
        Thread threads [][] = new Thread[result.length][result[0].length];
        for(int line=0 ; line<result.length ; line++) {
            for(int col=0 ; col<result[0].length ; col++) {
                int finalLine = line;
                int finalCol = col;
                threads[line][col] = new Thread(() -> {
                    // multiply each a value from left to right by each b value from top to bottom
                    double tempSum = 0;
                    for(int index=0 ; index<a[0].length ; index++) { // a[0].length == b[0].length
                        tempSum += multiply(a[finalLine][index], b[index][finalCol]);
                    }
                    result[finalLine][finalCol] = tempSum;

                }, "Thread:" + line + "-" + col);
                threads[line][col].start();
            }
        }

        // Wait until all threads have terminated otherwise some cells might not have been calculated
        for(int line=0 ; line<result.length ; line++) {
            for(int col=0 ; col<result[0].length ; col++) {
                threads[line][col].join();
            }
        }

        return result;
    }

    public double[][] dotProductSequential(double[][] a, double[][] b) throws MatrixFormatException{
        if(a==null || b==null) return null;
        if(a[0].length != b.length) throw new MatrixFormatException("Inner dimension numbers do not match between matrices.");
        double[][] result = new double[a.length][b[0].length]; // matrix shape: (a,b)*(c,d) = (a,d)
        // for each lines of matrix a
        for(int line=0 ; line<a.length ; line++) {
            // for each col in matrix b
            for(int col=0 ; col<b[0].length ; col++) {
                // multiply each a value from left to right by each b value from top to bottom
                double tempSum = 0;
                for(int index=0 ; index<a[0].length ; index++) { // a[0].length == b[0].length
                    tempSum += multiply(a[line][index], b[index][col]);
                }
                result[line][col] = tempSum;
            }
        }
        return result;
    }

    private double multiply(double a, double b) {

        // simulates a time consuming procedure by making the thread sleep
        try {
            TimeUnit.NANOSECONDS.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return a * b;
    }

    public class MatrixFormatException extends Exception {
        public MatrixFormatException(String errorMessage) {
            super(errorMessage);
        }
    }

    public String matrixToSting(double[][] m) {
        StringBuilder sb = new StringBuilder();
        for(int i=0 ; i<m.length ; i++) {
            for(int j=0 ; j<m[0].length ; j++) {
                // The value 8 because 1 tab = 8 spaces
                sb.append(String.format("%8s", String.format("%.2f", m[i][j]) + (j < m[0].length-1 ? ", " : "")));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
