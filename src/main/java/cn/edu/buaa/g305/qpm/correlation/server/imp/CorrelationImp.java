package cn.edu.buaa.g305.qpm.correlation.server.imp;

import java.util.Map;

import org.springframework.stereotype.Component;

import cn.edu.buaa.g305.qpm.correlation.server.Correlation;

@Component
public class CorrelationImp implements Correlation{

	@Override
	public Map<String, Double> computeCorrelationAndPValue(double[] xArray,double[] yArray) {
		
		
		return null;
	}
	
}
