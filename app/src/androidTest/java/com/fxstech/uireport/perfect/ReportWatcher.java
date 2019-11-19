package com.fxstech.uireport.perfect;

import android.app.Instrumentation;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AssumptionViolatedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportWatcher extends TestWatcher {
    private final static String TAG="ReportWatcher";
    private Instrumentation mInstrumentation;
    private String reportPath;
    private static final String reportFileName;
    private static final String reportHtmlFileName;

    private String lastReport="";
    private JSONObject jsonObj;
    private int testsPass = 0;
    private int testsFail = 0;
    private int testsSkip = 0;
    private int testAll = 0;

    private String beginTime;
    private String totalTime;
    private String testName;
    private JSONArray testResult;

    private long startTime;
    private long endTime;


    static {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        reportFileName = "report_data_"+dateFormat.format(date)+".json";
        reportHtmlFileName="report_"+dateFormat.format(date)+".html";
    }
    public ReportWatcher(Instrumentation Instrumentation) {
        this.mInstrumentation= Instrumentation;
    }

    @Override
    protected void succeeded(Description description) {
        endTime=System.currentTimeMillis();
        testsPass=testsPass+1;
        assembleTestData(description,"成功",new JSONArray());
    }

    private void assembleTestData(Description description,String susStatus,JSONArray logStack){
        //用例描述获取
        String desc="";
        if(description.getAnnotation(TestDescription.class)!=null){
            desc=description.getAnnotation(TestDescription.class).value();
        }

        JSONObject testObject=new JSONObject();
        try {
            testObject.put("className",description.getClassName());
            testObject.put("methodName",description.getMethodName());
            testObject.put("description",desc);
            testObject.put("spendTime",(endTime-startTime)+"ms");
            testObject.put("status",susStatus);
            testObject.put("log",logStack);
            testResult.put(testObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void failed(Throwable e, Description description) {
        //截图，保存 TODO
        endTime=System.currentTimeMillis();
        //保存堆栈信息
        StackTraceElement[] stacks = e.getStackTrace();
        JSONArray logStack=new JSONArray();
        for(StackTraceElement element:stacks){
            logStack.put(element.toString());
        }
        testsFail=testsFail+1;
       assembleTestData(description,"失败",logStack);

    }

    @Override
    protected void skipped(AssumptionViolatedException e, Description description) {
        endTime=System.currentTimeMillis();
        testsSkip=testsSkip+1;
        //保存堆栈信息
        StackTraceElement[] stacks = e.getStackTrace();
        JSONArray logStack=new JSONArray();
        for(StackTraceElement element:stacks){
            logStack.put(element.toString());
        }
        assembleTestData(description,"跳过",logStack);

    }

    @Override
    protected void starting(Description description) {
        startTime=System.currentTimeMillis();
        reportPath= mInstrumentation.getTargetContext().getExternalFilesDir("report").getAbsolutePath();
        Log.v(TAG,"reportPath="+reportPath);
        Log.v(TAG,"class="+description.getClassName()+"  "+"test="+description.getMethodName());
        try {
            //首次创建报告json模板，后续加载报告
            lastReport=read(reportPath+File.separator+reportFileName,template_data,false);
            jsonObj = new JSONObject(lastReport);
            testsPass=jsonObj.getInt("testPass");
            testsFail=jsonObj.getInt("testFail");
            testsSkip=jsonObj.getInt("testSkip");
            if(jsonObj.getString("beginTime").equals("0")){
                beginTime=getCurFormatTime();
            }else{
                beginTime=jsonObj.getString("beginTime");
            }
            if(jsonObj.getString("testName").equals("0")){
                testName=getCurFormatTime();
            }else{
                testName=jsonObj.getString("testName");
            }

            totalTime=jsonObj.getString("totalTime");
            testResult= jsonObj.getJSONArray("testResult");
           // testResult= new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finished(Description description) {
       //更新数据到json报告中
        testAll=testsPass+testsFail+testsSkip;
        //总运行时间
        long totalTimetmp=endTime-getLongTime(beginTime);

        try {
            jsonObj.put("testPass",testsPass);
            jsonObj.put("testFail",testsFail);
            jsonObj.put("testSkip",testsSkip);
            jsonObj.put("testAll",testAll);
            jsonObj.put("testName",testName);
            jsonObj.put("beginTime",beginTime);
            jsonObj.put("totalTime",totalTimetmp+"ms");
            jsonObj.put("testResult",testResult);
            Log.v(TAG,"json="+jsonObj.toString());
            File dataFile=new File(reportPath+File.separator+reportFileName);
            //删除原文件
            dataFile.delete();
            //新建文件
            if(dataFile.exists()){
                Log.v(TAG,"测试完成："+description.getMethodName()+"  删除报告数据文件失败。");
            }else{
                dataFile.createNewFile();
                //写入新的数据
                createJsonFile(jsonObj.toString(), reportPath, reportFileName);
                if(dataFile.length()>50){
                    Log.v(TAG,"测试完成："+description.getMethodName()+"  更新报告完成。");
                }
            }

            //生成html报告
            File htmlFile=new File(reportPath+File.separator+reportHtmlFileName);
            htmlFile.delete();
            createHtmlReport(jsonObj.toString(),htmlFile);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String read(String path,String data,boolean isDel) {
        File file = new File(path);
        if(isDel){
            file.delete();
        }else{
            if(!file.exists()){
                try {
                    file.createNewFile();
                    //写入模板
                    createJsonFile(data, reportPath, reportFileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        InputStream is = null;
        StringBuffer sb = new StringBuffer();
        try {
            is = new FileInputStream(file);
            int index = 0;
            byte[] b = new byte[1024];
            while ((index = is.read(b)) != -1) {
                sb.append(new String(b, 0, index));
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private void createHtmlReport(String testData,File htmlFile) {

        try {
            //写文件
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(htmlFile, true)));
            //读文件
            InputStream is = mInstrumentation.getTargetContext().getAssets().open("template.txt");
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                if(str.contains("${resultData}")){
                    str="var resultData ="+testData;
                   // Log.v(TAG,"temp="+str);
                }
                out.write(str+"\r\n");
            }
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(reader!=null){
                reader.close();
            }
            if(out!=null){
              out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成.json格式文件
     */
    public static boolean createJsonFile(String jsonString, String filePath, String fileName) {
        // 标记文件生成是否成功
        boolean flag = true;
        // 拼接文件完整路径
        String fullPath = filePath + File.separator + fileName;
        // 生成json格式文件
        try {
            // 保证创建一个新文件
            File file = new File(fullPath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();
            // 将格式化后的字符串写入文件
            Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            write.write(jsonString);
            write.flush();
            write.close();
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }

        // 返回是否成功的标记
        return flag;
    }


    public static String template_data="{" +
            "\"testPass\": 0," +
            "\"testResult\": [" +
            "]," +
            "\"testName\": \"0\"," +
            "\"testAll\": 0," +
            "\"testFail\": 0," +
            "\"beginTime\": \"0\"," +
            "\"totalTime\": \"0\"," +
            "\"testSkip\": 0" +
            "}";

    /**
     * 获取当前格式时间
     * @return
     */
    public static String getCurFormatTime(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String sim = dateFormat.format(date);
        return sim;
    }

    /**
     * 时间格式转成长整型时间
     * @param datetime
     * @return
     */
    public static long getLongTime(String datetime){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        long time=0;
        try {
            time=dateFormat.parse(datetime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 长整型时间转化为格式时间
     * @param longtime
     * @return
     */
    public static String getLongTime2FormatDate(long longtime){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String time=dateFormat.format(longtime);
        return time;
    }

}
