package br.com.plusoftomni.integration;

import br.com.plusoftomni.integration.domain.avaya.AvayaService;
import br.com.plusoftomni.integration.domain.avaya.handler.AvayaLoginHandler;
import br.com.plusoftomni.integration.domain.avaya.handler.AvayaMakeCallHandler;
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
public class HoldAndUnholdHandlerTest extends AbstractAvayaTest {

    @Autowired
    private CTIEventHandler handler;

    @Before
    public void init(){
        super.initLoginEvent();

        handler.dispatch(loginEvent);

    }

    @Test
    public void shouldReceiveALoginEvent() throws Exception{

        AvayaService avayaServiceSecondAgent = buildExtraAvayaService();
        AvayaLoginHandler avayaLoginHandler = new AvayaLoginHandler(avayaServiceSecondAgent, callbackDispatcher);
        avayaLoginHandler.execute(initLoginEventExtraService(AbstractAvayaTest.ramal2, AbstractAvayaTest.agente2));

        Thread.sleep(5000);

        AvayaMakeCallHandler avayaMakeCallHandler = new AvayaMakeCallHandler(avayaServiceSecondAgent, callbackDispatcher);
        avayaMakeCallHandler.execute(new MakeCallEvent(AbstractAvayaTest.ramal1));

        Thread.sleep(3000);

        handler.dispatch(new AnswerEvent());

        Thread.sleep(1000);

        handler.dispatch(new HoldEvent());

        Thread.sleep(1000);

        handler.dispatch(new UnHoldEvent());

        Thread.sleep(1000);

        handler.dispatch(new DropCallEvent());

    }

    @After
    public void destroy(){
        handler.dispatch(new LogoutEvent());
    }



}
