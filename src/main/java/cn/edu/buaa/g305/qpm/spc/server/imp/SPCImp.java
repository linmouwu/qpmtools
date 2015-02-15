package cn.edu.buaa.g305.qpm.spc.server.imp;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import cn.edu.buaa.g305.qpm.spc.dao.SpcXMRRepository;
import cn.edu.buaa.g305.qpm.spc.dao.SpcXRRepository;
import cn.edu.buaa.g305.qpm.spc.dao.SpcXSRepository;
import cn.edu.buaa.g305.qpm.spc.domain.*;
import cn.edu.buaa.g305.qpm.spc.domain.spcxmr.SpcXMR;
import cn.edu.buaa.g305.qpm.spc.domain.spcxmr.SpcXMRIn;
import cn.edu.buaa.g305.qpm.spc.domain.spcxmr.SpcXMROut;
import cn.edu.buaa.g305.qpm.spc.domain.spcxr.SpcXRIn;
import cn.edu.buaa.g305.qpm.spc.domain.spcxr.SpcXROut;
import cn.edu.buaa.g305.qpm.spc.domain.spcxr.SpcXR;
import cn.edu.buaa.g305.qpm.spc.domain.spcxs.SpcXS;
import cn.edu.buaa.g305.qpm.spc.domain.spcxs.SpcXSIn;
import cn.edu.buaa.g305.qpm.spc.domain.spcxs.SpcXSOut;
import cn.edu.buaa.g305.qpm.spc.server.SPCService;
import cn.edu.buaa.g305.qpm.system.dao.ProjectRepository;
import cn.edu.buaa.g305.qpm.system.domain.Project;
import cn.edu.buaa.g305.qpm.system.server.SystemFind;
import static cn.edu.buaa.g305.qpm.spc.system.VariableControlChartsFactor.*;
import static cn.edu.buaa.g305.qpm.system.StringArrayToDoubleArray.*;
import static cn.edu.buaa.g305.qpm.system.DoublePrecisonArrayToStringArray.*;

@Component
public class SPCImp implements SPCService{
	
	@Autowired
	private SpcXRRepository spcxrRepository;
	@Autowired
	private SpcXSRepository spcxsRepository;
	@Autowired
	private SpcXMRRepository spcxmrRepository;
	@Autowired
	private SystemFind systemFind;
	@Autowired
	private ProjectRepository projectRepository;

	public SpcXROut computeXR(SpcXRIn spcxrIn) {
		
		SpcXROut spcxrOut=new SpcXROut();
		int precision=4;
		//样本数
		int n=spcxrIn.getX().length;
		//样本内样品数
		int group_n=spcxrIn.getX()[0].length;
		//样本内X平均值
		double[] xAverage=new double[n];
		//样本内极差
		double[] r=new double[n];
		//样本间X的均值
		double xSumAverage=0;
		//样本间R的均值
		double rAverage=0;
		int i=0;
		//计算X总平均值和R的平均值
		for (double[] xSum :toDouble(spcxrIn.getX())) {

			double min=xSum[0];
			double max=xSum[0];
			
			for (double x : xSum) {
				
				if(x<min)
				{
					min=x;
				}
				else if(x>max)
				{
					max=x;
				}
				xAverage[i]+=x;
								
			}
			xAverage[i]=xAverage[i]/group_n;
			xSumAverage+=xAverage[i];
			r[i]=max-min;
			rAverage+=r[i];
			i++;	
		}
		
		xSumAverage=xSumAverage/n;
		rAverage=rAverage/n;
		//计算X图的UCL和LCL，设置X图输出

		spcxrOut.setX(toStringPrecision(xAverage, precision));
		spcxrOut.setxCL(toStringPrecision(xSumAverage, precision));
		spcxrOut.setxUCL(toStringPrecision(xSumAverage+computeA2(group_n)*rAverage,  precision));
		spcxrOut.setxLCL(toStringPrecision(xSumAverage-computeA2(group_n)*rAverage,  precision));
		
		//计算R图的UCL和LCL，设置R图输出

		spcxrOut.setR(toStringPrecision(r,precision));
		spcxrOut.setrCL(toStringPrecision(rAverage, 4));
		spcxrOut.setrUCL(toStringPrecision(rAverage*computeD4(group_n), 4));
		spcxrOut.setrLCL(toStringPrecision(rAverage*computeD3(group_n), 4));
		
		spcxrOut.setTime(spcxrIn.getTime());
		
	
		return spcxrOut;
	}

	public SpcXSOut computeXS(SpcXSIn spcxsIn) {
		SpcXSOut spcxsOut=new SpcXSOut();
		//样本数
		int n=spcxsIn.getX().length;
		//样本内样品数
		int group_n=spcxsIn.getX()[0].length;
		//样本内X平均值
		double[] xAverage=new double[n];
		//样本内标准差估计
		double[] s=new double[n];
		//样本间X的均值
		double xSumAverage=0;
		//样本间S的均值
		double sAverage=0;
		//精度
		int precision=2;
		double[][] x=toDouble(spcxsIn.getX());
		//各个X平均值
		int i=0;
		for (double[] xSum : x) {

			for (double xSingal : xSum) {				
				xAverage[i]+=xSingal;								
			}
			xAverage[i]=xAverage[i]/group_n;
			xSumAverage+=xAverage[i];
			i++;	
		}
		//计算X总平均值
		xSumAverage=xSumAverage/n;
	    //计算各个s
		for (int m=0;m<n;m++)
		{
			for(int k=0;k<group_n;k++)
			{
				s[m]+=Math.pow(x[m][k]-xAverage[m],2);
			}
			s[m]=Math.sqrt(s[m]/(group_n-1));
			sAverage+=s[m];
		}
		//计算s的平均值
		sAverage=sAverage/n;
		//计算X图的UCL和LCL，设置X图输出

		if(spcxsIn.getSigma()==null)
		{
			spcxsOut.setX(toStringPrecision(xAverage,precision));
			spcxsOut.setxCL(toStringPrecision(xSumAverage, precision));
			spcxsOut.setxUCL(toStringPrecision(xSumAverage+computeA3(group_n)*sAverage, precision));
			spcxsOut.setxLCL(toStringPrecision(xSumAverage-computeA3(group_n)*sAverage, precision));
			//计算S图的UCL和LCL，设置S图输出
	      
			spcxsOut.setS(toStringPrecision(s,precision));
			spcxsOut.setsCL(toStringPrecision(sAverage, precision));
			spcxsOut.setsUCL(toStringPrecision(sAverage*computeB4(group_n), precision));
			spcxsOut.setsLCL(toStringPrecision(sAverage*computeB3(group_n), precision));
			
			spcxsOut.setTime(spcxsIn.getTime());
			return spcxsOut;
		}
		else
		{
			double sigma=toDouble(spcxsIn.getSigma());
			spcxsOut.setX(toStringPrecision(xAverage,precision));
			spcxsOut.setxCL(toStringPrecision(xSumAverage, precision));
			spcxsOut.setxUCL(toStringPrecision(xSumAverage+sigma*3, precision));
			spcxsOut.setxLCL(toStringPrecision(xSumAverage-sigma*3, precision));
			//计算S图的UCL和LCL，设置S图输出
	      
			spcxsOut.setS(toStringPrecision(s,precision));
			spcxsOut.setsCL(toStringPrecision(computeC4(group_n)*sigma, precision));
			spcxsOut.setsUCL(toStringPrecision(sigma*computeB6(group_n), precision));
			spcxsOut.setsLCL(toStringPrecision(sigma*computeB5(group_n), precision));
			
			spcxsOut.setTime(spcxsIn.getTime());
			
			return spcxsOut;
			
		}
		
		
	}
	
	public SpcXMROut computeXMR(SpcXMRIn spcxmrIn) {
		SpcXMROut spcxmrOut=new SpcXMROut();
		
		double[] x= toDouble(spcxmrIn.getX());
		//样本数
		int n=spcxmrIn.getX().length;
		//样本X平均值
		double xAverage=0;
		//移动极差
		double[] mR=new double[n-1];
		//移动极差均值
		double mRAverage=0;
		
		int precision=2;
	
		//计算X平均值,移动极差,移动极差平均值
		for (int i=0;i<n;i++) {
			if(i<n-1)
			{
				mR[i]=Math.abs(x[i+1]-x[i]);
				mRAverage+=mR[i];
			}			
			xAverage+=x[i];
		}
		xAverage=xAverage/n;
		mRAverage=mRAverage/(n-1);

		if(spcxmrIn.getSigma()==null)
		{
			spcxmrOut.setX(toStringPrecision(x,precision));
			spcxmrOut.setxCL(toStringPrecision(xAverage, precision));
			spcxmrOut.setxUCL(toStringPrecision(xAverage+2.66*mRAverage,precision));
			spcxmrOut.setxLCL(toStringPrecision(xAverage-2.66*mRAverage,precision));
			//计算MR图的UCL，设置S图输出
	      
			spcxmrOut.setMr(toStringPrecision(mR,precision));
			spcxmrOut.setMrCL(toStringPrecision(mRAverage, precision));
			spcxmrOut.setMrUCL(toStringPrecision(mRAverage*computeD4(2), precision));
			
			
			spcxmrOut.setTime(spcxmrIn.getTime());
			return spcxmrOut;
		}
		else
		{
			double sigma=toDouble(spcxmrIn.getSigma());
			spcxmrOut.setX(toStringPrecision(x,precision));
			spcxmrOut.setxCL(toStringPrecision(xAverage, precision));
			spcxmrOut.setxUCL(toStringPrecision(xAverage+3*sigma,precision));
			spcxmrOut.setxLCL(toStringPrecision(xAverage-3*sigma,precision));
			//计算MR图的UCL，设置S图输出
	      
			spcxmrOut.setMr(toStringPrecision(mR,precision));
			spcxmrOut.setMrCL(toStringPrecision(1.13*sigma, precision));
			spcxmrOut.setMrUCL(toStringPrecision(3.69*sigma, precision));
			
			
			spcxmrOut.setTime(spcxmrIn.getTime());
			return spcxmrOut;
			
		}
		
		
	}

	public SPCCOut computeC(SPCCIn spccIn) {
		double xAverage=0;
		SPCCOut spccOut=new SPCCOut();
		for (double x : spccIn.getX()) {
			xAverage+=x;
		}
		xAverage=xAverage/spccIn.getX().length;
		double sigma3=3*Math.sqrt(xAverage);
		spccOut.setC(xAverage);
		spccOut.setcUCL(xAverage+sigma3);
		if((xAverage-sigma3)<0)
		{
			spccOut.setcLCL(0);
		}
		else {
			spccOut.setcLCL(xAverage-sigma3);
		}
		spccOut.setX(spccIn.getX());
		spccOut.setTime(spccIn.getTime());
		
		return spccOut;
		
	}

	
	
	public SpcXR getXRByName(String name) {
		
		SpcXR spcXR=spcxrRepository.findByName(name);
		if(spcXR==null)
		{
			spcXR=new SpcXR();
			//找不到资源，设置错误信息和状态码
			spcXR.setErrorOutput("名为"+name+"的X-R图资源不存在",HttpStatus.NOT_FOUND);
			return spcXR;
		}
		else{
			spcXR.setHttpStatus(HttpStatus.OK);
			return spcXR;
		}
		
		
	}

	public SpcXR getXRById(String id) {
		
		SpcXR spcXR=spcxrRepository.findOne(id);
		if(spcXR==null)
		{
			spcXR=new SpcXR();
			spcXR.setErrorOutput("id为"+id+"的X-R图资源不存在",HttpStatus.NOT_FOUND);
			return spcXR;
		}
		else {
			spcXR.setHttpStatus(HttpStatus.OK);
			return spcXR;
		}
		
	}
	public SpcXR deleteXR(String id) {
		
		SpcXR spcXR=getXRById(id);
		if(spcXR.getError()==null)
		{
			spcXR.setStauts("deleted");
			spcxrRepository.delete(id);
		}
		
		return spcXR;
		
	}
	public SpcXR deleteXRByName(String name) {
		
		SpcXR spcXR=getXRByName(name);
		if(spcXR.getError()==null)
		{
			spcXR.setStauts("deleted");
		}
		if(spcXR.getId()!=null)
		{
			spcxrRepository.delete(spcXR.getId());
		}	
		return spcXR;
		
	}
	
	
	public SpcXR save(SpcXR spcXR,String project) {

		Project projectO=systemFind.findProductAffiliation(project);
		spcXR.setProject(projectO);
		try {
			spcXR.setOutput(computeXR(spcXR.getInput()));
		} catch (Exception e) {
			spcXR.setErrorOutput(e.getMessage(), HttpStatus.BAD_REQUEST);
			return spcXR;
		}

		try {
			spcXR=spcxrRepository.save(spcXR);
		} catch (DuplicateKeyException e) {
			spcXR.setErrorOutput("名字重复，请重新命名", HttpStatus.BAD_REQUEST);
			return spcXR;
		}
		
		spcXR.setHttpStatus(HttpStatus.CREATED);
		return spcXR;
	    
	}
	public SpcXR update(SpcXR spcXR,String id,String project){
		
		SpcXR spcXRt=getXRById(id);
		if(spcXRt.getError()==null)
		{
			spcXR.setId(id);
			spcXR=save(spcXR,project);
			if(spcXR.getError()==null)
			{
				spcXR.setHttpStatus(HttpStatus.OK);
				return spcXR;
			}
			else {
				return spcXR;
			}
			 
		}
		else {
			return spcXRt;
		}
	}
	public SpcList getSpcXRList() {
		List<SpcXR> spcXRList= (List<SpcXR>) spcxrRepository.findAll();
		SpcList spcList=new SpcList();
		spcList.setLists(spcXRList);
		spcList.setHttpStatus(HttpStatus.OK);
		return spcList;
	}
	
	@Override
	public SpcList getSpcXRListByProjectName(String name) {
		SpcList spcList=new SpcList();
		Project project=projectRepository.findByName(name);
		if(project==null)
		{
			spcList.setError("名为"+name+"项目不存在");
			spcList.setHttpStatus(HttpStatus.NOT_FOUND);
			spcList.setLists(new ArrayList<Spc>());
			return spcList;
		}
		else {
			spcList.setLists(spcxrRepository.findByProject(project));
			spcList.setHttpStatus(HttpStatus.OK);
			return spcList;
		}
	}
	
	//X-S图
	public SpcXS getXSByName(String name) {
		
		SpcXS spcXS=spcxsRepository.findByName(name);
		if(spcXS==null)
		{
			spcXS=new SpcXS();
			//找不到资源，设置错误信息和状态码
			spcXS.setErrorOutput("名为"+name+"的X-R图资源不存在",HttpStatus.NOT_FOUND);
			return spcXS;
		}
		else{
			spcXS.setHttpStatus(HttpStatus.OK);
			return spcXS;
		}
		
		
	}

	public SpcXS getXSById(String id) {
		
		SpcXS spcXS=spcxsRepository.findOne(id);
		if(spcXS==null)
		{
			spcXS=new SpcXS();
			spcXS.setErrorOutput("id为"+id+"的X-R图资源不存在",HttpStatus.NOT_FOUND);
			return spcXS;
		}
		else {
			spcXS.setHttpStatus(HttpStatus.OK);
			return spcXS;
		}
		
	}
	public SpcXS deleteXS(String id) {
		
		SpcXS spcXS=getXSById(id);
		if(spcXS.getError()==null)
		{
			spcXS.setStauts("deleted");
			spcxsRepository.delete(id);
		}
		
		return spcXS;
		
	}
	public SpcXS deleteXSByName(String name) {
		
		SpcXS spcXS=getXSByName(name);
		if(spcXS.getError()==null)
		{
			spcXS.setStauts("deleted");
		}
		if(spcXS.getId()!=null)
		{
			spcxsRepository.delete(spcXS.getId());
		}	
		return spcXS;
		
	}
	
	
	public SpcXS save(SpcXS spcXS,String project) {
		Project projectO=systemFind.findProductAffiliation(project);
		spcXS.setProject(projectO);

		try {
			spcXS.setOutput(computeXS(spcXS.getInput()));
		} catch (Exception e) {
			spcXS.setErrorOutput(e.getMessage(), HttpStatus.BAD_REQUEST);
			e.printStackTrace();
			return spcXS;
		}

		try {
			spcXS=spcxsRepository.save(spcXS);
		} catch (DuplicateKeyException e) {
			spcXS.setErrorOutput("名字重复，请重新命名", HttpStatus.BAD_REQUEST);
			return spcXS;
		}
		
		spcXS.setHttpStatus(HttpStatus.CREATED);
		return spcXS;
	    
	}
	public SpcXS update(SpcXS spcXS,String id,String project){
		
		SpcXS spcXSt=getXSById(id);
		if(spcXSt.getError()==null)
		{
			spcXS.setId(id);
			spcXS=save(spcXS,project);
			if(spcXS.getError()==null)
			{
				spcXS.setHttpStatus(HttpStatus.OK);
				return spcXS;
			}
			else {
				return spcXS;
			}
			 
		}
		else {
			return spcXSt;
		}
	}

	public SpcList getSpcXSList() {
		List<SpcXS> spcXSList= (List<SpcXS>) spcxsRepository.findAll();
		SpcList spcList=new SpcList();
		spcList.setLists(spcXSList);
		spcList.setHttpStatus(HttpStatus.OK);
		return spcList;
	}

	public SpcList getSpcXSListByProjectName(String name) {
		SpcList spcList=new SpcList();
		Project project=projectRepository.findByName(name);
		if(project==null)
		{
			spcList.setError("名为"+name+"项目不存在");
			spcList.setHttpStatus(HttpStatus.NOT_FOUND);
			spcList.setLists(new ArrayList<Spc>());
			return spcList;
		}
		else {
			spcList.setLists(spcxsRepository.findByProject(project));
			spcList.setHttpStatus(HttpStatus.OK);
			return spcList;
		}
		

	}
	
	//XMR图数据库操作

	@Override
	public SpcXMR getXMRByName(String name) {
		
		SpcXMR spcXMR=spcxmrRepository.findByName(name);
		if(spcXMR==null)
		{
			spcXMR=new SpcXMR();
			//找不到资源，设置错误信息和状态码
			spcXMR.setErrorOutput("名为"+name+"的XMR图资源不存在",HttpStatus.NOT_FOUND);
			return spcXMR;
		}
		else{
			spcXMR.setHttpStatus(HttpStatus.OK);
			return spcXMR;
		}
	}

	@Override
	public SpcXMR getXMRById(String id) {
		
		SpcXMR spcXMR=spcxmrRepository.findOne(id);
		if(spcXMR==null)
		{
			spcXMR=new SpcXMR();
			spcXMR.setErrorOutput("id为"+id+"的XMR图资源不存在",HttpStatus.NOT_FOUND);
			return spcXMR;
		}
		else {
			spcXMR.setHttpStatus(HttpStatus.OK);
			return spcXMR;
		}
		
	}

	@Override
	public SpcXMR deleteXMR(String id) {
		SpcXMR spcXMR=getXMRById(id);
		if(spcXMR.getError()==null)
		{
			spcXMR.setStauts("deleted");
			spcxmrRepository.delete(id);
		}	
		return spcXMR;
	}

	@Override
	public SpcXMR deleteXMRByName(String name) {
		SpcXMR spcXMR=getXMRByName(name);
		if(spcXMR.getError()==null)
		{
			spcXMR.setStauts("deleted");
		}
		if(spcXMR.getId()!=null)
		{
			spcxmrRepository.delete(spcXMR.getId());
		}	
		return spcXMR;
	}

	@Override
	public SpcXMR update(SpcXMR spcXMR, String id, String project) {
		SpcXMR spcXMRdb=getXMRById(id);
		if(spcXMRdb.getError()==null)
		{
			spcXMR.setId(id);
			spcXMR=save(spcXMR,project);
			if(spcXMR.getError()==null)
			{
				spcXMR.setHttpStatus(HttpStatus.OK);
				return spcXMR;
			}
			else {
				return spcXMR;
			}
			 
		}
		else {
			return spcXMRdb;
		}
	}

	@Override
	public SpcXMR save(SpcXMR spcXMR, String project) {
		Project projectO=systemFind.findProductAffiliation(project);
		spcXMR.setProject(projectO);

		try {
			spcXMR.setOutput(computeXMR(spcXMR.getInput()));
		} catch (Exception e) {
			spcXMR.setErrorOutput(e.getMessage(), HttpStatus.BAD_REQUEST);
			e.printStackTrace();
			return spcXMR;
		}

		try {
			spcXMR=spcxmrRepository.save(spcXMR);
		} catch (DuplicateKeyException e) {
			spcXMR.setErrorOutput("名字重复，请重新命名", HttpStatus.BAD_REQUEST);
			return spcXMR;
		}
		
		spcXMR.setHttpStatus(HttpStatus.CREATED);
		return spcXMR;
	    
	}

	@Override
	public SpcList getSpcXMRList() {
		List<SpcXMR> spcXMRList= (List<SpcXMR>) spcxmrRepository.findAll();
		SpcList spcList=new SpcList();
		spcList.setLists(spcXMRList);
		spcList.setHttpStatus(HttpStatus.OK);
		return spcList;
	}

	@Override
	public SpcList getSpcXMRListByProjectName(String name) {
		SpcList spcList=new SpcList();
		Project project=projectRepository.findByName(name);
		if(project==null)
		{
			spcList.setError("名为"+name+"项目不存在");
			spcList.setHttpStatus(HttpStatus.NOT_FOUND);
			spcList.setLists(new ArrayList<Spc>());
			return spcList;
		}
		else {
			spcList.setLists(spcxmrRepository.findByProject(project));
			spcList.setHttpStatus(HttpStatus.OK);
			return spcList;
		}
	}

}
