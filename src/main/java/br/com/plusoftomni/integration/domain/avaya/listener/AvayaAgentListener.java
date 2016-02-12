package br.com.plusoftomni.integration.domain.avaya.listener;

import br.com.plusoftomni.integration.domain.avaya.AvayaService;
import br.com.plusoftomni.integration.domain.telephonyplatform.CTIStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.telephony.TerminalEvent;
import javax.telephony.callcenter.AgentTerminalEvent;
import javax.telephony.callcenter.AgentTerminalListener;

@Service
@Lazy
public class AvayaAgentListener implements AgentTerminalListener {

    private AvayaService avayaService;
    private Object sigProvider;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AvayaAgentListener(AvayaService avayaService){
        this.avayaService = avayaService;
    }

	public void terminalListenerEnded(TerminalEvent arg0) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("LISTENER_ENDED"));
        logger.trace(avayaService.getActiveTerminal().getName()+" terminalListenerEnded");
	}

	public void agentTerminalBusy(AgentTerminalEvent arg0) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("TERMINAL_BUSY"));
        logger.trace(avayaService.getActiveTerminal().getName()+" agentTerminalBusy");
	}

	public void agentTerminalLoggedOff(AgentTerminalEvent arg0) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("LOGGED_OFF"));
        logger.trace(avayaService.getActiveTerminal().getName()+" agentTerminalLoggedOff");
		avayaService.setConsultCall(null);
        avayaService.setActiveCall(null);
        avayaService.setMakeCallExecuted(false);
	}

	public void agentTerminalLoggedOn(AgentTerminalEvent arg0) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("LOGGED_ON"));
        logger.trace(avayaService.getActiveTerminal().getName()+" agentTerminalLoggedOn");
        avayaService.setConsultCall(null);
        avayaService.setActiveCall(null);
        avayaService.setMakeCallExecuted(false);
	}

	public void agentTerminalNotReady(AgentTerminalEvent arg0) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("NOT_READY"));
        logger.trace(avayaService.getActiveTerminal().getName()+" agentTerminalNotReady");
        avayaService.setConsultCall(null);
        avayaService.setActiveCall(null);
        avayaService.setMakeCallExecuted(false);
	}

	public void agentTerminalReady(AgentTerminalEvent arg0) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("READY"));
        logger.trace(avayaService.getActiveTerminal().getName()+" agentTerminalReady");
        avayaService.setConsultCall(null);
        avayaService.setActiveCall(null);
        avayaService.setMakeCallExecuted(false);
	}

	public void agentTerminalUnknown(AgentTerminalEvent arg0) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("UNKNOWN"));
        logger.trace(avayaService.getActiveTerminal().getName()+" agentTerminalUnknown");
	}

	public void agentTerminalWorkNotReady(AgentTerminalEvent arg0) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("ACW"));
        logger.trace(avayaService.getActiveTerminal().getName()+" agentTerminalWorkNotReady");
        avayaService.setConsultCall(null);
        avayaService.setActiveCall(null);
        avayaService.setMakeCallExecuted(false);
	}

	public void agentTerminalWorkReady(AgentTerminalEvent arg0) {
        avayaService.getCallbackDispatcher().dispatch(new CTIStatusResponse("WORK READY"));
        logger.trace(avayaService.getActiveTerminal().getName()+" agentTerminalWorkReady");
        avayaService.setConsultCall(null);
        avayaService.setActiveCall(null);
        avayaService.setMakeCallExecuted(false);
	}



}
