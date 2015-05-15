package expressions.functions;

import java.lang.reflect.InvocationTargetException;

import data.WorkSheet;
import expressions.Brackets;
import expressions.FunctionOp;

public class Funclass{
	String name;
	double paramCount;
	Class<? extends FunctionOp> baseClass = null;
	String funExp;
	String paramName;
	String code;
	WorkSheet ws;
	
	public Funclass(Class<? extends FunctionOp> c){
		baseClass = c;
	}
	
	public Funclass(String name, String paramName, String code, double paramCount, WorkSheet ws){
		this.name = name.toUpperCase();
		this.paramName = paramName;
		this.paramCount = paramCount;
		this.ws = ws;
		this.code = code;
	}
	
	public FunctionOp newInstance() throws InstantiationException, IllegalAccessException{
		if(preDefined())
			return baseClass.newInstance();
		else
			return new Function(null, name , code, paramName, ws, paramCount);
	}
	
	public FunctionOp newInstance(Brackets params) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		if(preDefined())
			return baseClass.getConstructor(Brackets.class).newInstance(params);
		else
			return new Function(params, name , code, paramName, ws, paramCount);
	}
	
	boolean preDefined(){
		return baseClass != null;
	}
}
