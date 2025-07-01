package com.chen1335.spellColonies;

import com.chen1335.API.jobs.MEGuardTypes;
import com.chen1335.API.jobs.MEJobs;
import com.chen1335.spellColonies.client.animation.CitizenAnimationFactory;
import com.chen1335.spellColonies.common.events.DefineSpellTypeEvent;
import com.chen1335.spellColonies.mixins.IAbstractSpellMixin;
import com.chen1335.spellColonies.network.CitizenPlayAnimationPack;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mod(SpellColonies.MODID)
public class SpellColonies {
    public static final String MODID = "spell_colonies";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceLocation EMPTY_ANIMATION = ResourceLocation.fromNamespaceAndPath(MODID, "empty_animation");

    public SpellColonies(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::commonSetup);
        MEJobs.DEFERRED_REGISTER.register(modEventBus);
        MEGuardTypes.DEFERRED_REGISTER.register(modEventBus);
        modEventBus.addListener(this::registerPayloadHandlersEvent);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        List<AbstractSpell> healOther = List.of(
                SpellRegistry.BLESSING_OF_LIFE_SPELL.get(),
                SpellRegistry.HEALING_CIRCLE_SPELL.get()
        );

        List<AbstractSpell> healSelf = List.of(
                SpellRegistry.HEAL_SPELL.get()
        );


        for (Map.Entry<ResourceKey<AbstractSpell>, AbstractSpell> entry : SpellRegistry.REGISTRY.entrySet()) {
            AbstractSpell abstractSpell = entry.getValue();
            IAbstractSpellMixin abstractSpellMixin = (IAbstractSpellMixin) abstractSpell;
            if (healOther.contains(abstractSpell)) {
                abstractSpellMixin.sc$defineType(DefineSpellTypeEvent.SpellType.HEAL_OTHER);
            } else if (healSelf.contains(abstractSpell)) {
                abstractSpellMixin.sc$defineType(DefineSpellTypeEvent.SpellType.HEAL_SELF);
            } else {
                abstractSpellMixin.sc$defineType(DefineSpellTypeEvent.SpellType.DAMAGE);
            }

            NeoForge.EVENT_BUS.post(new DefineSpellTypeEvent(abstractSpell, entry.getKey().location()));
        }
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
                                return Optional.of(new AdjustmentModifier.PartModifier(new Vec3f(x * Mth.DEG_TO_RAD, y * Mth.DEG_TO_RAD, 0), Vec3f.ZERO, Vec3f.ZERO));
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

    public void registerPayloadHandlersEvent(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(CitizenPlayAnimationPack.TYPE, CitizenPlayAnimationPack.STREAM_CODEC, CitizenPlayAnimationPack::handler);
    }
}
