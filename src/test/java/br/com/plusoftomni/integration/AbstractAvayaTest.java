package br.com.plusoftomni.integration;

import br.com.plusoftomni.integration.domain.avaya.AvayaService;
import br.com.plusoftomni.integration.domain.avaya.listener.AvayaAgentListener;
import br.com.plusoftomni.integration.domain.avaya.listener.AvayaCallControlTerminalConnectionListener;
import br.com.plusoftomni.integration.domain.avaya.listener.AvayaProviderListener;
import br.com.plusoftomni.integration.domain.telephonyplatform.CallbackDispatcher;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.LoginEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hermeswaldemarin on 15/12/15.
 */
public abstract class AbstractAvayaTest {

    protected LoginEvent loginEvent;

    public static String ramal1 = "2409";
    public static String agente1 = "1480";

    public static String ramal2 = "2302";
    public static String agente2 = "1488";

    public static String ramal3 = "3168";
    public static String agente3 = "1478";

    public static String serviceName = "AVAYA#PLURISMIDIA#CSTA#CTIAES";
    public static String userAdmin = "ctiuser";
    public static String passAdmin = "Ctiuser@001";

    @Autowired
    CallbackDispatcher callbackDispatcher;

    public void initLoginEvent(){

        loginEvent = new LoginEvent();
        loginEvent.setServiceName(AbstractAvayaTest.serviceName);
        loginEvent.setUserAdmin(AbstractAvayaTest.userAdmin);
        loginEvent.setPasswordAdmin(AbstractAvayaTest.passAdmin);
        loginEvent.setTerminalNumber(AbstractAvayaTest.ramal1);
        loginEvent.setAgentNumber(AbstractAvayaTest.agente1);

    }

    protected AvayaService buildExtraAvayaService(){
        AvayaService service = new AvayaService();

        service.setAvayaCallControlTerminalConnectionListener(new AvayaCallControlTerminalConnectionListener(service));
        service.setAvayaProviderListener(new AvayaProviderListener(service));
        service.setAvayaAgentListener(new AvayaAgentListener(service));

        service.setCallbackDispatcher(callbackDispatcher);

        return service;
    }

    protected LoginEvent initLoginEventExtraService(String terminal, String agent){

        LoginEvent loginEventSecondAgent = new LoginEvent();
        loginEventSecondAgent.setServiceName(AbstractAvayaTest.serviceName);
        loginEventSecondAgent.setUserAdmin(AbstractAvayaTest.userAdmin);
        loginEventSecondAgent.setPasswordAdmin(AbstractAvayaTest.passAdmin);
        loginEventSecondAgent.setTerminalNumber(terminal);
        loginEventSecondAgent.setAgentNumber(agent);

        return loginEventSecondAgent;

    }

}
