package com.chen1335.spellColonies.common;


import com.chen1335.spellColonies.ai.workers.guard.EntityAIMagician;
import com.minecolonies.api.colony.jobs.IJob;
import com.minecolonies.api.entity.ModEntities;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.entity.citizen.Skill;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


public class EventHandler {
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class FORGE {
        @SubscribeEvent
        public static void citizenTick(LivingEvent.LivingTickEvent event) {

            if (event.getEntity() instanceof AbstractEntityCitizen citizen) {
                IJob<?> job = citizen.getCitizenJobHandler().getColonyJob();
                if (job != null && job.getWorkerAI() instanceof EntityAIMagician aiMagician) {
                    aiMagician.magicianCombatAI.tick();
                }
            }
        }

        @SubscribeEvent
        public static void LivingIncomingDamageEvent(LivingHurtEvent event) {
            if (event.getSource().getEntity() instanceof AbstractEntityCitizen citizen) {
                IJob<?> job = citizen.getCitizenJobHandler().getColonyJob();
                if (job != null && job.getWorkerAI() instanceof EntityAIMagician) {
                    if (citizen.getCitizenData() != null) {
                        float m = (1 + (float) citizen.getCitizenData().getCitizenSkillHandler().getLevel(Skill.Intelligence) / 25);
                        event.setAmount(event.getAmount() * m);
                    }
                }
            }

            if (event.getEntity() instanceof AbstractEntityCitizen citizen && event.getSource().getEntity() instanceof LivingEntity attacker) {
                IJob<?> job = citizen.getCitizenJobHandler().getColonyJob();
                if (job != null && job.getWorkerAI() instanceof EntityAIMagician entityAIMagician) {
                    if (!attacker.isAlliedTo(citizen)) {
                        entityAIMagician.setAttackTarget(attacker);
                    }
                }
            }
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class MOD {
        @SubscribeEvent
        public static void EntityAttributeModificationEvent(EntityAttributeModificationEvent event) {
            event.add(ModEntities.CITIZEN, Attributes.ATTACK_DAMAGE);
        }
    }
}
