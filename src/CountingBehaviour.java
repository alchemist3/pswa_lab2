import java.io.IOException;
import java.util.Random;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import matrix.MatrixPart;

@SuppressWarnings("serial")
public class CountingBehaviour extends CyclicBehaviour {


    private CountingAgent agent;
    private MatrixPart mf;

    public CountingBehaviour(CountingAgent agent) {
        super();

        this.agent = agent;
    }

    @Override
    public void action() {
        if (agent.getAgentBusy()) {
            updateStatus();
        } else {
            processMatrix();
        }
    }


    private void updateStatus() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(DFServiceHelper.getInstance().findAgent(agent, "distributor"));

        System.out.println(agent.getLocalName() + ": is ready");
        agent.send(msg);
        agent.setAgentBusy(false);
    }

    private void processMatrix() {
        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchReplyWith(agent.getLocalName()), MessageTemplate.MatchPerformative(ACLMessage.CFP));
        ACLMessage msg = agent.receive(mt);
        if (msg != null) {
            ACLMessage reply = msg.createReply();
            agent.setAgentBusy(true);
            try {
                mf = (MatrixPart) msg.getContentObject();
            } catch (UnreadableException e) {
                e.printStackTrace();
            }

            if (new Random().nextInt(100) > 20) {
                reply.setPerformative(ACLMessage.CONFIRM);

                System.out.println(agent.getLocalName() + ": starts calculation");
                mf.setResult(calculate(mf));
                try {
                    reply.setContentObject(mf);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                System.out.println(agent.getLocalName() + ": waits " + agent.getDelay() + "ms");
                agent.doWait(agent.getDelay());

                System.out.println(agent.getLocalName() + ": sends partial result");
                agent.send(reply);
            } else {
                reply.setPerformative(ACLMessage.FAILURE);

                System.out.println(agent.getLocalName() + ": failure happened");
                try {
                    reply.setContentObject(mf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                agent.send(reply);

                System.out.println(agent.getLocalName() + ": waits for 2000ms");
                agent.doWait(agent.getDelay());
            }
        }
    }

    private double calculate(MatrixPart mf) {
        double result = 0.0d;

        for (int i = 0; i < mf.getSize(); i++) {
            result += mf.getCol()[i] * mf.getRow()[i];
        }

        return result;
    }
}