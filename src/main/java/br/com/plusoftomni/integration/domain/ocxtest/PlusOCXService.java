package br.com.plusoftomni.integration.domain.ocxtest;

import br.com.plusoftomni.integration.domain.telephonyplatform.CallbackDispatcher;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by hermeswaldemarin on 15/12/15.
 */
@Service
public class PlusOCXService{

    private ActiveXComponent component;

    private CallbackDispatcher callbackDispatcher;

    public static ActiveXComponent activeXComponent;
    public static Dispatch realDispatcher;
    public static DispatchProxy proxyDispatcher;
    private static int N_THREADS = 1;
    private ExecutorService executor;
    private static Thread initialThread;

    @Autowired
    public PlusOCXService(CallbackDispatcher callbackDispatcher){
        try {

            executor = Executors.newFixedThreadPool(PlusOCXService.N_THREADS);

            if(initialThread != null){
                ComThread.quitMainSTA();
                initialThread.interrupt();
            }

            Runnable ocxInitial = new Runnable(){
                public void run(){
                    ComThread.InitMTA();

                    PlusOCXService.activeXComponent = new ActiveXComponent("plusActivexProj.plusActivex");

                    PlusOCXService.realDispatcher = (Dispatch)PlusOCXService.activeXComponent.getObject();

                    DispatchProxy eventDispatcher = generateProxy();

                    sleep(500);

                    Dispatch sc = eventDispatcher.toDispatch();

                    new DispatchEvents(sc, new PlusoftOCXListener(callbackDispatcher));

                    while(true){

                        if(PlusOCXService.proxyDispatcher == null){
                            PlusOCXService.proxyDispatcher = generateProxy();
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            initialThread = new Thread(ocxInitial);
            initialThread.start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect(){

    }

    private void sleep(long millis){
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static DispatchProxy generateProxy(){
        return new DispatchProxy(PlusOCXService.realDispatcher);
    }

    public Variant login(){

        Callable<Variant> task = () -> {
            Variant result = Dispatch.call(callInit(), "login");
            finishCall();
            return result;
        };

        Future<Variant> future = executor.submit(task);

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Variant logout(){
        Callable<Variant> task = () -> {
            Variant result = Dispatch.call(callInit(), "logout");
            finishCall();
            return result;
        };

        Future<Variant> future = executor.submit(task);

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Dispatch callInit(){
        while (PlusOCXService.proxyDispatcher == null){
            sleep(500);
        }
        return PlusOCXService.proxyDispatcher.toDispatch();
    }

    private void finishCall(){
        ComThread.Release();
        PlusOCXService.proxyDispatcher = null;
    }

}
