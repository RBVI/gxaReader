package edu.ucsf.rbvi.gxaReader.internal;

import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.ID;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_BEFORE;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.io.BasicCyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskFactory;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ucsf.rbvi.gxaReader.internal.model.MTXManager;
import edu.ucsf.rbvi.gxaReader.internal.model.GXAManager;
import edu.ucsf.rbvi.gxaReader.internal.tasks.GXACreateTableTaskFactory;
import edu.ucsf.rbvi.gxaReader.internal.tasks.GXAFetchTaskFactory;
import edu.ucsf.rbvi.gxaReader.internal.tasks.GXAShowResultsTaskFactory;
import edu.ucsf.rbvi.gxaReader.internal.tasks.MTXCreateTableTaskFactory;
import edu.ucsf.rbvi.gxaReader.internal.tasks.MTXGetValueTaskFactory;
import edu.ucsf.rbvi.gxaReader.internal.tasks.MTXReaderTaskFactory;

public class CyActivator extends AbstractCyActivator {

	public CyActivator() {
		super();
	}

	public void start(BundleContext bc) {
		final StreamUtil streamUtil = getService(bc, StreamUtil.class);
		final CyServiceRegistrar serviceRegistrar = getService(bc, CyServiceRegistrar.class);

		final MTXManager mtxManager = new MTXManager(serviceRegistrar);
		final GXAManager gxaManager = new GXAManager(serviceRegistrar, mtxManager);

		{
			// This is for the basic reader.  Note that we'll also load a more advanced one below
			final BasicCyFileFilter mtxFileFilter = new BasicCyFileFilter(new String[] { "mtx" },
			                              new String[] { "application/mtx" }, "MTX", DataCategory.TABLE, streamUtil);
			final MTXReaderTaskFactory mtxReaderFactory = new MTXReaderTaskFactory(mtxFileFilter, mtxManager);
	
			Properties mtxReaderProps = new Properties();
			mtxReaderProps.put(ID, "mtxTableReaderFactory");
			registerService(bc, mtxReaderFactory, InputStreamTaskFactory.class, mtxReaderProps);
	
			Properties mtxImporterProps = new Properties();
			mtxImporterProps.setProperty(PREFERRED_MENU, "Apps.MTXImporter");
			mtxImporterProps.setProperty(TITLE, "Import MTX files");
			registerService(bc, mtxReaderFactory, TaskFactory.class, mtxImporterProps);
		}

		{
			final MTXCreateTableTaskFactory mtxCreateTableFactory = new MTXCreateTableTaskFactory(mtxManager);
			Properties mtxTableProps = new Properties();
			mtxTableProps.setProperty(PREFERRED_MENU, "Apps.MTXImporter");
			mtxTableProps.setProperty(TITLE, "Create table from MTX file");
			registerService(bc, mtxCreateTableFactory, TaskFactory.class, mtxTableProps);
		}

		{
			final MTXGetValueTaskFactory mtxGetValueTaskFactory = new MTXGetValueTaskFactory(mtxManager);
			Properties mtxGetValueProps = new Properties();
			mtxGetValueProps.setProperty(PREFERRED_MENU, "Apps.MTXImporter");
			mtxGetValueProps.setProperty(TITLE, "Get value from matrix");
			registerService(bc, mtxGetValueTaskFactory, TaskFactory.class, mtxGetValueProps);
		}

		{
			final GXAFetchTaskFactory gxaFetchTaskFactory = new GXAFetchTaskFactory(gxaManager);
			Properties gxaFetchProps = new Properties();
			gxaFetchProps.setProperty(PREFERRED_MENU, "Apps.GXAFetch");
			gxaFetchProps.setProperty(TITLE, "Fetch an Experiment from EBI");
			registerService(bc, gxaFetchTaskFactory, TaskFactory.class, gxaFetchProps);
		}

		{
			final GXAShowResultsTaskFactory gxaResultsTaskFactory = new GXAShowResultsTaskFactory(gxaManager);
			Properties gxaResultsProps = new Properties();
			gxaResultsProps.setProperty(PREFERRED_MENU, "Apps.GXAFetch");
			gxaResultsProps.setProperty(TITLE, "Show Experiment Results from EBI");
			registerService(bc, gxaResultsTaskFactory, TaskFactory.class, gxaResultsProps);
		}

		{
			final GXACreateTableTaskFactory gxaCreateTableFactory = new GXACreateTableTaskFactory(gxaManager);
			Properties gxaTableProps = new Properties();
			gxaTableProps.setProperty(PREFERRED_MENU, "Apps.GXAFetch");
			gxaTableProps.setProperty(TITLE, "Create table from Experiment");
			registerService(bc, gxaCreateTableFactory, TaskFactory.class, gxaTableProps);
		}

	}
}
