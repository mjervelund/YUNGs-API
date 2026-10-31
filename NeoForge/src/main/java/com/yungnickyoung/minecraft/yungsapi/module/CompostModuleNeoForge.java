package com.yungnickyoung.minecraft.yungsapi.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;

/**
 * Handles items to be added as compostable.
 */
public class CompostModuleNeoForge {
    public static final Object2FloatMap<ItemLike> COMPOSTABLES = new Object2FloatOpenHashMap<>();

    /**
     * Registers all recipes added with {@link AutoRegisterUtils#addCompostableItem}.
     * Note that usage of the aforementioned method should be performed in a method annotated
     * with {@link AutoRegister}. This method is explicitly called after all such methods have been invoked,
     * during CommonSetup.
     */
    public static void registerCompostables() {
        // 26.3 removed ComposterBlock.COMPOSTABLES (compostables are an item data component / data map now); not ported.
        if (!COMPOSTABLES.isEmpty()) {
            com.yungnickyoung.minecraft.yungsapi.YungsApiCommon.LOGGER.warn("YUNG's API: {} compostable item(s) queued via AutoRegisterUtils were ignored (not supported on 26.3 yet)", COMPOSTABLES.size());
        }
    }
}
