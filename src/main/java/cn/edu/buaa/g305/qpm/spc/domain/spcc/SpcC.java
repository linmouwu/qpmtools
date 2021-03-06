package cn.edu.buaa.g305.qpm.spc.domain.spcc;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;

import cn.edu.buaa.g305.qpm.spc.domain.Spc;
import cn.edu.buaa.g305.qpm.spc.domain.spcxmr.SpcXMRIn;
import cn.edu.buaa.g305.qpm.spc.domain.spcxmr.SpcXMROut;
import cn.edu.buaa.g305.qpm.system.domain.Project;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;

@Document
@JsonSerialize(include=Inclusion.NON_NULL)
public class SpcC extends Spc{
	
	@Indexed(unique=true)
	private String name;
	@DBRef
	private Project project;
	
	private static String type="spcC";
	private String stauts="computeFinished";
	private SpcCIn input;
	private SpcCOut output;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}


	public String getStauts() {
		return stauts;
	}

	public void setStauts(String stauts) {
		this.stauts = stauts;
	}

	

	public SpcCIn getInput() {
		return input;
	}

	public void setInput(SpcCIn input) {
		this.input = input;
	}

	public SpcCOut getOutput() {
		return output;
	}

	public void setOutput(SpcCOut output) {
		this.output = output;
	}

	public static void setType(String type) {
		SpcC.type = type;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}


	//设置出错输出格式，不输出的设为空，jackson不输出
	public void setErrorOutput(String error,HttpStatus httpStatus) {
		
		id=null;
		name=null;
		stauts=null;
		input=null;
		output=null;
		project=null;
		this.error=error;
		this.httpStatus=httpStatus;
	}

	@Override
	
	public String toString()
	{
		String string="{id:"+id+","+
				       "name:"+ name+","+
				       "type:"+ type+","+
				       "stauts:"+       stauts+","+
				       "input:" +   input+","+
				       "output:" +   output+","+
				       "project"+project+"}";
		return string;
		
	}
	
	public static String format()
	{
		String format="{"+"createFormat:{"+
			       "name:organizationName.projectName."+type+".name,"+
			       "inputC:{" + SpcC.format()+"}},"+
			       	"updateFormat:{"	+ 
			       	"name:organizationName.projectName."+type+".name,"+
				    "inputC:{" + SpcC.format()+"}}}";
		return format;
	}


}
