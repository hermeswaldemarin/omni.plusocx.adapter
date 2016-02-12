package br.com.plusoftomni.integration;

import br.com.plusoftomni.integration.domain.telephonyplatform.CallbackDispatcher;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.LoginEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hermeswaldemarin on 15/12/15.
 */
public abstract class AbstractPlusOCXTest {

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
        loginEvent.setServiceName(AbstractPlusOCXTest.serviceName);
        loginEvent.setUserAdmin(AbstractPlusOCXTest.userAdmin);
        loginEvent.setPasswordAdmin(AbstractPlusOCXTest.passAdmin);
        loginEvent.setTerminalNumber(AbstractPlusOCXTest.ramal1);
        loginEvent.setAgentNumber(AbstractPlusOCXTest.agente1);

    }


}
