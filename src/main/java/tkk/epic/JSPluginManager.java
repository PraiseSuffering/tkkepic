package tkk.epic;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.Level;
import tkk.epic.js.JsContainer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public enum JSPluginManager {
    INSTANCE;

    public final LinkedHashMap<String, JsContainer> PLUGINS=new LinkedHashMap<>();

    public final String MODIFIER_BEFORE="before";

    public final String MODIFIER_AFTER="after";

    public final String MODIFIER_REQUIRED_BEFORE="required-before";

    public final String MODIFIER_REQUIRED_AFTER="required-after";

    public final String MODIFIER_LINK=":";

    public final String MODIFIER_SEPARATOR=";";

    public final String JS_FUNCTION_NAME_GET_PLUGIN_ID="getID";

    public final String JS_FUNCTION_NAME_GET_LOADING_SEQUENCE_REQUIREMENT="getLoadingRule";

    public void printMessage(String message){
        TkkEpic.LOGGER.log(Level.INFO,"[JSPluginManager]:"+message);
    }

    public void loaderJSPlugin(){
        TkkEpic.LOGGER.log(Level.INFO,"loaderJSPlugin!");
        unloadPlugin(PLUGINS);
        pluginInit(PLUGINS,pluginSort(getPluginList()));
        for(String name:PLUGINS.keySet()){
            TkkEpic.LOGGER.log(Level.INFO,"JSPlugin "+name);
        }
    }

    public JsContainer[] getPluginList(){
        JsContainer[] rt=new JsContainer[0];
        try{
            String[] tempA= getPluginJs();
            rt=new JsContainer[tempA.length];
            for(int i=0;i<tempA.length;i++){
                rt[i]=new JsContainer(tempA[i]);
            }
        }catch (Exception e){
            printMessage("error:"+ getError(e));
        }
        return rt;
    }

    public JsContainer[] pluginSort(JsContainer[] list){
        LinkedList<String> IDs=new LinkedList<>();
        LinkedList<JsContainer> linkedList=new LinkedList<>();
        ArrayList allIDs=new ArrayList();
        JsContainer[] copy=list.clone();
        for(int i=0;i<copy.length;i++){
            if(copy[i]==null){continue;}
            //添加无加载顺序需求的插件
            String id;
            if(IDs.contains((String) copy[i].run(JS_FUNCTION_NAME_GET_PLUGIN_ID))){
                copy[i]=null;
                printMessage("error:Duplicate id");
                continue;
            }
            id=(String) copy[i].run(JS_FUNCTION_NAME_GET_PLUGIN_ID);
            if(copy[i].run(JS_FUNCTION_NAME_GET_LOADING_SEQUENCE_REQUIREMENT)==null){
                linkedList.add(copy[i]);
                IDs.add(id);
                copy[i]=null;
            }else{
                allIDs.add(id);
            }
        }
        int addPluginCount;
        do{
            addPluginCount=0;
            plugin:
            for(int i=0;i<copy.length;i++){
                if(copy[i]==null){continue;}
                String[] rules=((String) copy[i].run(JS_FUNCTION_NAME_GET_LOADING_SEQUENCE_REQUIREMENT)).split(MODIFIER_SEPARATOR);
                String selfID=(String) copy[i].run(JS_FUNCTION_NAME_GET_PLUGIN_ID);
                ArrayList<String> afterTarget=new ArrayList<>();//后
                ArrayList<String> beforeTarget=new ArrayList<>();//前
                //遍历需求
                for(String ruleAndTarget:rules){
                    String rule=ruleAndTarget.split(MODIFIER_LINK)[0];
                    String target=ruleAndTarget.split(MODIFIER_LINK)[1];
                    if((rule.equals(MODIFIER_REQUIRED_AFTER) || rule.equals(MODIFIER_REQUIRED_BEFORE)) && !IDs.contains(target)){
                        if(!allIDs.contains(target)){
                            printMessage("error:"+selfID+" deficiency "+target);
                            copy[i]=null;
                        }
                        continue plugin;
                    }
                    if((rule.equals(MODIFIER_AFTER) || rule.equals(MODIFIER_BEFORE)) && !IDs.contains(target) && allIDs.contains(target)){
                        continue plugin;
                    }
                    if(rule.equals(MODIFIER_AFTER)){
                        afterTarget.add(target);
                    }
                    if(rule.equals(MODIFIER_BEFORE)){
                        beforeTarget.add(target);
                    }
                    if(rule.equals(MODIFIER_REQUIRED_AFTER)){
                        afterTarget.add(target);
                    }
                    if(rule.equals(MODIFIER_REQUIRED_BEFORE)){
                        beforeTarget.add(target);
                    }
                }
                //遍历完成且无缺失目标
                //添加至合适位置
                int afterIndex=-1;//插入位置至少
                int beforeIndex=-1;//插入位置最多
                //遍历获取插入位置
                //printMessage("debug|afterTarget:"+ arrayTool.arrayListToString(afterTarget)+",beforeTarget:"+arrayTool.arrayListToString(beforeTarget));
                for(int j=0;j<IDs.size();j++){
                    //printMessage("debug|afterTarget.contains:"+IDs.get(j)+",return:"+afterTarget.contains(IDs.get(j)));
                    if(afterTarget.contains(IDs.get(j))){
                        if(j>afterIndex){
                            afterIndex=j;
                        }
                    }
                    //printMessage("debug|beforeTarget.contains:"+IDs.get(j)+",return:"+beforeTarget.contains(IDs.get(j)));
                    if(beforeTarget.contains(IDs.get(j))){
                        if(beforeIndex==-1 || j<beforeIndex){
                            beforeIndex=j;
                        }
                    }
                }
                if(afterIndex>beforeIndex && (beforeIndex!=-1 && afterIndex!=-1)){
                    printMessage("error:Wrong loader rule");
                }else{
                    //printMessage("debug|beforeIndex:"+beforeIndex+",afterIndex:"+afterIndex);
                    int tempC=(afterIndex==-1)?beforeIndex:afterIndex+1;
                    tempC=(tempC==-1)?0:tempC;
                    tempC= Math.min(tempC, IDs.size());
                    IDs.add(tempC,(String) copy[i].run(JS_FUNCTION_NAME_GET_PLUGIN_ID));
                    linkedList.add(tempC,copy[i]);
                    copy[i]=null;
                    addPluginCount++;
                }
            }
        }while (addPluginCount!=0);
        JsContainer[] rt=new JsContainer[linkedList.size()];
        for(int i=0;i<linkedList.size();i++){
            rt[i]=linkedList.get(i);
        }
        return rt;

    }

    public void unloadPlugin(LinkedHashMap<String,JsContainer> map){
        callPluginRunFN("unload");
        map.clear();
    }

    public void pluginInit(LinkedHashMap<String,JsContainer> map,JsContainer[] plugins){
        map.clear();
        for (JsContainer js:plugins){
            String id=(String) js.run(JS_FUNCTION_NAME_GET_PLUGIN_ID);
            if(id==null){continue;}
            map.put(id,js);
        }
    }

    public void callPluginRunFN(String functionName,Object... args) {
        String[] keys=PLUGINS.keySet().toArray(new String[0]);
        for(int i=0;i<keys.length;i++){
            JsContainer js=PLUGINS.get(keys[i]);
            js.run(functionName,args);
            if(js.errored){
                printMessage("["+keys[i]+"]error:"+js.print);
            }
        }
    }

    public JsContainer getPluginMain(String id){
        return PLUGINS.get(id);
    }


    public static String[] getPluginJs(){
        ArrayList<String> returnedVault= Lists.newArrayList();
        try {
            File file = new File(TkkEpic.MOD_DIR.getCanonicalPath() + "/plugin/");
            if (!file.exists()) {
                file.mkdirs();
            }
            File[] list=file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if(name.endsWith(".js")){return true;}
                    return false;
                }
            });
            for(File JSfile:list){
                InputStreamReader fr = new InputStreamReader(new FileInputStream(JSfile), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(fr);
                StringBuffer jsCode=new StringBuffer();
                String temp;
                while ((temp= br.readLine())!=null){
                    jsCode.append("\n"+temp);
                }
                fr.close();
                br.close();
                returnedVault.add(jsCode.toString());
            }
        }catch (Exception e){
            TkkEpic.LOGGER.log(Level.ERROR,"getPluginJs error:"+ getError(e));
        }
        String[] rt=new String[returnedVault.size()];
        for(int i = 0;i<returnedVault.size();i++){
            rt[i]=returnedVault.get(i);
        }
        return rt;
    }


    public static String getError(Exception e){
        StringWriter temp = new StringWriter();
        e.printStackTrace(new PrintWriter(temp,true));
        return temp.toString();
    }

}
