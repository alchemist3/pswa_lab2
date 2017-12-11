import java.util.ArrayList;
import java.util.List;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;


public class DFServiceHelper {

    private static DFServiceHelper instance = null;

    protected DFServiceHelper() {}

    public static DFServiceHelper getInstance() {
        if (instance == null) {
            instance = new DFServiceHelper();
        }

        return instance;
    }

    public void register(Agent agent, String serviceName, String serviceType) {
        ServiceDescription sd = new ServiceDescription();
        sd.setName(serviceName);
        sd.setType(serviceType);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(agent.getAID());
        dfd.addServices(sd);

        try {
            DFService.register(agent, dfd);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }
    }


    public List<CountingAgent.CountingAgentInfo> findCountingAgents(Agent agent, String serviceType) {
        List<CountingAgent.CountingAgentInfo> countingAgents = new ArrayList<>();

        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceType);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(agent, dfd);
            for (DFAgentDescription dfad : result) {
                countingAgents.add(new CountingAgent.CountingAgentInfo(dfad.getName()));
            }
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }

        return countingAgents;
    }

    public AID findAgent(Agent agent, String serviceType) {
        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceType);

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(agent, dfd);
            if (result.length > 0) {
                return result[0].getName();
            }
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
