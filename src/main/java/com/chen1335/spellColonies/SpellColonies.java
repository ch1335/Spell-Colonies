package com.chen1335.spellColonies;

import com.chen1335.spellColonies.API.jobs.MEGuardTypes;
import com.chen1335.spellColonies.API.jobs.MEJobs;
import com.chen1335.spellColonies.client.animation.CitizenAnimationFactory;
import com.chen1335.spellColonies.common.events.DefineSpellTypeEvent;
import com.chen1335.spellColonies.mixins.IAbstractSpellMixin;
import com.chen1335.spellColonies.network.CitizenPlayAnimationPack;
import com.chen1335.spellColonies.network.ModMessages;
import com.mojang.logging.LogUtils;


import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AdjustmentModifier;
import dev.kosmx.playerAnim.core.util.Vec3f;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.setup.IronsAdjustmentModifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mod(SpellColonies.MODID)
public class SpellColonies {
    public static final String MODID = "spell_colonies";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceLocation EMPTY_ANIMATION = ResourceLocation.fromNamespaceAndPath(MODID, "empty_animation");



    public SpellColonies(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::commonSetup);
        MEJobs.DEFERRED_REGISTER.register(modEventBus);
        MEGuardTypes.DEFERRED_REGISTER.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        List<AbstractSpell> healOther = List.of(
                SpellRegistry.BLESSING_OF_LIFE_SPELL.get(),
                SpellRegistry.HEALING_CIRCLE_SPELL.get()
        );

        List<AbstractSpell> healSelf = List.of(
                SpellRegistry.HEAL_SPELL.get()
        );


        for (Map.Entry<ResourceKey<AbstractSpell>, AbstractSpell> entry : SpellRegistry.REGISTRY.get().getEntries()) {
            AbstractSpell abstractSpell = entry.getValue();
            IAbstractSpellMixin abstractSpellMixin = (IAbstractSpellMixin) abstractSpell;
            if (healOther.contains(abstractSpell)) {
                abstractSpellMixin.sc$defineType(DefineSpellTypeEvent.SpellType.HEAL_OTHER);
            } else if (healSelf.contains(abstractSpell)) {
                abstractSpellMixin.sc$defineType(DefineSpellTypeEvent.SpellType.HEAL_SELF);
            } else {
                abstractSpellMixin.sc$defineType(DefineSpellTypeEvent.SpellType.DAMAGE);
            }


            MinecraftForge.EVENT_BUS.post(new DefineSpellTypeEvent(abstractSpell, entry.getKey().location()));
        }

        ModMessages.register();
    }


    private void clientSetup(final FMLClientSetupEvent event) {
        CitizenAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                SpellAnimations.ANIMATION_RESOURCE,
                42,
                (abstractEntityCitizen) -> {
                    var animation = new ModifierLayer<>();
                    IronsAdjustmentModifier.INSTANCE = new IronsAdjustmentModifier((partName, partialTick) -> {
                        boolean handleHead = animation.getAnimation() != null && !animation.getAnimation().get3DTransform("head", TransformType.ROTATION, 0.5f, Vec3f.ZERO).equals(Vec3f.ZERO);
                        switch (partName) {
                            case "head" -> {
                                if (handleHead) {
                                    return Optional.of(new AdjustmentModifier.PartModifier(new Vec3f(0, Mth.lerp(partialTick, (abstractEntityCitizen.yHeadRotO - abstractEntityCitizen.yBodyRotO), (abstractEntityCitizen.yHeadRot - abstractEntityCitizen.yBodyRot)) * Mth.DEG_TO_RAD, 0), Vec3f.ZERO));
                                } else {
                                    return Optional.empty();
                                }
                            }
                            case "rightArm", "leftArm" -> {
                                float x = Mth.lerp(partialTick, abstractEntityCitizen.xRotO, abstractEntityCitizen.getXRot());
                                float y = Mth.lerp(partialTick, (abstractEntityCitizen.yHeadRotO - abstractEntityCitizen.yBodyRotO), (abstractEntityCitizen.yHeadRot - abstractEntityCitizen.yBodyRot));
                                return Optional.of(new AdjustmentModifier.PartModifier(new Vec3f(x * Mth.DEG_TO_RAD, y * Mth.DEG_TO_RAD, 0), Vec3f.ZERO));
                            }
                            default -> {
                                return Optional.empty();
                            }
                        }
                    });
                    animation.addModifier(IronsAdjustmentModifier.INSTANCE, 0);
                    return animation;
                });
    }

}
