package junglevision.test;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import ibis.ipl.IbisIdentifier;
import ibis.ipl.NoSuchPropertyException;
import ibis.ipl.server.ManagementServiceInterface;
import ibis.ipl.support.management.AttributeDescription;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import junglevision.gathering.exceptions.UnknownAttributeException;
import junglevision.test.FakeRegistryService.State;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeManagementService implements ManagementServiceInterface, FakeService {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.test.FakeManagementService");
	
	FakeRegistryService reg;
	HashMap<IbisIdentifier, State> ibises;
		
	public FakeManagementService(FakeRegistryService reg) {
		this.reg = reg;
		
		ibises = new HashMap<IbisIdentifier, State>();
		
		//Start an update timer for the list mutations
		UpdateTimer timer = new UpdateTimer(this);
		new Thread(timer).start();
	}
	
	public Object[] getAttributes(IbisIdentifier id, AttributeDescription... desc) throws IOException, NoSuchPropertyException {
		synchronized(ibises) {
			//if failing, go into an infinite loop, to simulate this ibis' failing connective state
			while(ibises.containsKey(id) && ibises.get(id) == State.FAILING) {
				//logger.debug("requested a failing ibis");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					throw new IOException("timeout");
				}
			}
			//This ibis may have been remove while we were waiting, or have not existed at all because it was recently removed
			if (!ibises.containsKey(id)) {
				logger.debug("requested a dead ibis");
				throw new SocketException("ibis doesn't exist");
			}
						
			//Otherwise just return decent results
			Object[] result = new Object[desc.length];			
			for (int i=0; i<desc.length; i++) {			
				//logger.debug("working on "+desc[i].getAttribute());
				
				if (		desc[i].getBeanName().compareTo("java.lang:type=OperatingSystem") == 0 &&
							desc[i].getAttribute().compareTo("ProcessCpuTime") == 0) {
					result[i] = (long) (Math.random()*5000);
				} else if (	desc[i].getBeanName().compareTo("java.lang:type=Runtime") == 0 &&
							desc[i].getAttribute().compareTo("Uptime") == 0) {
					result[i] = (long) (Math.random()*5000);
				} else if (	desc[i].getBeanName().compareTo("java.lang:type=OperatingSystem") == 0 &&
							desc[i].getAttribute().compareTo("AvailableProcessors") == 0) {
					result[i] = (int) 4;
				} else if (	desc[i].getBeanName().compareTo("java.lang:type=Memory") == 0 &&
							desc[i].getAttribute().compareTo("HeapMemoryUsage") == 0) {
					
					String[] itemNames = new String[2];
					itemNames[0] = "used";
					itemNames[1] = "max";
					
					String[] itemDescriptions = new String[2];
					itemDescriptions[0] = "used";
					itemDescriptions[1] = "maximum";
					
					@SuppressWarnings("rawtypes")
					OpenType itemTypes[] = new OpenType[] {
							SimpleType.LONG, 
							SimpleType.LONG};
					
					CompositeType type = null;
					try {
						type = new CompositeType("dummy", "test", itemNames, itemDescriptions, itemTypes);
					} catch (OpenDataException e) {
						logger.error("opendata exception");
						System.exit(0);
					}
					
					HashMap<String, Long> values = new HashMap<String, Long>();
					values.put("used", 	(long) (Math.random()*5000));
					values.put("max", 	(long) (Math.random()*5000));
					
					CompositeData data = null;
					try {
						data = new CompositeDataSupport(type, values);
					} catch (OpenDataException e) {
						logger.error("opendata exception");
						System.exit(0);
					}
					result[i] = data;				
				} else if (	desc[i].getBeanName().compareTo("java.lang:type=Memory") == 0 &&
							desc[i].getAttribute().compareTo("NonHeapMemoryUsage") == 0) {
					
					String[] itemNames = new String[2];
					itemNames[0] = "used";
					itemNames[1] = "max";
					
					String[] itemDescriptions = new String[2];
					itemDescriptions[0] = "used";
					itemDescriptions[1] = "maximum";
					
					@SuppressWarnings("rawtypes")
					OpenType itemTypes[] = new OpenType[] {
							SimpleType.LONG, 
							SimpleType.LONG};
					
					CompositeType type = null;
					try {
						type = new CompositeType("dummy", "test", itemNames, itemDescriptions, itemTypes);
					} catch (OpenDataException e) {
						logger.error("opendata exception");
						System.exit(0);
					}
					
					HashMap<String, Long> values = new HashMap<String, Long>();
					values.put("used", 	(long)(Math.random()*5000));
					values.put("max", 	(long)(Math.random()*5000));
					
					CompositeData data = null;
					try {
						data = new CompositeDataSupport(type, values);
					} catch (OpenDataException e) {
						logger.error("opendata exception");
						System.exit(0);
					}
					
					result[i] = data;
				} else if (	desc[i].getBeanName().compareTo("java.lang:type=Threading") == 0 &&
							desc[i].getAttribute().compareTo("ThreadCount") == 0) {
					
					result[i] = (int) (Math.random()*100);
				} else if (	desc[i].getBeanName().compareTo("ibis") == 0 &&
							desc[i].getAttribute().compareTo("receivedBytesPerIbis") == 0) {
					
					IbisIdentifier iArray[] = ibises.keySet().toArray(new IbisIdentifier[0]);
					Map<IbisIdentifier, Long> resultMap = new HashMap<IbisIdentifier, Long>();
					int destinations = (int) (Math.random()*ibises.size());
					int j = 0;
					while (j<destinations) {
						
						IbisIdentifier randomIbis = iArray[(int)(Math.random()*ibises.size())];
						while (resultMap.containsKey(randomIbis)) {
							randomIbis = iArray[(int)(Math.random()*ibises.size())];
						}
						resultMap.put(randomIbis, (long) (Math.random()*5000));
						j++;
					}
					result[i] = resultMap;
				} else if (	desc[i].getBeanName().compareTo("ibis") == 0 &&
							desc[i].getAttribute().compareTo("sentBytesPerIbis") == 0) {
					
					IbisIdentifier iArray[] = ibises.keySet().toArray(new IbisIdentifier[0]);
					Map<IbisIdentifier, Long> resultMap = new HashMap<IbisIdentifier, Long>();
					int destinations = (int) (Math.random()*ibises.size());
					int j = 0;
					while (j<destinations) {						
						IbisIdentifier randomIbis = iArray[(int)(Math.random()*ibises.size())];
						while (resultMap.containsKey(randomIbis)) {
							randomIbis = iArray[(int)(Math.random()*ibises.size())];
						}
						resultMap.put(randomIbis, (long) (Math.random()*5000));
						j++;
					}
					result[i] = resultMap;
				} else {
					throw new NoSuchPropertyException();
				}
				
				if (logger.isDebugEnabled()) {
					//logger.debug(desc[i].getAttribute() +" result: "+ result[i]);
				}
				
			}
			return result;
		}
	}

	public void doUpdate() {
		synchronized(ibises) {
			ibises = reg.getIbises();
		}
	}

}
