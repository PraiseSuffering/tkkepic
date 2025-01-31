package tkk.epic.modCompat;

import net.minecraftforge.fml.ModList;
import tkk.epic.TkkEpic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class CompatManager {
    private static final Map<String, Supplier<Callable<ICompat>>> compatFactories = new HashMap();
    private static final Map<String, ICompat> loadedCompats = new HashMap();


    static {
        compatFactories.put("curios", () -> {
            return CuriosCompat::new;
        });
    }

    public static void initCompats() {
        for (Map.Entry<String, Supplier<Callable<ICompat>>> entry : compatFactories.entrySet()) {
            if (ModList.get().isLoaded(entry.getKey())) {
                try {
                    loadedCompats.put(entry.getKey(), entry.getValue().get().call());
                } catch (Exception e) {
                    TkkEpic.LOGGER.error("CompatManager initCompats Error:", e);
                    e.printStackTrace();
                }
            }
        }
        loadedCompats.values().forEach((v0) -> {
            v0.init();
        });
    }

}
