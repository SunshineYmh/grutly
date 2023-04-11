package com.vastly.hlht.nashornJS;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

public class NashornJavaScriptTask {

    private static ScriptEngineManager scriptEngineManager =new ScriptEngineManager();
    private static ScriptEngine scriptEngine =scriptEngineManager.getEngineByName("nashorn");

    //存储需要执行js脚本
    private static Map<String,Invocable> jsInvocable = new HashMap<>();

    /**
     * 设置 脚本
     * @param group 脚本分组
     * @param js
     * @throws ScriptException
     */
    public static void setNashornJavaScript(String group,String js) throws ScriptException {
        scriptEngine.eval(js);
        Invocable jsInvoke = (Invocable) scriptEngine;
        jsInvocable.put(group,jsInvoke);
    }

    /**
     * * 执行脚本
     * @param jsInvoke
     * @param ffmc  执行方法
     * @param paranms 参数
     * @return
     * @throws ScriptException
     * @throws NoSuchMethodException
     */
    public static Object nashornJavaScriptExecute(Invocable jsInvoke,String ffmc,Object[] paranms) throws ScriptException, NoSuchMethodException {
        return  jsInvoke.invokeFunction(ffmc, paranms);
    }

    /**
     * 执行脚本
     * @param group 分组
     * @param ffmc
     * @param js
     * @param paranms
     * @return
     * @throws ScriptException
     * @throws NoSuchMethodException
     */
    public static Object nashornJavaScriptSend(String group,String ffmc,String js,Object[] paranms) throws ScriptException, NoSuchMethodException {
        if(!jsInvocable.containsKey(group)){
            setNashornJavaScript( group, js);
        }
        Invocable jsInvoke = jsInvocable.get(group);
        return nashornJavaScriptExecute( jsInvoke, ffmc,paranms);
    }

    public static void main(String[] args) throws ScriptException, NoSuchMethodException, InterruptedException {
        String str = "function add (a, b) {return a+b; }";
       Object[] paranms = new Object[]{10, 5};
        Object res = nashornJavaScriptSend("test","add",str, paranms);
        System.out.println("-1>>>>>>"+res);

    }
}

