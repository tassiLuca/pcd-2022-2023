package pcd.lab01.step04;

import java.util.Random;

/**
 * Simple class implementing a matrix.
 */
public final class Matrix {

    private final double[][] matrix;

    /**
     * Creates a new matrix of n rows x m columns.
     * @param n the number of rows
     * @param m the number of columns
     */
    public Matrix(final int n, final int m) {
        matrix = new double[n][m];
    }

    public void initRandom(final double factor) {
        final Random rand = new java.util.Random();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = (int) (rand.nextDouble() * factor);
                // mat[i][j] = rand.nextDouble();
            }
        }
    }

    public void set(final int i, final int j, final double v) {
        matrix[i][j] = v;
    }

    public double get(final int i, final int j) {
        return matrix[i][j];
    }

    public int getNRows() {
        return matrix.length;
    }

    public int getNColumns() {
        return matrix[0].length;
    }

    public static Matrix multiply(final Matrix matrixA, final Matrix matrixB) {
        Matrix matrixC = new Matrix(matrixA.getNRows(), matrixB.getNColumns());
        for (int i = 0; i < matrixC.getNRows(); i++) {
            for (int j = 0; j < matrixC.getNColumns(); j++) {
                double sum = 0;
                for (int k = 0; k < matrixB.getNColumns(); k++) {
                    sum += matrixA.get(i, k) * matrixB.get(k, j);
                }
                matrixC.set(i, j, sum);
            }
        }
        return matrixC;
    }

    public void print() {
        for (double[] doubles : matrix) {
            for (double aDouble : doubles) {
                System.out.print(" " + aDouble);
            }
            System.out.println();
        }
    }
}
