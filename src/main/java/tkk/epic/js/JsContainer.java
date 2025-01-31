package tkk.epic.js;

import org.apache.logging.log4j.Level;
import tkk.epic.TkkEpic;

import javax.script.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Function;

public class JsContainer {
    public String js="";
    public String print;
    public StringWriter sw = new StringWriter();
    public PrintWriter pw = new PrintWriter(sw);
    public ScriptEngineManager mgr = new ScriptEngineManager(null);
    public ScriptEngine engine = mgr.getEngineByName("ECMAScript");
    public boolean errored=false;
    public HashSet<String> unknownFunctions;
    public Consumer<JsContainer> errorPrint=null;


    public JsContainer(String code, Consumer<JsContainer> errorPrint){
        errorPrint=errorPrint;
        initJs(code);
    }
    public JsContainer(String code){
            initJs(code);
    }
    public void initJs(String js) {
        this.errored=false;
        print="";
        mgr = new ScriptEngineManager(null);
        try {
            if (mgr.getEngineByName("ECMAScript") == null) {
                Class<?> c = Class.forName("org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory");
                ScriptEngineFactory factory = (ScriptEngineFactory)c.newInstance();
                factory.getScriptEngine();
                mgr.registerEngineName("ECMAScript", factory);
                mgr.registerEngineExtension("js", factory);
                mgr.registerEngineMimeType("application/ecmascript", factory);
            }
        } catch (Exception throwable) {
            TkkEpic.LOGGER.log(Level.ERROR,"tkk ERROR initJs "+throwable);
        }
        engine = mgr.getEngineByName("ECMAScript");

        engine.getContext().setWriter(pw);
        engine.getContext().setErrorWriter(pw);
        try {
            this.js=js;
            engine.eval(js);
        }catch (Exception e){
            this.errored=true;
            appandConsole(e.getMessage());
            if(errorPrint!=null){errorPrint.accept(this);}
        }
        appandConsole(sw.getBuffer().toString().trim());
        sw.getBuffer().delete(0,sw.getBuffer().length());
        unknownFunctions = new HashSet<>();
    }
    public void appandConsole(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        if(this.print==null || this.print.equals("")){
            this.print=message;
            return;
        }
        this.print = message + "\n" + this.print;
    }
    public void setVar(String name,Object value){
        engine.put(name,value);
    };
    public Object getVar(String name){
        return engine.get(name);
    }
    public Object run(String fn,Object... args) {
        if(errored || unknownFunctions.contains(fn)){return null;}
        Object obj=null;
        try {
            obj=((Invocable) engine).invokeFunction(fn, args);
        }catch (NoSuchMethodException e){
            unknownFunctions.add(fn);
            //TkkGameLib.print("NoSuchMethodException:"+ miscTool.getError(e));
        }catch (ScriptException e){
            this.errored=true;
            appandConsole(e.getMessage());
            TkkEpic.LOGGER.log(Level.ERROR,"ScriptException:"+getError(e));
            if(errorPrint!=null){errorPrint.accept(this);}
        }catch (Exception e){
            this.errored=true;
            appandConsole(e.getMessage());
            TkkEpic.LOGGER.log(Level.ERROR,"Exception:"+getError(e));
            if(errorPrint!=null){errorPrint.accept(this);}
        }
        appandConsole(sw.getBuffer().toString().trim());
        sw.getBuffer().delete(0,sw.getBuffer().length());
        return obj;
    }
    public Function<Object[],Object> getFunction(String functionName){
        return (args)->{return this.run(functionName,args);};
    }


    public static String getError(Exception e){
        StringWriter temp = new StringWriter();
        e.printStackTrace(new PrintWriter(temp,true));
        return temp.toString();
    }

}