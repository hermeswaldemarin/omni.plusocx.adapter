package br.com.plusoftomni.integration;

import br.com.plusoftomni.integration.domain.telephonyplatform.CTIEventHandler;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.DropCallEvent;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.LogoutEvent;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.MakeCallEvent;
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
public class DropCallHandlerTest extends AbstractAvayaTest{

    @Autowired
    private CTIEventHandler handler;

    @Before
    public void init(){

        super.initLoginEvent();

        handler.dispatch(loginEvent);

    }

    @Test
    public void shouldReceiveALoginEvent() throws Exception{

        Thread.sleep(2000);

        handler.dispatch(new MakeCallEvent(AbstractAvayaTest.ramal2));

        Thread.sleep(2000);

        handler.dispatch(new DropCallEvent());

    }

    @After
    public void destroy(){
        handler.dispatch(new LogoutEvent());
    }

}
