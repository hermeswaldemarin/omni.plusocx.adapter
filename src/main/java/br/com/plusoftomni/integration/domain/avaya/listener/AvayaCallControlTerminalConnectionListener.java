package br.com.plusoftomni.integration.domain.avaya.listener;

import br.com.plusoftomni.integration.domain.avaya.AvayaService;
import br.com.plusoftomni.integration.domain.telephonyplatform.CTIStatusResponse;
import com.avaya.jtapi.tsapi.LucentCallInfo;
import com.avaya.jtapi.tsapi.LucentV6Agent;
import com.avaya.jtapi.tsapi.UserToUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.telephony.*;
import javax.telephony.callcontrol.CallControlCall;
import javax.telephony.callcontrol.CallControlConnectionEvent;
import javax.telephony.callcontrol.CallControlTerminalConnectionEvent;
import javax.telephony.callcontrol.CallControlTerminalConnectionListener;

@Service
@Lazy
public class AvayaCallControlTerminalConnectionListener implements
        CallControlTerminalConnectionListener {

    private final AvayaService avayaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public boolean onHold = false;

    @Autowired
    public AvayaCallControlTerminalConnectionListener(AvayaService avayaService){
        this.avayaService = avayaService;
    }

    public void terminalConnectionBridged(CallControlTerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("BRIDGED"));
        logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionBridged");
    }

    public void terminalConnectionDropped(CallControlTerminalConnectionEvent e) {
        if(!onHold){
            avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("DROPPED"));
        }
        logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionDropped");
    }

    public void terminalConnectionHeld(CallControlTerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("HOLD"));
        onHold = true;
        logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionHeld");
    }

    public void terminalConnectionInUse(CallControlTerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("IN_USE"));
        logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionInUse");
    }

    public void terminalConnectionRinging(CallControlTerminalConnectionEvent e) {

        logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionRinging");

        Address callingAddress = e.getCallingAddress();
        Call call = e.getCall();
        UserToUserInfo avayaUUI = null;
        String uui = new String();

        if(avayaService.getConsultCall() == null){

            logger.trace(avayaService.getActiveTerminal().getName()+" HAS ACTIVE CALL");


            if(!avayaService.isMakeCallExecuted()){

                avayaService.setActiveCall(call);

                if (callingAddress != null) {
                    logger.trace( "Terminal [{}] receiving call from [{}]", avayaService.getActiveTerminal().getName(), callingAddress.getName());
                } else {
                    logger.trace( "Terminal [{}] receiving call from [unknown]", avayaService.getActiveTerminal().getName());
                }

                if (call instanceof LucentCallInfo) {
                    if (e.getID() != CallControlConnectionEvent.CALLCTL_CONNECTION_NETWORK_REACHED) {
                        avayaUUI = ((LucentCallInfo) call).getUserToUserInfo();
                        if (avayaUUI != null) {
                            logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionRinging : avayaUUI " + avayaUUI.getString());

                            uui = avayaUUI.getString().split("\0")[0];
                        }
                    }
                }

                logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionRinging : 1 ");

                if (uui != null) {
                    logger.info("UUI: " + uui);
                }

                logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionRinging : 2 ");

                String origin ;
                if (e.getCallingAddress()!= null) {
                    origin = e.getCallingAddress().getName();
                } else{
                    origin = "unknown";
                }

                try
                {

                    logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionRinging : 5 ");

                    uui = uui.replace('\r', ' ');
                    uui = uui.replace('\n', ' ');

                    logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionRinging : 6 ");

                    logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionRinging : 11 ");

                    String calledNumber = "";
                    if ( avayaService.getActiveCall() != null ){

                        Address adrAux = ((CallControlCall)avayaService.getActiveCall()).getCalledAddress();
                        if(adrAux != null){
                            calledNumber = adrAux.getName();
                        }
                    }


                    avayaService.sendUUI(uui, origin, calledNumber);

                    logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionRinging : 13 ");

                }
                catch (Exception e2)
                {
                    logger.error("terminalConnectionRinging : Error : ", e2);
                }
            }

        }
    }


    public void terminalConnectionTalking(CallControlTerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("SPEAKING"));
        onHold = false;
        logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionTalking");
    }


    public void terminalConnectionUnknown(CallControlTerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("UNKNOWN"));
        logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionUnknown");
    }


    public void connectionAlerting(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("ALERTING"));
        logger.trace(avayaService.getActiveTerminal().getName()+" connectionAlerting");
    }


    public void connectionDialing(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("DIALING"));
        logger.trace(avayaService.getActiveTerminal().getName()+" connectionDialing");
    }


    public void connectionDisconnected(CallControlConnectionEvent e) {
        if(!onHold){
            avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("DISCONNECTED"));
        }
        logger.trace(avayaService.getActiveTerminal().getName()+" connectionDisconnected");
    }

    public void connectionEstablished(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("CALL_RECEIVED"));

        Call call = e.getCall();

        if(this.avayaService.getConsultCall() == null){
            if(this.avayaService.getActiveCall() == null){

                this.avayaService.setActiveCall(call);
            }

        }

        logger.trace(avayaService.getActiveTerminal().getName()+" connectionEstablished");
    }


    public void connectionFailed(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("FAILED"));
        logger.trace(avayaService.getActiveTerminal().getName()+" connectionFailed");
    }


    public void connectionInitiated(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("INITIATED"));
        logger.trace(avayaService.getActiveTerminal().getName()+" connectionInitiated");
    }


    public void connectionNetworkAlerting(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("NETWORK_ALERTING"));
        logger.trace(avayaService.getActiveTerminal().getName()+" connectionNetworkAlerting");
    }


    public void connectionNetworkReached(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("REACHED"));
        logger.trace(avayaService.getActiveTerminal().getName()+" connectionNetworkReached");
    }


    public void connectionOffered(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("OFFERED"));
        logger.trace(avayaService.getActiveTerminal().getName()+" connectionOffered");
    }


    public void connectionQueued(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("QUEUED"));
        logger.trace(avayaService.getActiveTerminal().getName()+" connectionQueued");
    }


    public void connectionUnknown(CallControlConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("UNKNOWN"));
        logger.trace(avayaService.getActiveTerminal().getName()+" connectionUnknown");
    }


    public void callActive(CallEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("ACTIVE"));
        logger.trace(avayaService.getActiveTerminal().getName()+" callActive");
    }


    public void callEventTransmissionEnded(CallEvent e) {

        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("CALL_ENDED"));

        Runnable task2 = () -> {
            try {
                int stateAgent = ((LucentV6Agent)avayaService.getAgentLogged()).getState();
                avayaService.setConsultCall(null);
                avayaService.setActiveCall(null);
                avayaService.setMakeCallExecuted(false);

                String status = null;

                switch (stateAgent) {
                    case 0:
                        status = "UNKNOWN";
                        break;
                    case 1:
                        status = "LOGGED";
                        break;
                    case 2:
                        status = "LOGGED OUT";
                        break;
                    case 3:
                        status = "NOT READY";
                        break;
                    case 4:
                        status = "READY";
                        break;
                    case 5:
                        status = "ACW";
                        break;
                    case 6:
                        status = "WORKING_READY";
                        break;
                    default:
                        break;
                }
                if(status != null)
                    avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse(status));
            }catch (Exception ex) {
                logger.error("callEventTransmissionEnded - > ERROR ", ex);

            }
        };

        new Thread(task2).start();

    }


    public void callInvalid(CallEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("INVALID"));
        logger.trace(avayaService.getActiveTerminal().getName()+" callInvalid");
    }


    public void multiCallMetaMergeEnded(MetaEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("MERGE_ENDED"));
        logger.trace(avayaService.getActiveTerminal().getName()+" multiCallMetaMergeEnded");
    }


    public void multiCallMetaMergeStarted(MetaEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("MERGE_STARTED"));
        logger.trace(avayaService.getActiveTerminal().getName()+" multiCallMetaMergeStarted");
    }


    public void multiCallMetaTransferEnded(MetaEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("MERGE_TRANSFER_ENDED"));
        logger.trace(avayaService.getActiveTerminal().getName()+" multiCallMetaTransferEnded");
    }


    public void multiCallMetaTransferStarted(MetaEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("M_TRANSF_STARTED"));
        logger.trace(avayaService.getActiveTerminal().getName()+" multiCallMetaTransferStarted");
    }


    public void singleCallMetaProgressEnded(MetaEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("M_PROGRESS_ENDED"));
        logger.trace(avayaService.getActiveTerminal().getName()+" singleCallMetaProgressEnded");
    }


    public void singleCallMetaProgressStarted(MetaEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("M_PROGRESS_STARTED"));
        logger.trace(avayaService.getActiveTerminal().getName()+" singleCallMetaProgressStarted");
    }


    public void singleCallMetaSnapshotEnded(MetaEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("M_SNAP_ENDED"));
        logger.trace(avayaService.getActiveTerminal().getName()+" singleCallMetaSnapshotEnded");
    }


    public void singleCallMetaSnapshotStarted(MetaEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("M_SNAP_STARTED"));
        logger.trace(avayaService.getActiveTerminal().getName()+" singleCallMetaSnapshotStarted");
    }


    public void connectionAlerting(ConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("ALERTING"));
        logger.trace(avayaService.getActiveTerminal().getName()+" connectionAlerting");
    }


    public void connectionConnected(ConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("CONNECTED"));
        logger.trace(avayaService.getActiveTerminal().getName()+" connectionConnected");
    }


    public void connectionCreated(ConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("CREATED"));
        logger.trace(avayaService.getActiveTerminal().getName()+" connectionCreated");
    }


    public void connectionDisconnected(ConnectionEvent e) {
        if(!onHold){
            avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("DISCONNECTED"));
        }
        logger.trace(avayaService.getActiveTerminal().getName()+" connectionDisconnected");
    }


    public void connectionFailed(ConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("FAILED"));
        logger.trace(avayaService.getActiveTerminal().getName()+" connectionFailed");
    }


    public void connectionInProgress(ConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("IN_PROGRESS"));
        logger.trace(avayaService.getActiveTerminal().getName()+" connectionInProgress");
    }


    public void connectionUnknown(ConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("UNKNOWN"));
        logger.trace(avayaService.getActiveTerminal().getName()+" connectionUnknown");
    }


    public void terminalConnectionActive(TerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("ACTIVE"));
        logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionActive");

    }


    public void terminalConnectionCreated(TerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("CREATED"));
        logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionCreated");

    }


    public void terminalConnectionDropped(TerminalConnectionEvent e) {
        if(!onHold){
            avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("DROPPED"));
        }
        logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionDropped");
    }


    public void terminalConnectionPassive(TerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("PASSIVE"));
        logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionPassive");
    }


    public void terminalConnectionRinging(TerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("RINGING"));
        logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionRinging");
    }


    public void terminalConnectionUnknown(TerminalConnectionEvent e) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("UNKNOWN"));
        logger.trace(avayaService.getActiveTerminal().getName()+" terminalConnectionUnknown");
    }

}