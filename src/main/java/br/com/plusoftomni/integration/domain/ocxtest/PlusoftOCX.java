package br.com.plusoftomni.integration.domain.ocxtest;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.DispatchProxy;
import com.jacob.com.STA;

/**
 * Created by hermeswaldemarin on 12/02/16.
 */
public class PlusoftOCX extends STA {

    public static ActiveXComponent sC;
    public static Dispatch sControl = null;
    public static DispatchProxy sCon = null;

    public boolean OnInit()
    {
        try
        {
            System.out.println("OnInit");
            System.out.println(Thread.currentThread());
            String lang = "VBScript";

            sC = new ActiveXComponent("plusActivexProj.plusActivex");
            sControl = (Dispatch)sC.getObject();

            // sCon can be called from another thread
            sCon = new DispatchProxy(sControl);

            //Dispatch.put(sControl, "Language", lang);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public void OnQuit()
    {
        System.out.println("OnQuit");
    }

}
