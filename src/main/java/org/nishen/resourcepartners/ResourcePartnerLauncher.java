package org.nishen.resourcepartners;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * @author nishen
 * 
 */
public class ResourcePartnerLauncher
{
	private static final Logger log = LoggerFactory.getLogger(ResourcePartnerLauncher.class);

	private static Map<String, String> options;

	public static void main(String[] args)
	{
		try
		{
			options = parseArgs(args);

			if (options.containsKey("h"))
			{
				printUsage();
				return;
			}
		}
		catch (Exception e)
		{
			log.error("invalid command line: {}", e.getMessage());
			return;
		}

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

		app.run(options);

		Calendar timeEnd = Calendar.getInstance();
		log.info("execution complete: [{}]", timeEnd.getTime());

		long diff = (timeEnd.getTimeInMillis() - timeStart.getTimeInMillis()) / 1000;
		log.info("time taken (seconds): {}", diff);
	}

	private static Map<String, String> parseArgs(String[] args) throws Exception
	{
		Map<String, String> options = new HashMap<String, String>();

		if (args.length == 0)
		{
			return options;
		}

		for (int x = 0; x < args.length; x++)
		{
			if (args[x].equals("-h"))
			{
				options.put("h", "true");
			}
			else if (args[x].equals("-?"))
			{
				options.put("h", "true");
			}
			else if (args[x].equals("-harvesters"))
			{
				if (args.length > (x + 1))
				{
					options.put("harvesters", args[++x].toUpperCase());
				}
				else
				{
					throw new Exception("list of harvesters required with -harvesters parameter.");
				}
			}
			else
			{
				throw new Exception("unknown option: " + args[x]);
			}
		}

		return options;
	}

	private static void printUsage()
	{
		System.out.println("java -jar resource-partner-sharing-harvest-x.y.z.jar [options]");
		System.out.println("  -h                                        help");
		System.out.println("  -?                                        help");
		System.out.println("  -harvesters harvester1,hearvester2,...    allows selection of harvesters to run by specifying");
		System.out.println("                                            a comma separated list: [LADD,ILRS,TEPUNA,OUTLOOK]");
	}
}
