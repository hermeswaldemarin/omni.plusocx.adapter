package br.com.plusoftomni.integration;

import br.com.plusoftomni.integration.domain.avaya.AvayaService;
import br.com.plusoftomni.integration.domain.avaya.handler.*;
import br.com.plusoftomni.integration.domain.telephonyplatform.CTIEventHandler;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OmniIntegrationPlatformApplication.class)
public class TransferHandlerTest  extends AbstractAvayaTest {

    @Autowired
    private CTIEventHandler handler;

    @Before
    public void init() {
        super.initLoginEvent();

        handler.dispatch(loginEvent);

    }

    @Test
    public void shouldTransferAfterConsult() throws Exception {
        AvayaService avayaServiceSecondAgent = buildExtraAvayaService();
        AvayaLoginHandler avayaLoginHandler = new AvayaLoginHandler(avayaServiceSecondAgent, callbackDispatcher);
        avayaLoginHandler.execute(initLoginEventExtraService(AbstractAvayaTest.ramal2, AbstractAvayaTest.agente2));

        AvayaService userTwoService = buildExtraAvayaService();
        AvayaLoginHandler userTwoLoginHandler = new AvayaLoginHandler(userTwoService, callbackDispatcher);
        userTwoLoginHandler.execute(initLoginEventExtraService(AbstractAvayaTest.ramal3, AbstractAvayaTest.agente3));

        Thread.sleep(5000);

        AvayaMakeCallHandler avayaMakeCallHandler = new AvayaMakeCallHandler(avayaServiceSecondAgent, callbackDispatcher);
        avayaMakeCallHandler.execute(new MakeCallEvent(AbstractAvayaTest.ramal1));

        Thread.sleep(3000);

        handler.dispatch(new AnswerEvent());

        Thread.sleep(2000);

        handler.dispatch(new ConsultEvent(AbstractAvayaTest.ramal3));

        Thread.sleep(2000);

        AvayaAnswerHandler userTwoAnswerConsult = new AvayaAnswerHandler(userTwoService, callbackDispatcher);
        userTwoAnswerConsult.execute(new AnswerEvent());

        Thread.sleep(2000);

        handler.dispatch(new TransferEvent());

        Thread.sleep(2000);

        AvayaDropCallHandler userTwoDropTransfer = new AvayaDropCallHandler(userTwoService, callbackDispatcher);
        userTwoDropTransfer.execute(new DropCallEvent());

        Thread.sleep(2000);

        AvayaLogoutHandler userTwoLogout = new AvayaLogoutHandler(userTwoService, callbackDispatcher);
        userTwoLogout.execute(new LogoutEvent());

        Thread.sleep(2000);

        AvayaLogoutHandler userOneLogout = new AvayaLogoutHandler(avayaServiceSecondAgent, callbackDispatcher);
        userOneLogout.execute(new LogoutEvent());


    }

    @Test
    public void shouldBlindTransfer() throws Exception {
        AvayaService avayaServiceSecondAgent = buildExtraAvayaService();
        AvayaLoginHandler avayaLoginHandler = new AvayaLoginHandler(avayaServiceSecondAgent, callbackDispatcher);
        avayaLoginHandler.execute(initLoginEventExtraService(AbstractAvayaTest.ramal2, AbstractAvayaTest.agente2));

        AvayaService userTwoService = buildExtraAvayaService();
        AvayaLoginHandler userTwoLoginHandler = new AvayaLoginHandler(userTwoService, callbackDispatcher);
        userTwoLoginHandler.execute(initLoginEventExtraService(AbstractAvayaTest.ramal3, AbstractAvayaTest.agente3));

        Thread.sleep(5000);

        AvayaMakeCallHandler avayaMakeCallHandler = new AvayaMakeCallHandler(avayaServiceSecondAgent, callbackDispatcher);
        avayaMakeCallHandler.execute(new MakeCallEvent(AbstractAvayaTest.ramal1));

        Thread.sleep(2000);

        handler.dispatch(new AnswerEvent());

        Thread.sleep(2000);

        handler.dispatch(new TransferEvent("", AbstractAvayaTest.ramal3));

        Thread.sleep(2000);

        AvayaAnswerHandler userTwoAnswerConsult = new AvayaAnswerHandler(userTwoService, callbackDispatcher);
        userTwoAnswerConsult.execute(new AnswerEvent());

        Thread.sleep(2000);

        handler.dispatch(new DropCallEvent());

        Thread.sleep(2000);

        AvayaLogoutHandler userTwoLogout = new AvayaLogoutHandler(userTwoService, callbackDispatcher);
        userTwoLogout.execute(new LogoutEvent());

        Thread.sleep(2000);

        AvayaLogoutHandler userOneLogout = new AvayaLogoutHandler(avayaServiceSecondAgent, callbackDispatcher);
        userOneLogout.execute(new LogoutEvent());


    }

    @After
    public void destroy() {
        handler.dispatch(new LogoutEvent());
    }
}
