import java.util.Random;
import jade.core.AID;
import jade.core.Agent;


@SuppressWarnings("serial")
public class CountingAgent extends Agent {

    private int delay;
    private boolean agentBusy;

    public int getDelay() {
        return delay;
    }

    public boolean getAgentBusy() {
        return agentBusy;
    }

    public void setAgentBusy(boolean agentBusy) {
        this.agentBusy = agentBusy;
    }

    public static class CountingAgentInfo {
        private AID agentId;
        private boolean agentBusy;

        public CountingAgentInfo(AID agentId) {
            this.agentId = agentId;
            this.agentBusy = true;

        }

        public AID getAgentId() {
            return agentId;
        }

        public boolean getAgentBusy() {return agentBusy;}
        public void setAgentBusy(boolean agentBusy) {this.agentBusy = agentBusy; }
    }

    @Override
    protected void setup() {

        delay = new Random().nextInt(1000) + 500;
        agentBusy = true;

        DFServiceHelper.getInstance().register(this, "matrixCalculator", "calculator");

        addBehaviour(new CountingBehaviour(this));

    }
}
