package tkk.epic.gameasset;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.Level;
import tkk.epic.TkkEpic;
import tkk.epic.js.JsContainer;
import yesman.epicfight.api.forgeevent.AnimationRegistryEvent;
import yesman.epicfight.api.model.JsonModelLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Animations {
    public static final String TKK_FILE_LOAD="tkkfileload";
    public static void registerAnimations(AnimationRegistryEvent event) {
        event.getRegistryMap().put(TKK_FILE_LOAD,Animations::build);
    }
    private static void build() {
        JsContainer[] loadAnimation=getAnimationJs();
        for(JsContainer js:loadAnimation){
            if(js.errored){
                TkkEpic.LOGGER.log(Level.ERROR,"tkkfileload error:"+js.print);
            }
        }
    }
    public static JsContainer[] getAnimationJs(){
        ArrayList<String> returnedVault= Lists.newArrayList();
        try {
            File file = new File(FMLPaths.GAMEDIR.get().toFile().getCanonicalPath() + "/"+TKK_FILE_LOAD+"/");
            if (!file.exists()) {
                file.mkdirs();
            }
            File[] files=file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if(name.endsWith(".js")){return true;}
                    return false;
                }
            });
            List<File> list= Arrays.asList(files);
            list.sort(Comparator.naturalOrder());
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
            e.printStackTrace();
        }
        JsContainer[] rt=new JsContainer[returnedVault.size()];
        for(int i = 0;i<returnedVault.size();i++){
            rt[i]=new JsContainer(returnedVault.get(i));
        }
        return rt;
    }


}
