import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import matrix.Matrix;
import matrix.MatrixPart;


@SuppressWarnings("serial")
public class DistributorAgent extends Agent {

    private List<MatrixPart> matrixParts;
    private List<AID> countingAgents;
    private Matrix resultMatrix;
    private int elementsToCalculate;

    @Override
    protected void setup() {

        Matrix mA = new Matrix(3, 4, new double[][] {{4, 7 ,1, 5}, {2, 8, 3, 12}, {1, 3, 7, 5}});
        Matrix mB = new Matrix(4, 3, new double[][] {{2, 4, 10}, {7, 3, 5}, {9, 1, 8}, {3, 11, 6}});
        resultMatrix = new Matrix(3, 3);

        countingAgents = new ArrayList<>();
        matrixParts = MatrixPart.generateFragments(mA, mB);
        this.setElementsToCalculate(matrixParts.size());

        DFServiceHelper.getInstance().register(this, "matrixDistributor", "distributor");

        addBehaviour(new DistributeBehaviour(this));
    }

    public List<MatrixPart> getMatrixParts() {
        return matrixParts;
    }

    public List<AID> getCountingAgents() {
        return countingAgents;
    }

    public Matrix getResultMatrix() {
        return resultMatrix;
    }

    public int getElementsToCalculate() {
        return elementsToCalculate;
    }

    public void setElementsToCalculate(int num){
        this.elementsToCalculate = num;
    }
}