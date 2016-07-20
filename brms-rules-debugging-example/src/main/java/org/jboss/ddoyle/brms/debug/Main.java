package org.jboss.ddoyle.brms.debug;

import org.jboss.ddoyle.brms.debug.listeners.DebugAgendaEventListener;
import org.jboss.ddoyle.brms.debug.model.Applicant;
import org.jboss.ddoyle.brms.debug.model.Loan;
import org.kie.api.KieServices;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		KieServices kieServices = KieServices.Factory.get();
		KieContainer kieContainer = kieServices.newKieClasspathContainer();
		
		KieSession kieSession = kieContainer.newKieSession();
		LOGGER.info("Creating new session.");
		//Configure the KieRuntimeLogger which will provide the logging we can use in the Eclipse Drools Audit view.
		KieRuntimeLogger kieLogger = kieServices.getLoggers().newFileLogger(kieSession, "audit");
		LOGGER.info("Creating new Logger.");
		try {
			//Configure the SLF4J Logger. We will use this logger in the RHS of our rules.
			Logger rulesLogger = LoggerFactory.getLogger("RulesLogger");
			kieSession.setGlobal("logger", rulesLogger);
			
			//Add event listener to session.
			kieSession.addEventListener(new DebugAgendaEventListener());
			
			//Insert our facts.
			kieSession.insert(getApplicant());
			Loan loan = getLoan();
			kieSession.insert(loan);
			
			//Fire the rules.
			kieSession.fireAllRules();
			
			//Print result
			LOGGER.info("Lone approved?: " + loan.isApproved());
		} finally {
			LOGGER.info("Disposing session.");
			kieSession.dispose();
			kieLogger.close();
		}
	}
	
	private static Applicant getApplicant() {
		Applicant applicant = new Applicant();
		
		applicant.setCreditScore(300);
		applicant.setName("Duncan Doyle");
		return applicant;
	}
	
	private static Loan getLoan(){
		Loan loan = new Loan();
		loan.setAmount(3000);
		//loan.setApproved(true);
		loan.setDuration(10);
		loan.setInterestRate(5.00);
		return loan;
	}
	
	
	
	
}
