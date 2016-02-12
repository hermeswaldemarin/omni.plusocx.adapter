package br.com.plusoftomni.integration.domain.avaya.handler;

import br.com.plusoftomni.integration.domain.avaya.AvayaService;
import br.com.plusoftomni.integration.domain.telephonyplatform.CTIResponse;
import br.com.plusoftomni.integration.domain.telephonyplatform.CallbackDispatcher;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.CancelConsultEvent;
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
public class AvayaCancelConsultHandler {

    CallbackDispatcher callbackDispatcher;

    private AvayaService avayaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AvayaCancelConsultHandler(AvayaService avayaService, CallbackDispatcher callbackDispatcher){
        this.avayaService = avayaService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Handle(CTIEvents.CANCELCONSULT)
    public void execute(CancelConsultEvent event){

        try {

            Runnable task2 = () -> {
                try {
                    if (avayaService.getActiveCall() != null) {

                        logger.trace("Active Connections : {}", avayaService.getActiveTerminal().getTerminalConnections().length);

                        if (avayaService.getActiveTerminal().getTerminalConnections().length > 1)
                        {
                            ((CallControlTerminalConnection)avayaService.getActiveTerminal().getTerminalConnections()[1]).getConnection().disconnect();
                            ((CallControlTerminalConnection)avayaService.getActiveTerminal().getTerminalConnections()[0]).unhold();
                        }
                        else
                        {
                            if (avayaService.getActiveTerminal().getTerminalConnections().length == 1){
                                ((CallControlTerminalConnection)avayaService.getActiveTerminal().getTerminalConnections()[0]).unhold();
                            }else{
                                logger.info("There are no current consult");
                            }
                        }

                        avayaService.setConsultCall(null);

                    } else {
                        logger.info("Terminal connection not found");
                    }
                } catch (Exception e) {
                    logger.error("Cancel Consult Error", e);
                }

            };

            new Thread(task2).start();

            callbackDispatcher.dispatch(new CTIResponse("cancelConsult", 0, "Consult canceled.", Collections.unmodifiableMap(Stream.of(
                    new AbstractMap.SimpleEntry<>("arg1", "one"),
                    new AbstractMap.SimpleEntry<>("arg2", "two"))
                    .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())))));

        } catch (Exception e) {
            logger.error("Cancel Consult Error", e);
        }

    }
}
