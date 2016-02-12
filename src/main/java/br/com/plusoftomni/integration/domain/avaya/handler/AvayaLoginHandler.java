package br.com.plusoftomni.integration.domain.avaya.handler;

import br.com.plusoftomni.integration.domain.avaya.AvayaService;
import br.com.plusoftomni.integration.domain.telephonyplatform.CTIErrorResponse;
import br.com.plusoftomni.integration.domain.telephonyplatform.CTIResponse;
import br.com.plusoftomni.integration.domain.telephonyplatform.CallbackDispatcher;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.LoginEvent;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.CTIEvents;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.EventHandler;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.Handle;
import com.avaya.jtapi.tsapi.LucentAddress;
import com.avaya.jtapi.tsapi.LucentAgent;
import com.avaya.jtapi.tsapi.LucentTerminal;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.telephony.JtapiPeer;
import javax.telephony.JtapiPeerFactory;
import javax.telephony.Provider;
import javax.telephony.callcenter.ACDAddress;
import javax.telephony.callcenter.Agent;
import javax.telephony.callcenter.AgentTerminal;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@EventHandler
public class AvayaLoginHandler {

    CallbackDispatcher callbackDispatcher;

    private AvayaService avayaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AvayaLoginHandler(AvayaService avayaService, CallbackDispatcher callbackDispatcher){
        this.avayaService = avayaService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Handle(CTIEvents.LOGIN)
    public void execute(LoginEvent event){

        Runnable task2 = () -> {

            try{

                logger.trace("LoginEvent received {}", ReflectionToStringBuilder.toString(event, ToStringStyle.MULTI_LINE_STYLE) );

                avayaService.getAvayaProviderListener().setSigProvider(avayaService.getSigProvider());

                String providerString = event.getServiceName() + ";loginID=" + event.getUserAdmin() + ";passwd=" + event.getPasswordAdmin();

                logger.trace("Getting JtapiPeer");

                JtapiPeer peer = JtapiPeerFactory.getJtapiPeer(null);

                logger.trace("JtapiPeer returned {}", ReflectionToStringBuilder.toString(peer, ToStringStyle.MULTI_LINE_STYLE));

                avayaService.setProvider(peer.getProvider(providerString));

                logger.trace("Provider returned {}",ReflectionToStringBuilder.toString(avayaService.getProvider(), ToStringStyle.MULTI_LINE_STYLE) );

                synchronized (avayaService.getSigProvider()) {

                    logger.trace("Waiting for provider start");

                    avayaService.getSigProvider().wait();

                    logger.trace("Provider connection established: Provider State {}", avayaService.getProvider().getState());

                    if (avayaService.getProvider().getState() == Provider.OUT_OF_SERVICE) {
                        throw new RuntimeException("Provider Nao Inicializado");
                    }
                }

                avayaService.setActiveAddress(avayaService.getProvider().getAddress(event.getTerminalNumber()));
                avayaService.setActiveTerminal(avayaService.getProvider().getTerminal(event.getTerminalNumber()));

                if(event.getGroup()!= null
                        && !event.getGroup().equals("")){

                    avayaService.setAcdAddress((ACDAddress) avayaService.getProvider().getAddress(event.getGroup()));
                    logger.trace("ACD Group {}", avayaService.getAcdAddress());
                }

                logger.trace("Addind Agent to activeTerminal {}, activeAddress {}, agentNumber {} ",
                        ReflectionToStringBuilder.toString(avayaService.getActiveTerminal(), ToStringStyle.MULTI_LINE_STYLE),
                        ReflectionToStringBuilder.toString(avayaService.getActiveAddress(), ToStringStyle.MULTI_LINE_STYLE),
                        event.getAgentNumber());

                ((LucentTerminal)avayaService.getActiveTerminal()).addAgent((LucentAddress)avayaService.getActiveAddress(), null,
                        Agent.LOG_IN, LucentAgent.MODE_AUTO_IN, event.getAgentNumber(), "");

                avayaService.setAgentLogged(getAgent());

                logger.trace("Agent added");

                String agentName = ((LucentTerminal)avayaService.getActiveTerminal()).getDirectoryName();

                callbackDispatcher.dispatch(new CTIResponse("login", 0, "Login OK", Collections.unmodifiableMap(Stream.of(
                        new AbstractMap.SimpleEntry<>("agentName", agentName!=null?agentName:"" ))
                        .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())))));

            }catch (Throwable e){
                callbackDispatcher.dispatch(new CTIErrorResponse("Avaya Initialize Erro: ", e));
                throw new ExceptionInInitializerError(e);
            }

        };

        new Thread(task2).start();


    }

    private Agent getAgent() {
        if (avayaService.getActiveTerminal() != null) {
            return ((AgentTerminal)avayaService.getActiveTerminal()).getAgents()[0];
        }
        return null;
    }

}
