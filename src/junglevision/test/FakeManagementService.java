package junglevision.test;

import java.util.HashMap;
import java.util.Map;

import ibis.ipl.IbisIdentifier;
import ibis.ipl.server.ManagementServiceInterface;
import ibis.ipl.support.management.AttributeDescription;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeManagementService implements ManagementServiceInterface {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.test.FakeManagementService");
	
	private IbisIdentifier[] fakeIbises;
		
	public FakeManagementService(IbisIdentifier[] fakeIbises) {
		this.fakeIbises = fakeIbises;
	}
	
	public Object[] getAttributes(IbisIdentifier arg0, AttributeDescription... arg1) throws Exception {
		Object[] result = new Object[arg1.length];
		for (int i=0; i<arg1.length; i++) {
			
			if (		arg1[i].getBeanName().compareTo("java.lang:type=OperatingSystem") == 0 &&
						arg1[i].getAttribute().compareTo("ProcessCpuTime") == 0) {
				result[i] = (long) (Math.random()*5000);
			} else if (	arg1[i].getBeanName().compareTo("java.lang:type=Runtime") == 0 &&
						arg1[i].getAttribute().compareTo("Uptime") == 0) {
				result[i] = (long) (Math.random()*5000);
			} else if (	arg1[i].getBeanName().compareTo("java.lang:type=OperatingSystem") == 0 &&
						arg1[i].getAttribute().compareTo("AvailableProcessors") == 0) {
				result[i] = (int) 4;
			} else if (	arg1[i].getBeanName().compareTo("java.lang:type=Memory") == 0 &&
						arg1[i].getAttribute().compareTo("HeapMemoryUsage") == 0) {
				
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
				
				CompositeType type = new CompositeType("dummy", "test", itemNames, itemDescriptions, itemTypes);
				
				HashMap<String, Long> values = new HashMap<String, Long>();
				values.put("used", 	(long) (Math.random()*5000));
				values.put("max", 	(long) (Math.random()*5000));
				
				CompositeData data = new CompositeDataSupport(type, values);
				
				result[i] = data;				
			} else if (	arg1[i].getBeanName().compareTo("java.lang:type=Memory") == 0 &&
						arg1[i].getAttribute().compareTo("NonHeapMemoryUsage") == 0) {
				
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
				
				CompositeType type = new CompositeType("dummy", "test", itemNames, itemDescriptions, itemTypes);
				
				HashMap<String, Long> values = new HashMap<String, Long>();
				values.put("used", 	(long)(Math.random()*5000));
				values.put("max", 	(long)(Math.random()*5000));
				
				CompositeData data = new CompositeDataSupport(type, values);
				
				result[i] = data;
			} else if (	arg1[i].getBeanName().compareTo("java.lang:type=Threading") == 0 &&
						arg1[i].getAttribute().compareTo("ThreadCount") == 0) {
				
				result[i] = (int) (Math.random()*100);
			} else if (	arg1[i].getBeanName().compareTo("ibis") == 0 &&
						arg1[i].getAttribute().compareTo("receivedBytesPerIbis") == 0) {
				
				Map<IbisIdentifier, Long> resultMap = new HashMap<IbisIdentifier, Long>();
				int destinations = (int) (Math.random()*fakeIbises.length);
				int j = 0;
				while (j<destinations) {
					int randomIbis = (int) (Math.random()*fakeIbises.length);
					while (resultMap.containsKey(fakeIbises[randomIbis])) {
						randomIbis = (int) (Math.random()*fakeIbises.length);
					}
					resultMap.put(fakeIbises[randomIbis], (long) (Math.random()*5000));
					j++;
				}
				result[i] = resultMap;
			} else if (	arg1[i].getBeanName().compareTo("ibis") == 0 &&
						arg1[i].getAttribute().compareTo("sentBytesPerIbis") == 0) {
				
				Map<IbisIdentifier, Long> resultMap = new HashMap<IbisIdentifier, Long>();
				int destinations = (int) (Math.random()*fakeIbises.length);
				int j = 0;
				while (j<destinations) {
					int randomIbis = (int) (Math.random()*fakeIbises.length);
					while (resultMap.containsKey(fakeIbises[randomIbis])) {
						randomIbis = (int) (Math.random()*fakeIbises.length);
					}
					resultMap.put(fakeIbises[randomIbis], (long) (Math.random()*5000));
					j++;
				}
				result[i] = resultMap;
			}
			
			if (logger.isDebugEnabled()) {
				logger.debug(arg1[i].getAttribute() +" result: "+ result[i]);
			}
			
		}
		return result;
	}

}
