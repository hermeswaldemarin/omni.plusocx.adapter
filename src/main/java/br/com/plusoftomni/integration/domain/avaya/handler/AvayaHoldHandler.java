package br.com.plusoftomni.integration.domain.avaya.handler;

import br.com.plusoftomni.integration.domain.avaya.AvayaService;
import br.com.plusoftomni.integration.domain.telephonyplatform.CTIResponse;
import br.com.plusoftomni.integration.domain.telephonyplatform.CallbackDispatcher;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.HoldEvent;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.CTIEvents;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.EventHandler;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.telephony.callcontrol.CallControlTerminalConnection;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@EventHandler
public class AvayaHoldHandler {

    CallbackDispatcher callbackDispatcher;

    private AvayaService avayaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AvayaHoldHandler(AvayaService avayaService, CallbackDispatcher callbackDispatcher){
        this.avayaService = avayaService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Handle(CTIEvents.HOLD)
    public void execute(HoldEvent event){
        try {

            if (avayaService.getActiveTerminal() != null)  {
                if ((avayaService.getActiveTerminal().getTerminalConnections()) != null) {

                    logger.trace("Set Terminal [{}] on hold", avayaService.getActiveTerminal().getName());

                    ((CallControlTerminalConnection)avayaService.getActiveTerminal().getTerminalConnections()[0]).hold();

                    logger.trace("Terminal [{}] Hold setted", avayaService.getActiveTerminal().getName());

                }
            }

            callbackDispatcher.dispatch(new CTIResponse("hold", 0, "Holding...", Collections.unmodifiableMap(Stream.of(
                    new AbstractMap.SimpleEntry<>("arg1", "one"),
                    new AbstractMap.SimpleEntry<>("arg2", "two"))
                    .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())))));
        } catch (Exception e) {
            logger.error("Hold Error", e);
        }
    }
}
