package junglevision.gathering.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibis.ipl.IbisIdentifier;
import ibis.ipl.NoSuchPropertyException;
import ibis.ipl.server.ManagementServiceInterface;
import ibis.ipl.support.management.AttributeDescription;
import junglevision.gathering.Location;
import junglevision.gathering.MetricDescription.MetricType;
import junglevision.gathering.Pool;

/**
 * A representation of a seperate Ibis instance within the data gathering universe
 * @author Maarten van Meersbergen
 */
public class Ibis extends Element implements junglevision.gathering.Ibis {
	private static final Logger logger = LoggerFactory.getLogger("ibis.deploy.gui.junglevision.gathering.impl.Ibis");
	
	ManagementServiceInterface manInterface;
	IbisIdentifier ibisid;
	private Pool pool;
	private Location location;
	
	public Ibis(ManagementServiceInterface manInterface, IbisIdentifier ibisid, Pool pool, Location location) {
		super();
		this.manInterface = manInterface;
		this.ibisid = ibisid;
		this.pool = pool;
		this.location = location;		
	}
	
	public junglevision.gathering.Metric[] getMetrics() {
		ArrayList<junglevision.gathering.Metric> result = new ArrayList<junglevision.gathering.Metric>();
		for (junglevision.gathering.Metric metric : metrics.values()) {
			if (metric.getDescription().getType() == MetricType.NODE) {
				result.add(metric);
			}
		}		
		return result.toArray(new junglevision.gathering.Metric[0]);
	}
	
	public Location getLocation() {
		return location;
	}
	
	public Pool getPool() {
		return pool;
	}
	
	public void update() throws TimeoutException {
		//Make an array of all the AttributeDescriptions needed to update this Ibis' metrics.
		ArrayList<AttributeDescription> requestList = new ArrayList<AttributeDescription>();
		for (Entry<junglevision.gathering.MetricDescription, junglevision.gathering.Metric> entry : metrics.entrySet()) {
			Metric metric = (Metric)entry.getValue();
			requestList.addAll(((MetricDescription)metric.getDescription()).getNecessaryAttributes());
		}
		
		AttributeDescription[] requestArray = (AttributeDescription[]) requestList.toArray(new AttributeDescription[0]);
		
		try {
			//Then, pass this array to the management service interface, and receive an array of result objects in the same order
			Object[] results = manInterface.getAttributes(ibisid, requestArray);
			
			//Split the result objects into partial arrays depending on the amount needed per metric
			int j=0;			
			for (Entry<junglevision.gathering.MetricDescription, junglevision.gathering.Metric> entry : metrics.entrySet()) {
				Metric metric = ((Metric)entry.getValue());
				Object[] partialResults = new Object[((MetricDescription)metric.getDescription()).getNecessaryAttributes().size()];				
				for (int i=0; i < partialResults.length ; i++) {
					partialResults[i] = results[j];	
					j++;
				}
				
				//And pass them to the individual metrics to be updated.
				metric.update(partialResults);
			}
		} catch (IOException e) {
			throw new TimeoutException();
		} catch (NoSuchPropertyException e) {
			logger.error("Ibis "+ibisid+" got exception while updating metrics: "+ e.getMessage());
		} catch (Exception e) {
			logger.error("Ibis "+ibisid+" got exception while updating metrics: "+ e.getMessage());
		}		
	}	
	
	//Tryout for steering
	public void kill() {
		//TODO implement
	}
	
	public String debugPrint() {
		String result = ibisid+"metrics: ";
				
		for (Entry<junglevision.gathering.MetricDescription, junglevision.gathering.Metric> entry : metrics.entrySet()) {
			result += "  " + entry.getValue().getDescription().getName();
		}

		result += "\n";
		
		return result;
	}
}