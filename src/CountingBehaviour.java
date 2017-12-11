import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
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
            System.out.println(agent.getLocalName() + ": begin calculation");
            agent.setAgentBusy(true);

            ACLMessage reply = msg.createReply();
            try {
                mf = (MatrixPart) msg.getContentObject();
                mf.setResult(calculate(mf));
                reply.setContentObject(mf);
                reply.setPerformative(ACLMessage.CONFIRM);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(agent.getLocalName() + ": waits for " + agent.getDelay() + "ms");
            agent.doWait(agent.getDelay());

            System.out.println(agent.getLocalName() + ": sends partial result");
            agent.send(reply);
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