package matrix;

public class Matrix {

    private int rows;
    private int columns;
    private double[][] values;

    public Matrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.values = new double[rows][columns];
    }

    public Matrix(int rows, int columns, double[][] values) {
        this.rows = rows;
        this.columns = columns;
        this.values = values;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public double[][] getValues() {
        return values;
    }

    public void setValue(int row, int col, double value) {
        this.values[row][col] = value;
    }
}