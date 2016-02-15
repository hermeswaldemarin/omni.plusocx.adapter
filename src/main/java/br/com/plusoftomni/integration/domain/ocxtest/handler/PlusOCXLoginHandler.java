package br.com.plusoftomni.integration.domain.ocxtest.handler;

import br.com.plusoftomni.integration.domain.ocxtest.PlusOCXService;
import br.com.plusoftomni.integration.domain.ocxtest.PlusoftOCXListener;
import br.com.plusoftomni.integration.domain.telephonyplatform.CTIResponse;
import br.com.plusoftomni.integration.domain.telephonyplatform.CallbackDispatcher;
import br.com.plusoftomni.integration.domain.telephonyplatform.event.LoginEvent;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.CTIEvents;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.EventHandler;
import br.com.plusoftomni.integration.infrastructure.telephonyplatform.annotation.Handle;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.DispatchEvents;
import com.jacob.com.Variant;
import ezjcom.JComException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by hermeswaldemarin on 14/12/15.
 */
@EventHandler
public class PlusOCXLoginHandler {

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

        try {


            ComThread.InitSTA();

            Thread.sleep(1000);

            Dispatch sc = plusOCXService.getComponent().toDispatch();

            new DispatchEvents(sc, new PlusoftOCXListener(this.callbackDispatcher));

            Variant result = Dispatch.call(sc, "login");

            System.out.println(result);



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
