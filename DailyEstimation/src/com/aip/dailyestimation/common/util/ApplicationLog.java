package com.aip.dailyestimation.common.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
/**
 * @author Hitesh
 * Class used to redirect application log on a file on SDcard.
 *
 */
public class ApplicationLog {
	
	/**
	 * Function dumps the log on the file in SDCard.First the file with specified name is created on SDCard.
	 * Using the 'exec' method an array of strings is passed to execute log command is at runtime .
	 * Log Command has various filters that can be referred here "http://developer.android.com/guide/developing/debugging/debugging-log.html".
	 * In this function suppresses all log messages except those with the tagName i.e log 
	 * containing TAG with tagName are only redirected to file.
	 * 
     * @param context Object of Context, context from where the activity is going to start.
	 * @param fileName String value specifies the name for the file.
	 * @param tagName String value specifies the tag name ,only log with this tag name will be written on file with name specified in fileName.
	 */
    public static void writeToFile(Context context,String packageName, String fileName,String tagName)
    {
    	String line;
    	File file;
    	try{
//    		if(isMediaAvailable()){
//    			file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+packageName+"/"+fileName+".log");
//    		}else
//    			file = new File("/data/data/"+packageName+"/"+fileName+".log");
    		
    		
    		if(isMediaAvailable()){
    			file = new File(context.getExternalFilesDir(null).getAbsolutePath().toString()+"/"+fileName+".log"); 
    		}
    		else{
    			File myDir = context.getFilesDir(); 
    			file = new File(myDir,fileName); 
    		}
//    		File filename = new File(Environment.getExternalStorageDirectory()+"/"+packageName+"/"+fileName+".log"); 
    		file.createNewFile(); 
    		FileOutputStream fOut = new FileOutputStream(file);
    		OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

    		String[] LOGCAT_CMD = new String[] {"logcat", "-d",tagName+":V","*:S"}; 
    		Process logcatProc = null; 

    		logcatProc = Runtime.getRuntime().exec(LOGCAT_CMD);    		
    		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(logcatProc.getInputStream()));
    		
    		while ((line = bufferedReader.readLine()) != null) {
    			myOutWriter.append(line);
    		}
    		myOutWriter.close();
    		fOut.close(); 
    	}
    	catch (Exception e) {
    		Log.e("PITLOG", e.getMessage());
    	}
    }
    
    private static boolean isMediaAvailable(){
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)){
			return true;
		}
		return false;
	}
}