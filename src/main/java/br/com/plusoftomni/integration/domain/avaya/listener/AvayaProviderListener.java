package br.com.plusoftomni.integration.domain.avaya.listener;

import br.com.plusoftomni.integration.domain.avaya.AvayaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.telephony.ProviderEvent;
import javax.telephony.ProviderListener;

@Service
@Lazy
public class AvayaProviderListener implements ProviderListener {

    private AvayaService avayaService;
    private Object sigProvider;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AvayaProviderListener(AvayaService avayaService){
        this.avayaService = avayaService;
    }

	public void providerEventTransmissionEnded(ProviderEvent e) {
        logger.trace("providerEventTransmissionEnded");
	}

	
	public void providerInService(ProviderEvent e) {
        synchronized (sigProvider) {
            sigProvider.notify();
        }
        logger.info("providerInService");
	}

	
	public void providerOutOfService(ProviderEvent e) {
        logger.trace("providerOutOfService");
	}

	
	public void providerShutdown(ProviderEvent e) {
        logger.trace("providerOutOfService");
	}

    public Object getSigProvider() {
        return sigProvider;
    }

    public void setSigProvider(Object sigProvider) {
        this.sigProvider = sigProvider;
    }
}
