package br.com.plusoftomni.integration;

import br.com.plusoftomni.integration.application.telephonyplatform.AbstractTelephonyPlatformTest;
import br.com.plusoftomni.integration.domain.avaya.AvayaService;
import br.com.plusoftomni.integration.domain.avaya.handler.*;
import br.com.plusoftomni.integration.domain.avaya.listener.AvayaAgentListener;
import br.com.plusoftomni.integration.domain.avaya.listener.AvayaCallControlTerminalConnectionListener;
import br.com.plusoftomni.integration.domain.avaya.listener.AvayaProviderListener;
import br.com.plusoftomni.integration.domain.telephonyplatform.CallbackDispatcher;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.*;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AvayaImplItegrationTests extends AbstractTelephonyPlatformTest {

    public AvayaImplItegrationTests(){
        this.activeProfile = "";
        this.TIMEOUT = 10;
    }

    private void initLogin() throws Exception {
        LoginEvent loginEvent = new LoginEvent();
        loginEvent.setServiceName(AbstractAvayaTest.serviceName);
        loginEvent.setUserAdmin(AbstractAvayaTest.userAdmin);
        loginEvent.setPasswordAdmin(AbstractAvayaTest.passAdmin);
        loginEvent.setTerminalNumber(AbstractAvayaTest.ramal1);
        loginEvent.setAgentNumber(AbstractAvayaTest.agente1);

        assertTrue(getPayloadReturnFromEvent("login", loginEvent).contains("READY"));
    }

    protected AvayaService buildExtraAvayaService(){
        AvayaService service = new AvayaService();

        service.setAvayaCallControlTerminalConnectionListener(new AvayaCallControlTerminalConnectionListener(service));
        service.setAvayaProviderListener(new AvayaProviderListener(service));
        service.setAvayaAgentListener(new AvayaAgentListener(service));

        service.setCallbackDispatcher(new CallbackDispatcher());

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

    @Test
    public void loginShouldReturnReadyMessage() throws Exception {

        initLogin();

    }

    @Test
    public void makeCallShouldReturnOKMessage() throws Exception {

        initLogin();

        this.TIMEOUT = 3;

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("MAKECALL", new MakeCallEvent(AbstractAvayaTest.ramal2)).contains("MESSAGE"));

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("DROPCALL", new DropCallEvent()).contains("MESSAGE"));

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("logout", new LogoutEvent()).contains("MESSAGE"));
    }

    @Test
    public void answerShouldReturnOKMessage() throws Exception {

        initLogin();

        AvayaService avayaServiceSecondAgent = buildExtraAvayaService();
        AvayaLoginHandler avayaLoginHandler = new AvayaLoginHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaLoginHandler.execute(initLoginEventExtraService(AbstractAvayaTest.ramal2, AbstractAvayaTest.agente2));

        Thread.sleep(3000);

        AvayaMakeCallHandler avayaMakeCallHandler = new AvayaMakeCallHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaMakeCallHandler.execute(new MakeCallEvent(AbstractAvayaTest.ramal1));

        this.TIMEOUT = 3;

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("ANSWER", new AnswerEvent()).contains("MESSAGE"));

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("DROPCALL", new DropCallEvent()).contains("MESSAGE"));

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("logout", new LogoutEvent()).contains("MESSAGE"));
    }

    @Test
    public void holdAndUnholShouldReturnOKMessage() throws Exception {

        initLogin();

        AvayaService avayaServiceSecondAgent = buildExtraAvayaService();
        AvayaLoginHandler avayaLoginHandler = new AvayaLoginHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaLoginHandler.execute(initLoginEventExtraService(AbstractAvayaTest.ramal2, AbstractAvayaTest.agente2));

        Thread.sleep(3000);

        AvayaMakeCallHandler avayaMakeCallHandler = new AvayaMakeCallHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaMakeCallHandler.execute(new MakeCallEvent(AbstractAvayaTest.ramal1));

        this.TIMEOUT = 3;

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("ANSWER", new AnswerEvent()).contains("MESSAGE"));

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("HOLD", new HoldEvent()).contains("MESSAGE"));

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("UNHOLD", new UnHoldEvent()).contains("MESSAGE"));

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("DROPCALL", new DropCallEvent()).contains("MESSAGE"));

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("logout", new LogoutEvent()).contains("MESSAGE"));
    }

    @Test
    public void consultAndCancelConsultShouldReturnOKMessage() throws Exception {

        initLogin();

        AvayaService userOneService = buildExtraAvayaService();
        AvayaLoginHandler userOneLoginHandler = new AvayaLoginHandler(userOneService, new CallbackDispatcher());
        userOneLoginHandler.execute(initLoginEventExtraService(AbstractAvayaTest.ramal2, AbstractAvayaTest.agente2));

        Thread.sleep(3000);

        AvayaService userTwoService = buildExtraAvayaService();
        AvayaLoginHandler userTwoLoginHandler = new AvayaLoginHandler(userTwoService, new CallbackDispatcher());
        userTwoLoginHandler.execute(initLoginEventExtraService(AbstractAvayaTest.ramal3, AbstractAvayaTest.agente3));

        Thread.sleep(3000);

        AvayaMakeCallHandler userOneMakeCallHandler = new AvayaMakeCallHandler(userOneService, new CallbackDispatcher());
        userOneMakeCallHandler.execute(new MakeCallEvent(AbstractAvayaTest.ramal1));

        this.TIMEOUT = 3;

        Thread.sleep(2000);

        assertTrue(getPayloadReturnFromEvent("ANSWER", new AnswerEvent()).contains("MESSAGE"));

        Thread.sleep(1000);

        assertTrue(getPayloadReturnFromEvent("CONSULT", new ConsultEvent(AbstractAvayaTest.ramal3)).contains("MESSAGE"));

        Thread.sleep(1000);

        AvayaAnswerHandler userTwoAnswerCallHandler = new AvayaAnswerHandler(userTwoService, new CallbackDispatcher());
        userTwoAnswerCallHandler.execute(new AnswerEvent());

        Thread.sleep(1000);

        assertTrue(getPayloadReturnFromEvent("CANCELCONSULT", new CancelConsultEvent()).contains("MESSAGE"));

        Thread.sleep(1000);

        assertTrue(getPayloadReturnFromEvent("DROPCALL", new DropCallEvent()).contains("MESSAGE"));

        Thread.sleep(1500);

        assertTrue(getPayloadReturnFromEvent("logout", new LogoutEvent()).contains("MESSAGE"));
    }

    @Test
    public void transferAfterConsultShouldReturnOKMessage() throws Exception {

        initLogin();

        AvayaService avayaServiceSecondAgent = buildExtraAvayaService();
        AvayaLoginHandler avayaLoginHandler = new AvayaLoginHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaLoginHandler.execute(initLoginEventExtraService(AbstractAvayaTest.ramal2, AbstractAvayaTest.agente2));

        Thread.sleep(3000);

        AvayaService userTwoService = buildExtraAvayaService();
        AvayaLoginHandler userTwoLoginHandler = new AvayaLoginHandler(userTwoService, new CallbackDispatcher());
        userTwoLoginHandler.execute(initLoginEventExtraService(AbstractAvayaTest.ramal3, AbstractAvayaTest.agente3));

        Thread.sleep(3000);

        AvayaMakeCallHandler avayaMakeCallHandler = new AvayaMakeCallHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaMakeCallHandler.execute(new MakeCallEvent(AbstractAvayaTest.ramal1));

        Thread.sleep(3000);

        assertTrue(getPayloadReturnFromEvent("ANSWER", new AnswerEvent()).contains("MESSAGE"));

        Thread.sleep(2000);

        assertTrue(getPayloadReturnFromEvent("CONSULT", new ConsultEvent(AbstractAvayaTest.ramal3)).contains("MESSAGE"));

        Thread.sleep(2000);

        AvayaAnswerHandler userTwoAnswerConsult = new AvayaAnswerHandler(userTwoService, new CallbackDispatcher());
        userTwoAnswerConsult.execute(new AnswerEvent());

        Thread.sleep(2000);


        assertTrue(getPayloadReturnFromEvent("TRANSFER", new TransferEvent()).contains("MESSAGE"));

        Thread.sleep(2000);

        AvayaDropCallHandler userTwoDropTransfer = new AvayaDropCallHandler(userTwoService, new CallbackDispatcher());
        userTwoDropTransfer.execute(new DropCallEvent());

        Thread.sleep(2000);

        AvayaLogoutHandler userTwoLogout = new AvayaLogoutHandler(userTwoService, new CallbackDispatcher());
        userTwoLogout.execute(new LogoutEvent());

        Thread.sleep(2000);

        AvayaLogoutHandler userOneLogout = new AvayaLogoutHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        userOneLogout.execute(new LogoutEvent());

        Thread.sleep(2000);

        assertTrue(getPayloadReturnFromEvent("logout", new LogoutEvent()).contains("MESSAGE"));
    }

    @Test
    public void blindTransferShouldReturnOKMessage() throws Exception {

        initLogin();

        AvayaService avayaServiceSecondAgent = buildExtraAvayaService();
        AvayaLoginHandler avayaLoginHandler = new AvayaLoginHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaLoginHandler.execute(initLoginEventExtraService(AbstractAvayaTest.ramal2, AbstractAvayaTest.agente2));

        Thread.sleep(3000);

        AvayaService userTwoService = buildExtraAvayaService();
        AvayaLoginHandler userTwoLoginHandler = new AvayaLoginHandler(userTwoService, new CallbackDispatcher());
        userTwoLoginHandler.execute(initLoginEventExtraService(AbstractAvayaTest.ramal3, AbstractAvayaTest.agente3));

        Thread.sleep(3000);

        AvayaMakeCallHandler avayaMakeCallHandler = new AvayaMakeCallHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaMakeCallHandler.execute(new MakeCallEvent(AbstractAvayaTest.ramal1));


        Thread.sleep(3000);

        assertTrue(getPayloadReturnFromEvent("ANSWER", new AnswerEvent()).contains("MESSAGE"));

        Thread.sleep(1000);

        assertTrue(getPayloadReturnFromEvent("TRANSFER", new TransferEvent("", AbstractAvayaTest.ramal3)).contains("MESSAGE"));

        Thread.sleep(1000);

        AvayaAnswerHandler userTwoAnswerConsult = new AvayaAnswerHandler(userTwoService, new CallbackDispatcher());
        userTwoAnswerConsult.execute(new AnswerEvent());

        Thread.sleep(1000);

        AvayaDropCallHandler userTwoDropTransfer = new AvayaDropCallHandler(userTwoService, new CallbackDispatcher());
        userTwoDropTransfer.execute(new DropCallEvent());

        Thread.sleep(1000);

        assertTrue(getPayloadReturnFromEvent("DROPCALL", new DropCallEvent()).contains("MESSAGE"));

        Thread.sleep(1000);

        AvayaLogoutHandler userTwoLogout = new AvayaLogoutHandler(userTwoService, new CallbackDispatcher());
        userTwoLogout.execute(new LogoutEvent());

        Thread.sleep(1000);

        AvayaLogoutHandler userOneLogout = new AvayaLogoutHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        userOneLogout.execute(new LogoutEvent());

        Thread.sleep(1000);

        assertTrue(getPayloadReturnFromEvent("logout", new LogoutEvent()).contains("MESSAGE"));

    }

    @Test
    public void conferenceShouldReturnOKMessage() throws Exception {

        initLogin();

        Thread.sleep(3000);

        AvayaService avayaServiceSecondAgent = buildExtraAvayaService();
        AvayaLoginHandler avayaLoginHandler = new AvayaLoginHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaLoginHandler.execute(initLoginEventExtraService(AbstractAvayaTest.ramal2, AbstractAvayaTest.agente2));

        Thread.sleep(3000);

        AvayaService userTwoService = buildExtraAvayaService();
        AvayaLoginHandler userTwoLoginHandler = new AvayaLoginHandler(userTwoService, new CallbackDispatcher());
        userTwoLoginHandler.execute(initLoginEventExtraService(AbstractAvayaTest.ramal3, AbstractAvayaTest.agente3));

        Thread.sleep(3000);

        AvayaMakeCallHandler avayaMakeCallHandler = new AvayaMakeCallHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        avayaMakeCallHandler.execute(new MakeCallEvent(AbstractAvayaTest.ramal1));

        Thread.sleep(3000);

        assertTrue(getPayloadReturnFromEvent("ANSWER", new AnswerEvent()).contains("MESSAGE"));

        Thread.sleep(2000);

        assertTrue(getPayloadReturnFromEvent("CONFERENCE", new ConferenceEvent(AbstractAvayaTest.ramal3)).contains("MESSAGE"));

        Thread.sleep(2000);

        AvayaAnswerHandler userTwoAnswer = new AvayaAnswerHandler(userTwoService, new CallbackDispatcher());
        userTwoAnswer.execute(new AnswerEvent());

        Thread.sleep(2000);

        AvayaDropCallHandler userTwoDropTransfer = new AvayaDropCallHandler(userTwoService, new CallbackDispatcher());
        userTwoDropTransfer.execute(new DropCallEvent());

        Thread.sleep(2000);

        assertTrue(getPayloadReturnFromEvent("DROPCALL", new DropCallEvent()).contains("MESSAGE"));


        Thread.sleep(2000);

        AvayaDropCallHandler userOneDropTransfer = new AvayaDropCallHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        userOneDropTransfer.execute(new DropCallEvent());


        Thread.sleep(2000);

        AvayaLogoutHandler userTwoLogout = new AvayaLogoutHandler(userTwoService, new CallbackDispatcher());
        userTwoLogout.execute(new LogoutEvent());

        Thread.sleep(2000);

        AvayaLogoutHandler userOneLogout = new AvayaLogoutHandler(avayaServiceSecondAgent, new CallbackDispatcher());
        userOneLogout.execute(new LogoutEvent());

        Thread.sleep(2000);

        assertTrue(getPayloadReturnFromEvent("logout", new LogoutEvent()).contains("MESSAGE"));

    }

}
