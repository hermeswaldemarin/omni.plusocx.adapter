package br.com.plusoftomni.integration;

import br.com.plusoftomni.integration.application.telephonyplatform.AbstractTelephonyPlatformTest;
import br.com.plusoftomni.integration.domain.ocxtest.PlusOCXService;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.LoginEvent;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.LogoutEvent;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PlusOCXImplItegrationTests extends AbstractTelephonyPlatformTest {

    public PlusOCXImplItegrationTests(){
        this.activeProfile = "";
        this.TIMEOUT = 10;
    }

    private void initLogin() throws Exception {
        LoginEvent loginEvent = new LoginEvent();
        loginEvent.setServiceName(AbstractPlusOCXTest.serviceName);
        loginEvent.setUserAdmin(AbstractPlusOCXTest.userAdmin);
        loginEvent.setPasswordAdmin(AbstractPlusOCXTest.passAdmin);
        loginEvent.setTerminalNumber(AbstractPlusOCXTest.ramal1);
        loginEvent.setAgentNumber(AbstractPlusOCXTest.agente1);

        assertTrue(getPayloadReturnFromEvent("login", loginEvent).contains("Login OK"));
    }

    protected PlusOCXService buildExtraAvayaService(){
        PlusOCXService service = new PlusOCXService(null);

        return service;
    }

    protected LoginEvent initLoginEventExtraService(String terminal, String agent){

        LoginEvent loginEventSecondAgent = new LoginEvent();
        loginEventSecondAgent.setServiceName(AbstractPlusOCXTest.serviceName);
        loginEventSecondAgent.setUserAdmin(AbstractPlusOCXTest.userAdmin);
        loginEventSecondAgent.setPasswordAdmin(AbstractPlusOCXTest.passAdmin);
        loginEventSecondAgent.setTerminalNumber(terminal);
        loginEventSecondAgent.setAgentNumber(agent);

        return loginEventSecondAgent;

    }

    @Test
    public void loginShouldReturnReadyMessage() throws Exception {

        initLogin();


    }

    @Test
    public void logoutShouldReturnReadyMessage() throws Exception {

        assertTrue(getPayloadReturnFromEvent("logout", new LogoutEvent()).contains("Logout OK"));

    }

}
