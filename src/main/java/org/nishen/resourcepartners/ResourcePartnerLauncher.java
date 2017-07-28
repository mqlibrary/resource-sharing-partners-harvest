package org.nishen.resourcepartners;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * This application is a framework for executing tasks. Add your custom tasks
 * and you can execute them via a command line interface.
 * 
 * <p>
 * This is a demo providing a couple of task examples.
 * 
 * @author nishen
 * 
 */
public class ResourcePartnerLauncher
{
	private static final Logger log = LoggerFactory.getLogger(ResourcePartnerLauncher.class);

	public static void main(String[] args)
	{
		// list for injector modules
		List<Module> modules = new ArrayList<Module>();

		// module (main configuration)
		modules.add(new ResourcePartnerModule());

		// create the injector
		log.debug("creating injector");
		Injector injector = Guice.createInjector(modules);

		// initialise the application object.
		log.debug("creating application");
		ResourcePartnerApp app = injector.getInstance(ResourcePartnerApp.class);

		// execute the application
		Calendar timeStart = Calendar.getInstance();
		log.info("executing: [{}]", timeStart.getTime());

		app.run();

		Calendar timeEnd = Calendar.getInstance();
		log.info("execution complete: [{}]", timeEnd.getTime());

		long diff = (timeEnd.getTimeInMillis() - timeStart.getTimeInMillis()) / 1000;
		log.info("time taken (seconds): {}", diff);
	}
}
