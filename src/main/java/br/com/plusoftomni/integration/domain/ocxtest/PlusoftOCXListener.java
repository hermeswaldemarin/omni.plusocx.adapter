package br.com.plusoftomni.integration.domain.ocxtest;

import br.com.plusoftomni.integration.domain.telephonyplatform.CTIResponse;
import br.com.plusoftomni.integration.domain.telephonyplatform.CallbackDispatcher;
import com.jacob.com.Dispatch;
import com.jacob.com.EnumVariant;
import com.jacob.com.Variant;
import com.jacob.com.VariantViaEvent;
import org.aspectj.weaver.ast.Var;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by hermeswaldemarin on 12/02/16.
 */
public class PlusoftOCXListener {

    CallbackDispatcher callbackDispatcher;

    public PlusoftOCXListener(CallbackDispatcher callbackDispatcher){
        this.callbackDispatcher = callbackDispatcher;
    }

    public void OnChange(Variant[] args){


        if(args[0].toJavaObject().toString().indexOf("login") > -1){

            String agentName = "test";

            if(callbackDispatcher != null){
                callbackDispatcher.dispatch(new CTIResponse("login", 0, "Login OK", Collections.unmodifiableMap(Stream.of(
                        new AbstractMap.SimpleEntry<>("agentName", agentName!=null?agentName:"" ))
                        .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())))));
            }else{
                System.out.println("Login OK");
            }



        }else if(args[0].toJavaObject().toString().indexOf("logout") > -1){

            if(callbackDispatcher != null){
                callbackDispatcher.dispatch(new CTIResponse("logout", 0, "Logout OK", Collections.unmodifiableMap(Stream.of(
                        new AbstractMap.SimpleEntry<>("arg1", "one"),
                        new AbstractMap.SimpleEntry<>("arg2", "two"))
                        .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())))));
            }else{
                System.out.println("Logout OK");
            }


        }

    }
}
