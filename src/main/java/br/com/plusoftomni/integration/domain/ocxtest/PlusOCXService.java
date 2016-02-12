package br.com.plusoftomni.integration.domain.ocxtest;

import ezjcom.JComException;
import org.springframework.stereotype.Service;

/**
 * Created by hermeswaldemarin on 15/12/15.
 */
@Service
public class PlusOCXService {

    private _plusActivex plusActivex;

    public PlusOCXService(){
        try {
            this.plusActivex = new plusActivex().get_plusActivex();
        } catch (JComException e) {
            e.printStackTrace();
        }
    }

    public _plusActivex getPlusActivex() {
        return plusActivex;
    }

    public void setPlusActivex(_plusActivex plusActivex) {
        this.plusActivex = plusActivex;
    }
}
