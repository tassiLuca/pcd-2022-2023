package pcd.lab06.executors.matmul;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatMulConcurLib {

    private static MatMulConcurLib instance;

    public static MatMulConcurLib getInstance() {
        synchronized (MatMulConcurLib.class) {
            if (instance == null) {
                instance = new MatMulConcurLib();
            }
            return instance;
        }
    }

    private MatMulConcurLib() { }

    public Matrix multiply(Matrix matrixA, Matrix matrixB) throws MatMulException {
        Matrix result = new Matrix(matrixA.getNRows(), matrixB.getNColumns());
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        try {
            for (int i = 0; i < matrixA.getNRows(); i++) {
                for (int j = 0; j < matrixB.getNColumns(); j++) {
                    executor.execute(new ComputeElemTask(i, j, matrixA, matrixB, result));
                    // Alternative: using a lambda expression to specify the task
					/*
					executor.execute(() -> {
						double sum = 0;
						for (int k = 0; k < matA.getNColumns(); k++){
							sum += matA.get(i, k)*matB.get(k, j);
						}
						matC.set(i,j,sum);
					});
					*/
                }
            }
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            return result;
        } catch (Exception ex){
            throw new MatMulException();
        }
    }
}
