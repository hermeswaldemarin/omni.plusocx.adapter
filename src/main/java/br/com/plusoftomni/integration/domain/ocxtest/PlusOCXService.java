package br.com.plusoftomni.integration.domain.ocxtest;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.*;
import ezjcom.JComException;
import org.springframework.stereotype.Service;

/**
 * Created by hermeswaldemarin on 15/12/15.
 */
@Service
public class PlusOCXService {

    public PlusOCXService(){
        try {
            //
            // this.plusActivex = new plusActivex().get_plusActivex();

            ComThread.InitSTA();
            //PlusoftOCX script = new PlusoftOCX();
           // Thread.sleep(1000);

            // get a thread-local Dispatch from sCon
            //Dispatch sc = PlusoftOCX.sCon.toDispatch();

            //Variant result = Dispatch.call(sc, "login" );
           // System.out.println("eval(login) = "+ result);

            Dispatch test = new ActiveXComponent("plusActivexProj.plusActivex");

            new DispatchEvents(test, new PlusoftOCXListener());


            Variant result = Dispatch.call(test, "login");

            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
