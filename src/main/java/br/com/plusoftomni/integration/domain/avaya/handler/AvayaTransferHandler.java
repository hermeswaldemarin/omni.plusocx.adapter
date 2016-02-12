package br.com.plusoftomni.integration.domain.avaya.handler;

import br.com.plusoftomni.integration.domain.avaya.AvayaService;
import br.com.plusoftomni.integration.domain.telephonyplatform.CTIResponse;
import br.com.plusoftomni.integration.domain.telephonyplatform.CallbackDispatcher;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.TransferEvent;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.CTIEvents;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.EventHandler;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.Handle;
import com.avaya.jtapi.tsapi.LucentAddress;
import com.avaya.jtapi.tsapi.LucentCall;
import com.avaya.jtapi.tsapi.LucentTerminal;
import com.avaya.jtapi.tsapi.UserToUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.telephony.Call;
import javax.telephony.TerminalConnection;
import javax.telephony.callcontrol.CallControlCall;
import javax.telephony.callcontrol.CallControlTerminalConnection;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@EventHandler
public class AvayaTransferHandler {

    CallbackDispatcher callbackDispatcher;

    private AvayaService avayaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AvayaTransferHandler(AvayaService avayaService, CallbackDispatcher callbackDispatcher){
        this.avayaService = avayaService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Handle(CTIEvents.TRANSFER)
    public void execute(TransferEvent event){


        try{

            if (avayaService.getActiveCall() == null) {
                logger.info("Necessario Ligacao Ativa");
                return;
            }

            if(avayaService.getConsultCall() != null){
                logger.info("TRANSF -> transfer after consult = ");

                logger.info("TRANSF -> UUI = " + event.getUuiToSend());


                logger.info("TRANSF -> PASSO = 1 ");
                if((CallControlTerminalConnection)avayaService.getActiveTerminal().getTerminalConnections()[1] != null){
                    logger.info("TRANSF -> PASSO = 2 ");
                    UserToUserInfo avayaUUI = null;
                    if (event.getUuiToSend() != null) {
                        avayaUUI = new UserToUserInfo(event.getUuiToSend());
                    }
                    logger.info("TRANSF -> PASSO = 3 ");
                    try {
                        logger.info("TRANSF -> PASSO = 8 ");
                        ((CallControlCall)avayaService.getActiveTerminal().getTerminalConnections()[1].getConnection().getCall()).transfer((Call)avayaService.getActiveTerminal().getTerminalConnections()[0].getConnection().getCall());


                        logger.info("TRANSF -> PASSO = 9 ");
                        logger.info("Terminal [{}] Transferindo Para {}", avayaService.getActiveAddress().getName(), event.getDestinationNumber());
                        logger.info("TRANSF -> PASSO = 10 ");

                        avayaService.setConsultCall(null);

                    } catch (Exception e) {
                        logger.info("TRANSF -> ERRO = 1 ");
                        logger.error("Erro transfere - >", e);

                    }
                }
            }else{

                logger.info("TRANSF -> blind transfer = ");

                UserToUserInfo avayaUUI = null;
                if (event.getUuiToSend() != null) {
                    avayaUUI = new UserToUserInfo(event.getUuiToSend());
                }
                logger.info("TRANSF X -> 4 ");
                try {
                    ((CallControlCall)avayaService.getActiveCall()).setTransferEnable(true);
                    logger.info("TRANSF X -> 5 ");
                    //informo qual a terminalConnection que controla a transferencia
                    ((CallControlCall)avayaService.getActiveCall()).setTransferController(avayaService.getActiveTerminal().getTerminalConnections()[0]);
                    //coloco a ligacao ativa em held
                    logger.info("TRANSF X -> 6 ");
                    ((CallControlTerminalConnection)avayaService.getActiveTerminal().getTerminalConnections()[0]).hold();
                    //crio uma nova ligcacao que sera transferida
                    logger.info("TRANSF X -> 7 ");
                    Call ligacaoTransferencia = avayaService.getProvider().createCall();
                    logger.info("TRANSF X -> 8 ");
                    ((LucentCall)ligacaoTransferencia).connect((LucentTerminal)avayaService.getActiveTerminal(),
                            (LucentAddress)avayaService.getActiveAddress(), event.getDestinationNumber(), false, avayaUUI);

                    logger.info("TRANSF X -> 9 ");
                    ((CallControlCall)ligacaoTransferencia).setTransferEnable(true);
                    logger.info("TRANSF X -> 10 ");
                    ((CallControlCall)ligacaoTransferencia).setTransferController(avayaService.getActiveTerminal().getTerminalConnections()[1]);
                    logger.info("TRANSF X -> 11 ");
                    ((CallControlTerminalConnection)avayaService.getActiveTerminal().getTerminalConnections()[1]).hold();
                    logger.info("TRANSF X -> 12 ");
                    ((CallControlTerminalConnection)avayaService.getActiveTerminal().getTerminalConnections()[0]).unhold();
                    logger.info("TRANSF X -> 13 ");
                    ((CallControlCall)avayaService.getActiveCall()).transfer(ligacaoTransferencia);
                    logger.info("TRANSF X -> 14 ");
                    logger.info("Terminal [{}] Transferindo Para {}", avayaService.getActiveAddress().getName(), event.getDestinationNumber());
                    logger.info("TRANSF X -> 15 ");


                    try{
                        if (avayaService.getActiveTerminal().getTerminalConnections().length > 1){


                            TerminalConnection terminalConnectionDrop = avayaService.getActiveTerminal().getTerminalConnections()[1];

                            logger.trace("Terminal [{}] Ending Call", avayaService.getActiveTerminal().getName());

                            if (terminalConnectionDrop != null) {
                                terminalConnectionDrop.getConnection().disconnect();
                                logger.trace("Terminal [{}] Call Ended", avayaService.getActiveTerminal().getName());
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Erro disconnect - >", e);
                    }

                } catch (Exception e) {
                    logger.error("Erro transfere - >", e);
                }
            }


        }catch (Exception e) {
            logger.error("Erro transfere - >", e);
        }

        callbackDispatcher.dispatch(new CTIResponse("transfer", 0, "Transfer Completed.", Collections.unmodifiableMap(Stream.of(
                new AbstractMap.SimpleEntry<>("arg1", "one"),
                new AbstractMap.SimpleEntry<>("arg2", "two"))
                .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())))));
    }
}
