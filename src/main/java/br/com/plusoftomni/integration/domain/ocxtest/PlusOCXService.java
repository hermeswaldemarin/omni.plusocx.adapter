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

    private DispatchProxy component;

    public PlusOCXService(){
        try {


            //PlusoftOCX script = new PlusoftOCX();
           // Thread.sleep(1000);

            // get a thread-local Dispatch from sCon
            //Dispatch sc = PlusoftOCX.sCon.toDispatch();

            //Variant result = Dispatch.call(sc, "login" );
           // System.out.println("eval(login) = "+ result);

            Dispatch realComponent = new ActiveXComponent("plusActivexProj.plusActivex");

            component = new DispatchProxy(realComponent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DispatchProxy getComponent(){
        return component;
    }

}
