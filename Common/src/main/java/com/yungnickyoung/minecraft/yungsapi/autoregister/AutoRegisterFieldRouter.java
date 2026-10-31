package com.yungnickyoung.minecraft.yungsapi.autoregister;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.*;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.level.levelgen.feature.Feature;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

public class AutoRegisterFieldRouter {
    /** Raw class of T in MapCodec<T>, MapCodec<? extends T>, or null if unknown. */
    private static Class<?> rawTypeArgument(Type fieldType) {
        if (!(fieldType instanceof ParameterizedType pt) || pt.getActualTypeArguments().length != 1) return null;
        return rawClass(pt.getActualTypeArguments()[0]);
    }

    private static Class<?> rawClass(Type t) {
        if (t instanceof Class<?> c) return c;
        if (t instanceof ParameterizedType p) return rawClass(p.getRawType());
        if (t instanceof WildcardType w && w.getUpperBounds().length > 0) return rawClass(w.getUpperBounds()[0]);
        return null;
    }

    public static void queueField(AutoRegisterField registerData) {
        if (registerData.object() instanceof StructureType<?>) {
            AutoRegistrationManager.STRUCTURE_TYPES.add(registerData);
        } else if (registerData.object() instanceof StructurePoolElementType<?>) {
            AutoRegistrationManager.STRUCTURE_POOL_ELEMENT_TYPES.add(registerData);
        } else if (registerData.object() instanceof StructurePieceType) {
            AutoRegistrationManager.STRUCTURE_PIECE_TYPES.add(registerData);
        } else if (registerData.object() instanceof MapCodec<?>) {
            // Since 26.3 structure placements, features, placement modifiers and structure processors are registered as
            // bare MapCodecs, so the kind is read from the field's declared type argument (e.g. MapCodec<MyProcessor>).
            Class<?> target = rawTypeArgument(registerData.genericType());
            if (target != null && StructureProcessor.class.isAssignableFrom(target)) {
                AutoRegistrationManager.STRUCTURE_PROCESSOR_TYPES.add(registerData);
            } else if (target != null && StructurePlacement.class.isAssignableFrom(target)) {
                AutoRegistrationManager.STRUCTURE_PLACEMENT_TYPES.add(registerData);
            } else if (target != null && Feature.class.isAssignableFrom(target)) {
                AutoRegistrationManager.FEATURES.add(registerData);
            } else if (target != null && PlacementModifier.class.isAssignableFrom(target)) {
                AutoRegistrationManager.PLACEMENT_MODIFIER_TYPES.add(registerData);
            } else {
                throw new IllegalStateException("Cannot route AutoRegister MapCodec field " + registerData.name()
                        + ": declare it as MapCodec<T> with T a StructureProcessor, StructurePlacement, Feature or PlacementModifier (was " + registerData.genericType() + ")");
            }
        } else if (registerData.object() instanceof CriterionTrigger) {
            AutoRegistrationManager.CRITERION_TRIGGERS.add(registerData);
        } else if (registerData.object() instanceof AutoRegisterBlock) {
            AutoRegistrationManager.BLOCKS.add(registerData);
        } else if (registerData.object() instanceof AutoRegisterItem) {
            AutoRegistrationManager.ITEMS.add(registerData);
        } else if (registerData.object() instanceof AutoRegisterBlockEntityType) {
            AutoRegistrationManager.BLOCK_ENTITY_TYPES.add(registerData);
        } else if (registerData.object() instanceof AutoRegisterCreativeTab) {
            AutoRegistrationManager.CREATIVE_MODE_TABS.add(registerData);
        } else if (registerData.object() instanceof AutoRegisterEntityType) {
            AutoRegistrationManager.ENTITY_TYPES.add(registerData);
        } else if (registerData.object() instanceof EntityDataSerializer<?>) {
            AutoRegistrationManager.ENTITY_DATA_SERIALIZERS.add(registerData);
        } else if (registerData.object() instanceof AutoRegisterMobEffect) {
            AutoRegistrationManager.MOB_EFFECTS.add(registerData);
        } else if (registerData.object() instanceof AutoRegisterPotion) {
            AutoRegistrationManager.POTIONS.add(registerData);
        } else if (registerData.object() instanceof AutoRegisterParticleType<?>) {
            AutoRegistrationManager.PARTICLE_TYPES.add(registerData);
        } else if (registerData.object() instanceof AutoRegisterSoundEvent) {
            AutoRegistrationManager.SOUND_EVENTS.add(registerData);
        } else if (registerData.object() instanceof AutoRegisterCommand) {
            AutoRegistrationManager.COMMANDS.add(registerData);
        }
    }
}