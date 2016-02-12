package br.com.plusoftomni.integration.domain.avaya;

import br.com.plusoftomni.integration.domain.avaya.listener.AvayaAgentListener;
import br.com.plusoftomni.integration.domain.avaya.listener.AvayaCallControlTerminalConnectionListener;
import br.com.plusoftomni.integration.domain.avaya.listener.AvayaProviderListener;
import br.com.plusoftomni.integration.domain.telephonyplatform.CTIResponse;
import br.com.plusoftomni.integration.domain.telephonyplatform.CallbackDispatcher;
import com.avaya.jtapi.tsapi.LucentTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.telephony.Address;
import javax.telephony.Call;
import javax.telephony.Provider;
import javax.telephony.Terminal;
import javax.telephony.callcenter.ACDAddress;
import javax.telephony.callcenter.Agent;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by hermeswaldemarin on 15/12/15.
 */
@Service
public class AvayaService {

    private Provider provider;
    private Address activeAddress;
    private Terminal activeTerminal;
    private Call activeCall;
    private Call consultCall;
    private Object sigProvider = new Object();
    private Agent agentLogged;
    private ACDAddress acdAddress;
    private boolean makeCallExecuted = false;

    @Autowired
    private AvayaProviderListener avayaProviderListener;

    @Autowired
    private AvayaCallControlTerminalConnectionListener avayaCallControlTerminalConnectionListener;

    @Autowired
    private AvayaAgentListener avayaAgentListener;

    @Autowired
    private CallbackDispatcher callbackDispatcher;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) throws Exception {
        this.provider = provider;
        if(this.provider != null)
            this.provider.addProviderListener(getAvayaProviderListener());
    }

    public Address getActiveAddress() {
        return activeAddress;
    }

    public void setActiveAddress(Address activeAddress) {
        this.activeAddress = activeAddress;
    }

    public Terminal getActiveTerminal() {
        return activeTerminal;
    }

    public void setActiveTerminal(Terminal activeTerminal) throws Exception {
        this.activeTerminal = activeTerminal;
        if(this.activeTerminal != null)
            this.activeTerminal.addCallListener(getAvayaCallControlTerminalConnectionListener());
    }

    public Call getActiveCall() {
        return activeCall;
    }

    public void setActiveCall(Call activeCall) {
        this.activeCall = activeCall;
    }

    public AvayaProviderListener getAvayaProviderListener() {
        return avayaProviderListener;
    }

    public AvayaCallControlTerminalConnectionListener getAvayaCallControlTerminalConnectionListener() {
        return avayaCallControlTerminalConnectionListener;
    }

    public Object getSigProvider() {
        return sigProvider;
    }

    public Agent getAgentLogged() {
        return agentLogged;
    }

    public void setAgentLogged(Agent agentLogged) throws Exception{
        ((LucentTerminal)getActiveTerminal()).addTerminalListener(avayaAgentListener);
        this.agentLogged = agentLogged;
    }

    public CallbackDispatcher getCallbackDispatcher() {
        return callbackDispatcher;
    }

    public void sendUUI(String uui, String origin, String calledNumber) {
        callbackDispatcher.dispatch(new CTIResponse("ring", 0, "ringing...", Collections.unmodifiableMap(Stream.of(
                new AbstractMap.SimpleEntry<>("uui", uui),
                new AbstractMap.SimpleEntry<>("origin", origin),
                new AbstractMap.SimpleEntry<>("calledNumber", calledNumber))
                .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()))))
        );
    }

    public Call getConsultCall() {
        return consultCall;
    }

    public void setConsultCall(Call consultCall) {
        this.consultCall = consultCall;
    }

    public ACDAddress getAcdAddress() {
        return acdAddress;
    }

    public void setAcdAddress(ACDAddress acdAddress) {
        this.acdAddress = acdAddress;
    }

    public void setAvayaProviderListener(AvayaProviderListener avayaProviderListener) {
        this.avayaProviderListener = avayaProviderListener;
    }

    public void setAvayaCallControlTerminalConnectionListener(AvayaCallControlTerminalConnectionListener avayaCallControlTerminalConnectionListener) {
        this.avayaCallControlTerminalConnectionListener = avayaCallControlTerminalConnectionListener;
    }

    public void setAvayaAgentListener(AvayaAgentListener avayaAgentListener) {
        this.avayaAgentListener = avayaAgentListener;
    }

    public void setCallbackDispatcher(CallbackDispatcher callbackDispatcher) {
        this.callbackDispatcher = callbackDispatcher;
    }

    public void setSigProvider(Object sigProvider) {
        this.sigProvider = sigProvider;
    }

    public boolean isMakeCallExecuted() {
        return makeCallExecuted;
    }

    public void setMakeCallExecuted(boolean makeCallExecuted) {
        this.makeCallExecuted = makeCallExecuted;
    }
}
