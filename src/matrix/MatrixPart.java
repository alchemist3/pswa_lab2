package matrix;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MatrixPart implements Serializable {

    private int rowIndex;
    private int colIndex;
    private double[] row;
    private double[] col;

    private double result;
    private MatrixPartStatus state;


    public MatrixPart(int rowIndex, int colIndex, double[] row, double[] col) {
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.row = row;
        this.col = col;
        this.state = MatrixPartStatus.InQueue;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    public double[] getRow() {
        return row;
    }

    public double[] getCol() {
        return col;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
        this.state = MatrixPartStatus.Calculated;
    }

    public MatrixPartStatus getState() {
        return state;
    }

    public void setState(MatrixPartStatus state) {
        this.state = state;
    }

    public int getSize() {
        if (row.length == col.length)
            return row.length;

        return 0;
    }


    public static List<MatrixPart> generateFragments(Matrix a, Matrix b) {
        List<MatrixPart> matrixParts = new ArrayList<>();

        for (int i = 0; i < a.getRows(); i++) {
            for (int j = 0; j < b.getColumns(); j++) {
                double[] transpValues = new double[b.getRows()];

                for (int k = 0; k < b.getRows(); k++) {
                    transpValues[k] = b.getValues()[k][j];
                }
                matrixParts.add(new MatrixPart(i, j, a.getValues()[i], transpValues));
            }
        }

        return matrixParts;
    }


    public enum MatrixPartStatus {
        InQueue,
        Sent,
        Calculated
    }




}