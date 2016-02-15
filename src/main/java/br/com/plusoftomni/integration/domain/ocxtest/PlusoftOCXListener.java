package br.com.plusoftomni.integration.domain.ocxtest;

import br.com.plusoftomni.integration.domain.telephonyplatform.CTIResponse;
import br.com.plusoftomni.integration.domain.telephonyplatform.CallbackDispatcher;
import com.jacob.com.Variant;

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

        if(args[0].getString().indexOf("login") > -1){

            String agentName = "test";

            callbackDispatcher.dispatch(new CTIResponse("login", 0, "Login OK", Collections.unmodifiableMap(Stream.of(
                    new AbstractMap.SimpleEntry<>("agentName", agentName!=null?agentName:"" ))
                    .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())))));

        }else if(args[0].getString().indexOf("logout") > -1){

            callbackDispatcher.dispatch(new CTIResponse("logout", 0, "Logout OK", Collections.unmodifiableMap(Stream.of(
                    new AbstractMap.SimpleEntry<>("arg1", "one"),
                    new AbstractMap.SimpleEntry<>("arg2", "two"))
                    .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())))));
        }

        System.out.println("here");
    }
}
