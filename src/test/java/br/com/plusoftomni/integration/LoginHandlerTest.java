package br.com.plusoftomni.integration;

import br.com.plusoftomni.integration.domain.telephonyplatform.CTIEventHandler;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.LogoutEvent;
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
public class LoginHandlerTest extends AbstractAvayaTest {

    @Autowired
    private CTIEventHandler handler;

    @Before
    public void init(){
        super.initLoginEvent();
    }

    @Test
    public void shouldReceiveALoginEvent(){

        handler.dispatch(loginEvent);

    }

    @After
    public void destroy(){
        handler.dispatch(new LogoutEvent());
    }

}
