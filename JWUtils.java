package cn.wjdiankong.jw.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;

import android.text.TextUtils;
import android.util.Log;

public class JWUtils {
	
	//过滤规则
	public static String logSwitch = null;
	public static String filterMethodName = null;
	public static String filterMethodReturnType = null;
	public static String filterMethodParamType = null;
	public static String filterClassName = null;
	public static String cmdLineStr = null;
	
	public static long fileModifyTime = System.currentTimeMillis();
	public static File file = new File("/data/local/tmp/log.txt");
	public static Method[] allMethods;
	
	static{
		cmdLineStr = getCmdArgs();
		parseCmdLineArgs(cmdLineStr);
		fileModifyTime = file.lastModified();
	}
	
	/**
	 * 打印堆栈信息
	 * @param tag
	 */
	public static void printErrorStatckTrace(String tag){
		if(!isShowLog()){
			return;
		}
		StackTraceElement[] stackElements = new Throwable().getStackTrace();
		if(!isFilterInfo(stackElements[1])){
			return;
		}
		try{
			throw new NullPointerException();
		}catch(Exception e){
			Log.d(tag, "++++++++++++++++++Start++++++++++++++++++");
			Log.d(tag, ""+e.getLocalizedMessage());
			Log.d(tag, "++++++++++++++++++++End++++++++++++++++++\n\n");
		}
	}
	
	/**
	 * 打印堆栈信息
	 * @param tag
	 */
	public static void printStackTrace(String tag) {
		if(!isShowLog()){
			return;
		}
		StackTraceElement[] stackElements = new Throwable().getStackTrace();
		if(!isFilterInfo(stackElements[1])){
			return;
		}
        if(stackElements != null){
        	Log.d(tag, "++++++++++++++++++Start++++++++++++++++++");
            for(int i = 0; i < stackElements.length; i++){
                Log.d(tag, stackElements[i].toString()); 
            }
            Log.d(tag, "++++++++++++++++++++End++++++++++++++++++\n\n");
        }
    }
	
	public static boolean isFilterInfo(StackTraceElement ment){
		boolean isClassName = isFilterClassName(ment);
		boolean isMethodName = isFilterMethodName(ment);
		boolean isMethodReturnT = isFilterMethodReturnType(ment);
		boolean isMethodParamT = isFilterMethodParamType(ment);
		return isClassName && isMethodName && isMethodReturnT && isMethodParamT;
	}
	
	/**
	 * 过滤方法
	 * @param ment
	 * @return
	 */
	private static boolean isFilterMethodName(StackTraceElement ment){
		if(TextUtils.isEmpty(filterMethodName)){
			return true;
		}
		String methodName = ment.getMethodName();
		if(TextUtils.isEmpty(methodName)){
			return true;
		}
		if(filterMethodName.equals(methodName)){
			return true;
		}
		return false;
	}
	
	/**
	 * 过滤类名
	 * @param ment
	 * @return
	 */
	private static boolean isFilterClassName(StackTraceElement ment){
		if(TextUtils.isEmpty(filterClassName)){
			return true;
		}
		String className = ment.getClassName();
		if(TextUtils.isEmpty(filterClassName)){
			return true;
		}
		if(filterClassName.equals(className)){
			return true;
		}
		return false;
	}
	
	/**
	 * 过滤方法返回类型
	 * @param ment
	 * @return
	 */
	private static boolean isFilterMethodReturnType(StackTraceElement ment){
		if(TextUtils.isEmpty(filterMethodReturnType)){
			return true;
		}
		getClassAllMethod(ment);
		for(Method method : allMethods){
			if(method.getName().equals(ment.getMethodName())){
				Class<?> returnType = method.getReturnType();
				String returnTypeS = getTypeSign(returnType);
				if(returnTypeS.equals(filterMethodReturnType)){
					System.out.println("returntype:"+returnTypeS+","+filterMethodReturnType);
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 过滤方法参数类型
	 * @param ment
	 * @return
	 */
	private static boolean isFilterMethodParamType(StackTraceElement ment){
		if(TextUtils.isEmpty(filterMethodParamType)){
			return true;
		}
		getClassAllMethod(ment);
		for(Method method : allMethods){
			if(method.getName().equals(ment.getMethodName())){
				Class<?>[] paramTypes = method.getParameterTypes();
				StringBuilder sb = new StringBuilder();
				for(Class<?> paramT : paramTypes){
					sb.append(getTypeSign(paramT)+";");
				}
				if(sb.toString().contains(filterMethodParamType)){
					System.out.println("paramtype:"+sb.toString()+","+filterMethodParamType);
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 获取类中所有的方法信息
	 * @param ment
	 */
	private static void getClassAllMethod(StackTraceElement ment){
		try{
			Class<?> objClass = Class.forName(ment.getClassName());
			Method[] methods1 = objClass.getMethods();
			Method[] methods2 = objClass.getDeclaredMethods();
			allMethods = new Method[methods1.length+methods2.length];
			for(int i=0;i<methods1.length;i++){
				allMethods[i] = methods1[i];
			}
			for(int j=methods1.length;j<methods1.length+methods2.length;j++){
				allMethods[j] = methods2[j-methods1.length];
			}
		}catch(Throwable e){
		}
	}
	
	/**
	 * ͨ获取签名信息
	 * @param typeClass
	 * @return
	 */
	private static String getTypeSign(Class<?> typeClass){
		if(boolean.class == typeClass){
			return "Z";
		}
		if(byte.class == typeClass){
			return "B";
		}
		if(short.class == typeClass){
			return "S";
		}
		if(int.class == typeClass){
			return "I";
		}
		if(float.class == typeClass){
			return "F";
		}
		if(double.class == typeClass){
			return "D";
		}
		if(long.class == typeClass){
			return "J";
		}
		if(char.class == typeClass){
			return "C";
		}
		
		String className = typeClass.getName();
		if(className == null){
			return "";
		}
		
		return className.replace(".", "/");
	}
	
	/**
	 * @return
	 */
	private static boolean isShowLog(){
		if(fileModifyTime != file.lastModified()){
			fileModifyTime = file.lastModified();
			cmdLineStr = getCmdArgs();
			parseCmdLineArgs(cmdLineStr);
		}
		if("1".equals(logSwitch)){
			return true;
		}
		return false;
	}
	
	/**
	 * 获取命令行参数信息
	 * @return
	 */
	public static String getCmdArgs(){
		File file = new File("/data/local/tmp/log.txt");
		BufferedReader bf= null;
		try {
			bf= new BufferedReader(new FileReader(file));
			String line = null;
			while((line = bf.readLine()) != null){
				if(line != null){
					return line;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try{
				if(bf != null)
					bf.close();
			}catch(Exception e){
				
			}
		}
		return null;
	}
	
	/**
	 * 解析命令行参数信息
	 * -s 1 -m JW -r int -p [int,java.lang.String] -c JWUtils
	 * @param cmdLine
	 */
	private static void parseCmdLineArgs(String cmdLine){
		try{
			String[] strAry = cmdLine.split(" ");
			for(int i=0;i<strAry.length;i+=2){
				switch(strAry[i]){
					case "-s":
						logSwitch = strAry[i+1];
						break;
					case "-m":
						filterMethodName = strAry[i+1];
						break;
					case "-r":
						filterMethodReturnType = getTypeSign(strAry[i+1]);
						break;
					case "-p":
						filterMethodParamType = strAry[i+1];
						try{
							String[] arys = filterMethodParamType.substring(1, filterMethodParamType.length()-1).split(",");
							StringBuilder sb = new StringBuilder();
							for(String str1 : arys){
								sb.append(getTypeSign(str1) + ";");
							}
							filterMethodParamType = sb.toString();
						}catch(Exception e){
						}
						break;
					case "-c":
						filterClassName = strAry[i+1];
						break;
				}
			}
		}catch(Exception e){
		}
	}
	
	/**
	 * @param type
	 * @return
	 */
	private static String getTypeSign(String type){
		switch(type){
			case "boolean":
				return getTypeSign(boolean.class);
			case "byte":
				return getTypeSign(byte.class);
			case "short":
				return getTypeSign(short.class);
			case "int":
				return getTypeSign(int.class);
			case "char":
				return getTypeSign(char.class);
			case "float":
				return getTypeSign(float.class);
			case "double":
				return getTypeSign(double.class);
			case "long":
				return getTypeSign(long.class);
		}
		try{
			return getTypeSign(Class.forName(type));
		}catch(Exception e){
			return null;
		}
	}

}
