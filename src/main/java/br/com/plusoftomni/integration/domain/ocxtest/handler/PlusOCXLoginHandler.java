package br.com.plusoftomni.integration.domain.ocxtest.handler;

import br.com.plusoftomni.integration.domain.ocxtest.PlusOCXService;
import br.com.plusoftomni.integration.domain.telephonyplatform.CallbackDispatcher;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.LoginEvent;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.CTIEvents;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.EventHandler;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.Handle;
import com.jacob.com.STA;
import com.jacob.com.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@EventHandler
public class PlusOCXLoginHandler extends STA{

    CallbackDispatcher callbackDispatcher;

    private PlusOCXService plusOCXService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public PlusOCXLoginHandler(PlusOCXService plusOCXService, CallbackDispatcher callbackDispatcher){
        this.plusOCXService = plusOCXService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Handle(CTIEvents.LOGIN)
    public void execute(LoginEvent event){

        Variant result = plusOCXService.login();

    }

}
