package br.com.plusoftomni.integration.domain.ocxtest.handler;

import br.com.plusoftomni.integration.domain.ocxtest.PlusOCXService;
import br.com.plusoftomni.integration.domain.telephonyplatform.CTIResponse;
import br.com.plusoftomni.integration.domain.telephonyplatform.CallbackDispatcher;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.LoginEvent;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.CTIEvents;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.EventHandler;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.Handle;
import ezjcom.JComException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@EventHandler
public class PlusOCXLoginHandler {

    CallbackDispatcher callbackDispatcher;

    private PlusOCXService plusOCXService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public PlusOCXLoginHandler(PlusOCXService plusOCXService, CallbackDispatcher callbackDispatcher){
        this.plusOCXService = plusOCXService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Handle(CTIEvents.LOGIN)
    public void execute(LoginEvent event){

        try {

            //plusOCXService

            String agentName = "test";

            callbackDispatcher.dispatch(new CTIResponse("login", 0, "Login OK", Collections.unmodifiableMap(Stream.of(
                    new AbstractMap.SimpleEntry<>("agentName", agentName!=null?agentName:"" ))
                    .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())))));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
