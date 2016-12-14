package com.sample.drools;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.sample.drools.bean.Message;

/**
 * This is a sample class to launch a rule.
 */
public class DroolsTest {

    public static final void main(String[] args) {
        try {
            // load up the knowledge base
	        KieServices ks = KieServices.Factory.get();
    	    KieContainer kContainer = ks.getKieClasspathContainer();
    	    KieSession kSession = kContainer.newKieSession("ksession-rules");

            // go !
    	    long startTime = System.currentTimeMillis();
            for(int i=0;i<100;i++){
//            	System.out.println( "******************** "+i+" ********************" );
            	Message message = new Message();
            	message.setMessage("º¼ÖÝÊÐHello World i="+i);
            	message.setFlag(i%2);
            	message.setStatus(Message.HELLO);
            	kSession.insert(message);
            	kSession.fireAllRules();
            }
            kSession.dispose();
            System.out.println(System.currentTimeMillis() - startTime + "ms");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}