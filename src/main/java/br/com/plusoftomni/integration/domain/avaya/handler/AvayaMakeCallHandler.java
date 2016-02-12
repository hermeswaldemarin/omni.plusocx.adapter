package br.com.plusoftomni.integration.domain.avaya.handler;

import br.com.plusoftomni.integration.domain.avaya.AvayaService;
import br.com.plusoftomni.integration.domain.telephonyplatform.CTIResponse;
import br.com.plusoftomni.integration.domain.telephonyplatform.CallbackDispatcher;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.MakeCallEvent;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.CTIEvents;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.EventHandler;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.Handle;
import com.avaya.jtapi.tsapi.LucentAddress;
import com.avaya.jtapi.tsapi.LucentCall;
import com.avaya.jtapi.tsapi.LucentTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.telephony.Call;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@EventHandler
public class AvayaMakeCallHandler {

    CallbackDispatcher callbackDispatcher;

    private AvayaService avayaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AvayaMakeCallHandler(AvayaService avayaService, CallbackDispatcher callbackDispatcher){
        this.avayaService = avayaService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Handle(CTIEvents.MAKECALL)
    public void execute(MakeCallEvent event){

        synchronized (this){
            try {

                if (avayaService.getActiveAddress() == null ) {
                    throw new RuntimeException("Terminal need to be monitored.");
                }

                logger.trace("Creating the new call object");

                Call call = avayaService.getProvider().createCall();

                logger.trace("Call instantiated");

                ((LucentCall)call).connect((LucentTerminal)avayaService.getActiveTerminal(), (LucentAddress)avayaService.getActiveAddress(), event.getCallNumber(), false, null);

                logger.trace("Call created");

                avayaService.setActiveCall(call);

                avayaService.setMakeCallExecuted(true);

                callbackDispatcher.dispatch(new CTIResponse("makeCall", 0, "Call complete.", Collections.unmodifiableMap(Stream.of(
                        new AbstractMap.SimpleEntry<>("arg1", "one"),
                        new AbstractMap.SimpleEntry<>("arg2", "two"))
                        .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())))));

            } catch (Exception e) {
                throw  new RuntimeException("MakeCall Error" + e.getMessage());
            }
        }
    }
}
