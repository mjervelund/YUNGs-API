package com.yungnickyoung.minecraft.yungsapi.module;


import com.yungnickyoung.minecraft.yungsapi.YungsApiNeoForge;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterPotion;
import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegisterUtils;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterField;
import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegistrationManager;
import com.yungnickyoung.minecraft.yungsapi.mixin.accessor.PotionAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Registration of Potions and brewing recipes.
 */
public class PotionModuleNeoForge {
    /** Brewing recipes queued via AutoRegisterUtils. NOTE: NeoForge 26.3 replaced IBrewingRecipe/RegisterBrewingRecipesEvent; registration is not ported yet. */
    public static final List<BrewingRecipe> BREWING_RECIPES = new ArrayList<>();

    public static void processEntries() {
        YungsApiNeoForge.loadingContextEventBus.addListener(YungsApiNeoForge.buildAutoRegistrar(
                Registries.POTION,
                AutoRegistrationManager.POTIONS,
                PotionModuleNeoForge::buildPotion,
                PotionModuleNeoForge::registerPotion)
        );

    }

    private static Potion buildPotion(AutoRegisterField data) {
        AutoRegisterPotion autoRegisterPotion = (AutoRegisterPotion) data.object();
        Potion potion = autoRegisterPotion.get();

        // If the potion does not have a name, set it based on the annotation data
        if (((PotionAccessor) potion).getName() == null) {
            String name = data.name().getNamespace() + "." + data.name().getPath();
            ((PotionAccessor) potion).setName(name);
        }

        return potion;
    }

    private static void registerPotion(AutoRegisterField data, Potion potion, RegisterEvent.RegisterHelper<Potion> helper) {
        // We directly reference the registry instead of using the helper so we can set the holder on the AutoRegisterPotion instance.
        // At the time of writing, the helper does not provide a way to get the holder after registration.
        Holder<Potion> holder = Registry.registerForHolder(BuiltInRegistries.POTION, data.name(), potion);
        ((AutoRegisterPotion) data.object()).setHolder(holder);
    }

    public record BrewingRecipe(Holder<Potion> input, Supplier<Item> ingredient, Holder<Potion> output) {
    }
}
