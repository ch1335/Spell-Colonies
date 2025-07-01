package com.chen1335.spellColonies.common;


import com.chen1335.spellColonies.ai.workers.guard.EntityAIMagician;
import com.minecolonies.api.colony.jobs.IJob;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.entity.citizen.Skill;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class EventHandler {
    @EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
    public static class GAME {
        @SubscribeEvent
        public static void citizenTick(EntityTickEvent.Post event) {
            if (event.getEntity() instanceof AbstractEntityCitizen citizen) {
                IJob<?> job = citizen.getCitizenJobHandler().getColonyJob();
                if (job != null && job.getWorkerAI() instanceof EntityAIMagician aiMagician) {
                    aiMagician.magicianCombatAI.tick();
                }
            }
        }

        @SubscribeEvent
        public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
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
}
