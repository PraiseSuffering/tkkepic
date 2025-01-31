package tkk.epic.utils;

import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.Level;
import tkk.epic.TkkEpic;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class FileTool {
    public static Map<String,String> getPluginJs(File file){
        Map<String,String> returnedVault= new HashMap<>();
        try {
            //File file = new File(TkkGameLib.MOD_DIR.getCanonicalPath() + "/plugin/");
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
                //String temp=br.readLine();
                String temp;
                if((temp= br.readLine())!=null){jsCode.append(temp);}
                while ((temp= br.readLine())!=null){
                    jsCode.append("\n"+temp);
                }
                fr.close();
                br.close();
                returnedVault.put(JSfile.getName(),jsCode.toString());
            }
        }catch (Exception e){
            TkkEpic.LOGGER.log(Level.ERROR,"getPluginJs("+file+") error:"+e);
        }
        return returnedVault;
    }
    public static void putJs(File file,String string) throws Exception {
        File fileParent=file.getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        OutputStreamWriter opsw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        String[] line=string.split("\n");
        if(line.length==0){return;}
        opsw.write(line[0]);
        if(line.length==1){return;}
        for (int i=1;i<line.length;i++){
            opsw.write("\r\n"+line[i]);
        }
        opsw.flush();
        opsw.close();
    }

    public static String loadJsForJar(String modId,String file) throws IOException {

        Class<?> modClass = ModList.get().getModObjectById(modId).get().getClass();
        InputStream inputStream = modClass.getResourceAsStream(file);

        if (inputStream == null) {
            throw new NoSuchElementException("Can't find specified file in mod resource " + modId + ":" + file);
        }

        InputStreamReader fr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(fr);
        StringBuffer jsCode=new StringBuffer();
        String temp;
        if((temp= br.readLine())!=null){jsCode.append(temp);}
        while ((temp= br.readLine())!=null){
            jsCode.append("\n"+temp);
        }
        fr.close();
        br.close();
        return jsCode.toString();
    }

    public static void testFile(File file,File target){
        try {
            byte[] fileData = new byte[(int) file.length()];
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(fileData);
            fileInputStream.close();


            File fileParent=target.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            if (!target.exists()) {
                target.createNewFile();
            }
            FileOutputStream fileOutputStream=new FileOutputStream(target);
            fileOutputStream.write(fileData);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
