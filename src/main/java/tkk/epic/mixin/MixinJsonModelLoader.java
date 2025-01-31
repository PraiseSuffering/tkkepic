package tkk.epic.mixin;

import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tkk.epic.TkkEpic;
import tkk.epic.gameasset.Animations;
import yesman.epicfight.api.model.JsonModelLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Optional;

@SuppressWarnings("InvalidInjectorMethodSignature")
@Mixin(value = JsonModelLoader.class, remap = false)
public class MixinJsonModelLoader {

    @Shadow private JsonObject rootJson;
    @Shadow private ResourceManager resourceManager;
    @Shadow private ResourceLocation resourceLocation;
    @Shadow private  String filehash;


    @Redirect(method = "Lyesman/epicfight/api/model/JsonModelLoader;<init>(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/ResourceLocation;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/ResourceManager;m_213713_(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Optional;"))
    private Optional<Resource> inject(ResourceManager instance, ResourceLocation resourceLocation) {
        if (!resourceLocation.getNamespace().equals(Animations.TKK_FILE_LOAD)) {
            return instance.getResource(resourceLocation);
        }
        try {
            String path=FMLPaths.GAMEDIR.get().toFile().getCanonicalPath() + "/" + Animations.TKK_FILE_LOAD + "/";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            Resource resource = new Resource(null, IoSupplier.create(Paths.get(file + "/" + resourceLocation.getPath())), null);
            Optional<Resource> a=Optional.ofNullable(resource);
            return a;
        }catch (Exception e){
            TkkEpic.LOGGER.log(Level.ERROR, Animations.TKK_FILE_LOAD+" error:" + e);
            StringWriter temp = new StringWriter();
            e.printStackTrace(new PrintWriter(temp,true));
            TkkEpic.LOGGER.log(Level.ERROR, temp);

        }
        return instance.getResource(resourceLocation);
    }
}
