import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import matrix.Matrix;
import matrix.MatrixPart;


@SuppressWarnings("serial")
public class DistributeBehaviour extends CyclicBehaviour {

    private DistributorAgent agent;
    private List<CountingAgent.CountingAgentInfo> countingAgents;



    public DistributeBehaviour(DistributorAgent agent) {
        super();

        this.agent = agent;
    }

    @Override
    public void action() {
        refreshCountingAgents();
        sendMatrix();
        receiveResult();
    }

    private void refreshCountingAgents() {
        List<CountingAgent.CountingAgentInfo> newCountingAgents = DFServiceHelper.getInstance().findCountingAgents(agent, "calculator");
        List<CountingAgent.CountingAgentInfo> tempCountingAgents = new ArrayList<>();

        for (CountingAgent.CountingAgentInfo newCountingAgent : newCountingAgents) {
            boolean exists = false;
            if (countingAgents != null) {
                for (CountingAgent.CountingAgentInfo countingAgent : countingAgents) {
                    if (newCountingAgent.getAgentId().equals(countingAgent.getAgentId())) {
                        tempCountingAgents.add(countingAgent);
                        exists = true;
                        break;
                    }
                }
            }

            if (!exists) {
                tempCountingAgents.add(newCountingAgent);
            }
        }
        countingAgents = tempCountingAgents;

        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage msg = agent.receive(mt);
        if (msg != null) {
            for (CountingAgent.CountingAgentInfo countingAgent : countingAgents) {
                if (countingAgent.getAgentId().equals(msg.getSender())) {
                    countingAgent.setAgentBusy(false);
                }
            }
        }
    }

    private void sendMatrix() {
        for (CountingAgent.CountingAgentInfo countingAgent : countingAgents) {
            if (!countingAgent.getAgentBusy()) {
                ACLMessage msg = new ACLMessage(ACLMessage.CFP);
                msg.addReceiver(countingAgent.getAgentId());

                try {
                    for (MatrixPart mf : agent.getMatrixParts()) {
                        if (MatrixPart.MatrixPartStatus.InQueue.equals(mf.getState())) {
                            mf.setState(MatrixPart.MatrixPartStatus.Sent);
                            msg.setContentObject(mf);
                            msg.setReplyWith(countingAgent.getAgentId().getLocalName());
                            countingAgent.setAgentBusy(true);

                            System.out.println(agent.getLocalName() + ": send part (" + mf.getRowIndex() + ";" + mf.getColIndex() + ") to agent " + countingAgent.getAgentId().getLocalName());
                            agent.send(msg);

                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void receiveResult() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
        ACLMessage msg = agent.receive(mt);
        if (msg != null) {
            try {
                MatrixPart mf = (MatrixPart) msg.getContentObject();
                if (MatrixPart.MatrixPartStatus.Calculated.equals(mf.getState()))
                    agent.getResultMatrix().setValue(mf.getRowIndex(), mf.getColIndex(), mf.getResult());
                System.out.println(agent.getLocalName() + ": got partial result (" + mf.getRowIndex() + ";" + mf.getColIndex() + ") from agent " + msg.getSender().getLocalName());
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

            //printMatrix(agent.getResultMatrix());


        }

    }


    private void printMatrix(Matrix m) {
        for (double[] r : m.getValues()) {
            for (double v : r) {
                System.out.print(v + "\t");
            }
            System.out.println();
        }
    }
}
