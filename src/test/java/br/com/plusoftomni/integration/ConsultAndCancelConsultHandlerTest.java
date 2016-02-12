package br.com.plusoftomni.integration;

import br.com.plusoftomni.integration.domain.avaya.AvayaService;
import br.com.plusoftomni.integration.domain.avaya.handler.AvayaAnswerHandler;
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
public class ConsultAndCancelConsultHandlerTest extends AbstractAvayaTest{

    @Autowired
    private CTIEventHandler handler;

    @Before
    public void init(){
        super.initLoginEvent();

        handler.dispatch(loginEvent);

    }

    @Test
    public void shouldReceiveALoginEvent() throws Exception{

        AvayaService userOneService = buildExtraAvayaService();
        AvayaLoginHandler userOneLoginHandler = new AvayaLoginHandler(userOneService, callbackDispatcher);
        userOneLoginHandler.execute(initLoginEventExtraService(AbstractAvayaTest.ramal2, AbstractAvayaTest.agente2));

        AvayaService userTwoService = buildExtraAvayaService();
        AvayaLoginHandler userTwoLoginHandler = new AvayaLoginHandler(userTwoService, callbackDispatcher);
        userTwoLoginHandler.execute(initLoginEventExtraService(AbstractAvayaTest.ramal3, AbstractAvayaTest.agente3));

        Thread.sleep(5000);

        AvayaMakeCallHandler userOneMakeCallHandler = new AvayaMakeCallHandler(userOneService, callbackDispatcher);
        userOneMakeCallHandler.execute(new MakeCallEvent(AbstractAvayaTest.ramal1));

        Thread.sleep(2000);

        handler.dispatch(new AnswerEvent());

        Thread.sleep(1000);

        handler.dispatch(new ConsultEvent(AbstractAvayaTest.ramal3));

        Thread.sleep(1000);

        AvayaAnswerHandler userTwoAnswerCallHandler = new AvayaAnswerHandler(userTwoService, callbackDispatcher);
        userTwoAnswerCallHandler.execute(new AnswerEvent());

        Thread.sleep(1000);

        handler.dispatch(new CancelConsultEvent());

        Thread.sleep(1000);

        handler.dispatch(new DropCallEvent());

    }

    @After
    public void destroy(){
        handler.dispatch(new LogoutEvent());
    }

}
