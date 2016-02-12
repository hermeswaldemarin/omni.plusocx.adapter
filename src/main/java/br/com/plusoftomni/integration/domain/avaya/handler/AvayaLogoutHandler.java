package br.com.plusoftomni.integration.domain.avaya.handler;

import br.com.plusoftomni.integration.domain.avaya.AvayaService;
import br.com.plusoftomni.integration.domain.telephonyplatform.CTIResponse;
import br.com.plusoftomni.integration.domain.telephonyplatform.CallbackDispatcher;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.LogoutEvent;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.CTIEvents;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.EventHandler;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.telephony.CallListener;
import javax.telephony.ProviderListener;
import javax.telephony.callcenter.Agent;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@EventHandler
public class AvayaLogoutHandler {

    CallbackDispatcher callbackDispatcher;

    private AvayaService avayaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AvayaLogoutHandler(AvayaService avayaService, CallbackDispatcher callbackDispatcher){
        this.avayaService = avayaService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Handle(CTIEvents.LOGOUT)
    public void execute(LogoutEvent event){
        try{

            if (avayaService.getAgentLogged() != null) {

                Agent a = avayaService.getAgentLogged();
                String agentId = a.getAgentID();

                logger.trace("Agend ID {} to be logged out", agentId);

                a.getAgentTerminal().removeAgent(a);

            } else {
                logger.trace("Agent is not logged in");
            }

            CallListener[] callListenerArray = null;
            ProviderListener[] providerListener = null;

            if (avayaService.getActiveTerminal() != null) {
                if ((callListenerArray = avayaService.getActiveTerminal().getCallListeners()) != null) {

                    for( CallListener listener : callListenerArray){
                        avayaService.getActiveTerminal().removeCallListener(listener);

                        if(listener == null){
                            break;
                        }
                    }
                }

            }
            logger.trace("Shutting down the provider");
            avayaService.getProvider().shutdown();
            logger.trace("Provider is down");

            avayaService.setProvider(null);
            avayaService.setActiveTerminal(null);
            avayaService.setActiveCall(null);
            avayaService.setMakeCallExecuted(false);
            avayaService.setActiveAddress(null);

            logger.trace("The Avaya Integration is down.");


            callbackDispatcher.dispatch(new CTIResponse("logout", 0, "Logout OK", Collections.unmodifiableMap(Stream.of(
                    new AbstractMap.SimpleEntry<>("arg1", "one"),
                    new AbstractMap.SimpleEntry<>("arg2", "two"))
                    .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())))));

        }catch (Exception e) {
            logger.error("Logout Error", e);
        }
    }
}
