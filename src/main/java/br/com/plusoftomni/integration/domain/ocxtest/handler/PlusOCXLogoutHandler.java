package br.com.plusoftomni.integration.domain.ocxtest.handler;

import br.com.plusoftomni.integration.domain.ocxtest.PlusOCXService;
import br.com.plusoftomni.integration.domain.telephonyplatform.CallbackDispatcher;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.LogoutEvent;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.CTIEvents;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.EventHandler;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.Handle;
import com.jacob.com.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@EventHandler
public class PlusOCXLogoutHandler {

    CallbackDispatcher callbackDispatcher;

    private PlusOCXService plusOCXService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public PlusOCXLogoutHandler(PlusOCXService plusOCXService, CallbackDispatcher callbackDispatcher){
        this.plusOCXService = plusOCXService;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Handle(CTIEvents.LOGOUT)
    public void execute(LogoutEvent event){

        Variant result = plusOCXService.logout();

    }
}
