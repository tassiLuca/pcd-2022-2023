package pcd.lab06.executors.matmul;

/**
 * Simple class implementing a matrix.
 */
public class Matrix {

    private final double[][] mat;

    public Matrix(int n, int m) {
        mat = new double[n][m];
    }

    public void initRandom(double factor) {
        java.util.Random rand = new java.util.Random();
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                mat[i][j] = (int) (rand.nextDouble() * factor);
                // mat[i][j] = rand.nextDouble();
            }
        }
    }

    public void set(int i, int j, double v) {
        mat[i][j] = v;
    }

    public double get(int i, int j) {
        return mat[i][j];
    }

    public int getNRows() {
        return mat.length;
    }

    public int getNColumns() {
        return mat[0].length;
    }

    public static Matrix multiply(Matrix matrixA, Matrix matrixB) {
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
        for (double[] doubles : mat) {
            for (double aDouble : doubles) {
                System.out.print(" " + aDouble);
            }
            System.out.println();
        }
    }
}
