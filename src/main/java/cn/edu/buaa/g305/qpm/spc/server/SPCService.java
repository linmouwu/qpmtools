package cn.edu.buaa.g305.qpm.spc.server;

import cn.edu.buaa.g305.qpm.spc.domain.*;
import cn.edu.buaa.g305.qpm.spc.domain.spcc.SpcC;
import cn.edu.buaa.g305.qpm.spc.domain.spcc.SpcCIn;
import cn.edu.buaa.g305.qpm.spc.domain.spcc.SpcCOut;
import cn.edu.buaa.g305.qpm.spc.domain.spcu.SpcU;
import cn.edu.buaa.g305.qpm.spc.domain.spcu.SpcUIn;
import cn.edu.buaa.g305.qpm.spc.domain.spcu.SpcUOut;
import cn.edu.buaa.g305.qpm.spc.domain.spcxmr.SpcXMR;
import cn.edu.buaa.g305.qpm.spc.domain.spcxmr.SpcXMRIn;
import cn.edu.buaa.g305.qpm.spc.domain.spcxmr.SpcXMROut;
import cn.edu.buaa.g305.qpm.spc.domain.spcxr.SpcXRIn;
import cn.edu.buaa.g305.qpm.spc.domain.spcxr.SpcXROut;
import cn.edu.buaa.g305.qpm.spc.domain.spcxr.SpcXR;
import cn.edu.buaa.g305.qpm.spc.domain.spcxs.SpcXS;
import cn.edu.buaa.g305.qpm.spc.domain.spcxs.SpcXSIn;
import cn.edu.buaa.g305.qpm.spc.domain.spcxs.SpcXSOut;
import cn.edu.buaa.g305.qpm.spc.domain.spcz.SpcZ;
import cn.edu.buaa.g305.qpm.spc.domain.spcz.SpcZIn;
import cn.edu.buaa.g305.qpm.spc.domain.spcz.SpcZOut;

public interface SPCService {
	
	SpcXROut computeXR(SpcXRIn spcxr);
	SpcXSOut computeXS(SpcXSIn spcxsIn);
	SpcXMROut computeXMR(SpcXMRIn spcxmrIn);
	SpcCOut computeC(SpcCIn spccIn);
	SpcUOut computeU(SpcUIn spcuIn);
	SpcZOut computeZ(SpcZIn spczIn);
	
	//X-R图数据库操作
	SpcXR getXRByName(String name);
	
	SpcXR getXRById(String id);
	
	SpcXR deleteXR(String id);
	
	SpcXR deleteXRByName(String name);
	
	SpcXR update(SpcXR spcXR,String id,String project);
	SpcXR save(SpcXR spcXR,String project);
	
	SpcList getSpcXRList();
	SpcList getSpcXRListByProjectName(String name);
	
	
	//X-S图数据库操作
    SpcXS getXSByName(String name);
	
	SpcXS getXSById(String id);
	
	SpcXS deleteXS(String id);
	
	SpcXS deleteXSByName(String name);
	
	SpcXS update(SpcXS spcXS,String id,String project);
	SpcXS save(SpcXS spcXS,String project);
	
	SpcList getSpcXSList();
	SpcList getSpcXSListByProjectName(String name);
	
	//XMR图数据库操作
	SpcXMR getXMRByName(String name);
	
	SpcXMR getXMRById(String id);
	
	SpcXMR deleteXMR(String id);
	
	SpcXMR deleteXMRByName(String name);
	
	SpcXMR update(SpcXMR spcXMR,String id,String project);
	SpcXMR save(SpcXMR spcXMR,String project);
	
	SpcList getSpcXMRList();
	SpcList getSpcXMRListByProjectName(String name);
	
	//C图数据库操作
    SpcC getCByName(String name);
	
	SpcC getCById(String id);
	
	SpcC deleteC(String id);
	
	SpcC deleteCByName(String name);
	
	SpcC update(SpcC spcC,String id,String project);
	SpcC save(SpcC spcC,String project);
	
	SpcList getSpcCList();
	SpcList getSpcCListByProjectName(String name);
	
	//U图数据库操作
	SpcU getUByName(String name);
	
	SpcU getUById(String id);
	
	SpcU deleteU(String id);
	
	SpcU deleteUByName(String name);
	
	SpcU update(SpcU spcU,String id,String project);
	SpcU save(SpcU spcU,String project);
	
	SpcList getSpcUList();
	SpcList getSpcUListByProjectName(String name);
	
	//Z图数据库操作
	SpcZ getZByName(String name);
	
	SpcZ getZById(String id);
	
	SpcZ deleteZ(String id);
	
	SpcZ deleteZByName(String name);
	
	SpcZ update(SpcZ spcZ,String id,String project);
	SpcZ save(SpcZ spcZ,String project);
	
	SpcList getSpcZList();
	SpcList getSpcZListByProjectName(String name);

}
