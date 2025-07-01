package com.chen1335.spellColonies.ai.workers.guard;

import com.chen1335.spellColonies.SpellColonies;
import com.chen1335.spellColonies.common.events.DefineSpellTypeEvent;
import com.chen1335.spellColonies.mixins.IAbstractSpellMixin;
import com.chen1335.spellColonies.network.CitizenPlayAnimationPack;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.ITickRateStateMachine;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.entity.citizen.Skill;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.core.colony.buildings.AbstractBuildingGuards;
import com.minecolonies.core.entity.ai.combat.AttackMoveAI;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import com.minecolonies.core.entity.pathfinding.PathfindingUtils;
import com.minecolonies.core.entity.pathfinding.PathingOptions;
import com.minecolonies.core.entity.pathfinding.navigation.MinecoloniesAdvancedPathNavigate;
import com.minecolonies.core.entity.pathfinding.pathjobs.AbstractPathJob;
import com.minecolonies.core.entity.pathfinding.pathjobs.PathJobCanSee;
import com.minecolonies.core.entity.pathfinding.pathjobs.PathJobMoveAwayFromLocation;
import com.minecolonies.core.entity.pathfinding.pathjobs.PathJobMoveToLocation;
import com.minecolonies.core.entity.pathfinding.pathresults.PathResult;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.registries.DataAttachmentRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static io.redspace.ironsspellbooks.api.registry.AttributeRegistry.COOLDOWN_REDUCTION;

public class MagicianCombatAI extends AttackMoveAI<EntityCitizen> {
    private final PathingOptions combatPathingOptions;
    private SpellData preferableSpell;

    private MagicData magicData;

    public LivingEntity getCurrentTarget() {
        return target;
    }

    public MagicianCombatAI(EntityCitizen owner, ITickRateStateMachine stateMachine) {
        super(owner, stateMachine);
        if (!owner.hasData(DataAttachmentRegistry.MAGIC_DATA.get())) {
            owner.setData(DataAttachmentRegistry.MAGIC_DATA.get(), new MagicData(true));
        }
        magicData = owner.getData(DataAttachmentRegistry.MAGIC_DATA.get());
        magicData.setSyncedData(new SyncedSpellData(owner));
        combatPathingOptions = new PathingOptions();
        combatPathingOptions.setEnterDoors(true);
        combatPathingOptions.setCanOpenDoors(true);
        combatPathingOptions.setCanSwim(true);
        combatPathingOptions.withOnPathCost(0.8);
        combatPathingOptions.withJumpCost(0.01);
        combatPathingOptions.withDropCost(1.5);
    }

    private void playCastAnimation(AbstractSpell spell, boolean isStart) {
        if (!this.user.level().isClientSide) {
            Optional<ResourceLocation> optional = isStart ? spell.getCastStartAnimation().getForPlayer() : spell.getCastFinishAnimation().getForPlayer();

            if (optional.isPresent()) {
                ResourceLocation resourceLocation = optional.get();
                ServerLevel serverLevel = (ServerLevel) this.user.level();
                serverLevel.getChunkSource().broadcast(this.user, new CitizenPlayAnimationPack(this.user.getId(), resourceLocation, CitizenPlayAnimationPack.AnimationType.IRON_SPELL_BOOK.ordinal()));

            } else {
                ServerLevel serverLevel = (ServerLevel) this.user.level();
                serverLevel.getChunkSource().broadcast(this.user, new CitizenPlayAnimationPack(this.user.getId(), SpellColonies.EMPTY_ANIMATION, CitizenPlayAnimationPack.AnimationType.IRON_SPELL_BOOK.ordinal()));
            }
        }
    }


    public List<SpellData> getAvailableSpells() {
        List<SpellData> spells = new ArrayList<>();
        for (int i = 0; i < this.user.getInventoryCitizen().getSlots(); i++) {
            @NotNull ItemStack itemStack = this.user.getInventoryCitizen().getStackInSlot(i);
            if (itemStack.getItem() instanceof Scroll) {
                @NotNull SpellData spellData = ISpellContainer.getOrCreate(itemStack).getSpellAtIndex(0);
                if (!magicData.getPlayerCooldowns().isOnCooldown(spellData.getSpell())) {
                    spells.add(spellData);
                }
            }
        }
        return spells;
    }

    @Override
    protected boolean isInDistanceForAttack(LivingEntity target) {
        return true;
    }

    @Override
    public boolean isEntityValidTarget(LivingEntity target) {
        if (target instanceof IMagicSummon magicSummon && magicSummon.isAlliedHelper(this.user)) {
            return false;
        }
        return super.isEntityValidTarget(target);
    }

    @Override
    protected void doAttack(LivingEntity target) {
        if (magicData.isCasting()) {
            return;
        }

        List<SpellData> spells = getAvailableSpells();
        if (spells.isEmpty()) {
            return;
        }

        int i1 = this.user.getRandom().nextInt(spells.size());
        SpellData spellData = spells.get(i1);


        if (preferableSpell != null) {
            spellData = preferableSpell;
        }

        AbstractSpell spell = spellData.getSpell();
        int level = spellData.getLevel();

        castSpell(spell, level);
    }

    public void castSpell(AbstractSpell spell, int level) {
        if (magicData.isCasting()) {
            return;
        }

        if (this.user.getCitizenData() != null) {
            this.user.getCitizenData().getCitizenSkillHandler().addXpToSkill(Skill.Intelligence, 1, this.user.getCitizenData());
            this.user.getCitizenData().getCitizenSkillHandler().addXpToSkill(Skill.Mana, 0.5, this.user.getCitizenData());
        }

        if (target != null) {
            this.user.lookAt(target, 180, 180);
        }
        int castTime = spell.getCastTime(level);
        playCastAnimation(spell, true);
        spell.checkPreCastConditions(this.user.level(), level, this.user, magicData);
        if (castTime > 0) {
            magicData.initiateCast(spell, level, spell.getEffectiveCastTime(level, this.user), CastSource.SPELLBOOK, "");
            if (magicData.getCastType() == CastType.CONTINUOUS) {
                spell.onCast(this.user.level(), level, this.user, CastSource.MOB, this.user.getData(DataAttachmentRegistry.MAGIC_DATA.get()));
            }
        } else {
            spell.onCast(this.user.level(), level, this.user, CastSource.MOB, this.user.getData(DataAttachmentRegistry.MAGIC_DATA.get()));
            addCooldown(spell);
        }
    }

    public void addCooldown(AbstractSpell spell) {
        int cooldown = getEffectiveSpellCooldown(spell, this.user, magicData.getCastSource());
        if (this.user.getCitizenData() != null) {
            int mama = this.user.getCitizenData().getCitizenSkillHandler().getLevel(Skill.Mana);
            cooldown = (int) (cooldown * Math.max(1 - ((float) mama / 50), 0.25F));
        }
        magicData.getPlayerCooldowns().addCooldown(spell, cooldown);
    }

    @Override
    protected int getAttackDelay() {
        return 0;
    }

    @Override
    protected void onTargetDied(LivingEntity target) {
        if (magicData.isCasting()) {
            AbstractSpell spell = SpellRegistry.getSpell(magicData.getCastingSpellId());
            magicData.resetCastingState();
            if (magicData.getCastType() == CastType.CONTINUOUS) {
                magicData.getPlayerCooldowns().addCooldown(magicData.getCastingSpellId(), getEffectiveSpellCooldown(spell, this.user, magicData.getCastSource()));

            }
            playCastAnimation(SpellRegistry.getSpell(magicData.getCastingSpellId()), false);
        }
    }

    @Override
    protected PathResult<AbstractPathJob> moveInAttackPosition(final LivingEntity target) {
        if (BlockPosUtil.getDistanceSquared(target.blockPosition(), user.blockPosition()) <= 36) {
            final PathJobMoveAwayFromLocation job = new PathJobMoveAwayFromLocation(user.level(),
                    user.blockPosition(),
                    target.blockPosition(),
                    (int) 7.0,
                    (int) user.getAttribute(Attributes.FOLLOW_RANGE).getValue(),
                    user);
            final PathResult<AbstractPathJob> pathResult = ((MinecoloniesAdvancedPathNavigate) user.getNavigation()).setPathJob(job, null, getCombatMovementSpeed(), true);
            job.setPathingOptions(combatPathingOptions);
            return pathResult;
        } else if (BlockPosUtil.getDistance2D(target.blockPosition(), user.blockPosition()) >= 20) {
            final PathJobMoveToLocation job = new PathJobMoveToLocation(user.level(), PathfindingUtils.prepareStart(user), target.blockPosition(), 200, user);
            final PathResult<AbstractPathJob> pathResult = ((MinecoloniesAdvancedPathNavigate) user.getNavigation()).setPathJob(job, null, getCombatMovementSpeed(), true);
            job.setPathingOptions(combatPathingOptions);
            return pathResult;
        }
        final PathJobCanSee job = new PathJobCanSee(user, target, user.level(), ((AbstractBuildingGuards) user.getCitizenData().getWorkBuilding()).getGuardPos(), 40);
        final PathResult<AbstractPathJob> pathResult = ((MinecoloniesAdvancedPathNavigate) user.getNavigation()).setPathJob(job, null, getCombatMovementSpeed(), true);
        job.setPathingOptions(combatPathingOptions);
        return pathResult;
    }

    private double getCombatMovementSpeed() {
        return 1;
    }

    @Override
    protected double getAttackDistance() {
        return 1;
    }

    public void tick() {
        if (magicData.isCasting()) {
            AbstractSpell spell = SpellRegistry.getSpell(magicData.getCastingSpellId());
            if (target != null) {
                this.user.lookAt(target, 180, 180);
            }
            spell.onServerCastTick(this.user.level(), magicData.getCastingSpellLevel(), this.user, magicData);
            if (magicData.getCastType() == CastType.CONTINUOUS && (magicData.getCastDurationRemaining() + 1) % 10 == 0) {
                spell.onCast(this.user.level(), magicData.getCastingSpellLevel(), this.user, CastSource.SPELLBOOK, magicData);
            }
            if (magicData.getCastDurationRemaining() <= 0) {
                PlayerRecasts playerRecasts = magicData.getPlayerRecasts();
                if (!playerRecasts.hasRecastForSpell(spell)) {
                    spell.onCast(this.user.level(), magicData.getCastingSpellLevel(), this.user, CastSource.SPELLBOOK, magicData);
                    if (!playerRecasts.hasRecastForSpell(spell)) {
                        addCooldown(spell);
                    } else {
                        preferableSpell = new SpellData(spell, magicData.getCastingSpellLevel());
                    }
                } else {
                    spell.onCast(this.user.level(), magicData.getCastingSpellLevel(), this.user, CastSource.SPELLBOOK, magicData);
                    if (playerRecasts.hasRecastForSpell(spell)) {
                        playerRecasts.decrementRecastCount(spell);
                        if (!playerRecasts.hasRecastForSpell(spell)) {
                            addCooldown(spell);
                            preferableSpell = null;
                        }
                    } else {
                        addCooldown(spell);
                    }
                }
                magicData.resetCastingState();
                playCastAnimation(spell, false);
            }
            magicData.handleCastDuration();
        }


        List<SpellData> availableSpells = getAvailableSpells();
        Map<AbstractSpell, Integer> spellIntegerMap = new HashMap<>();
        for (SpellData spell : availableSpells) {
            spellIntegerMap.put(spell.getSpell(), spell.getLevel());
        }

        Integer level = spellIntegerMap.get(SpellRegistry.GREATER_HEAL_SPELL.get());

        if (this.user.getHealth() < this.user.getMaxHealth() * 0.4 && level != null) {
            castSpell(SpellRegistry.GREATER_HEAL_SPELL.get(), level);
        } else if (this.user.getHealth() < this.user.getMaxHealth() * 0.9) {
            for (SpellData availableSpell : availableSpells) {
                if (IAbstractSpellMixin.cast(availableSpell.getSpell()).sc$isType(DefineSpellTypeEvent.SpellType.HEAL_SELF)) {
                    castSpell(availableSpell.getSpell(), availableSpell.getLevel());
                    break;
                }
            }
        }

        magicData.getPlayerCooldowns().tick(1);
    }

    @Override
    public void resetTarget() {
        super.resetTarget();
        magicData.resetCastingState();
        playCastAnimation(SpellRegistry.getSpell(magicData.getCastingSpellId()), false);
    }

    public static int getEffectiveSpellCooldown(AbstractSpell spell, AbstractEntityCitizen entityCitizen, CastSource castSource) {
        double playerCooldownModifier = entityCitizen.getAttributeValue(COOLDOWN_REDUCTION);

        float itemCoolDownModifer = 1;
        if (castSource == CastSource.SWORD) {
            itemCoolDownModifer = ServerConfigs.SWORDS_CD_MULTIPLIER.get().floatValue();
        }
        return (int) (spell.getSpellCooldown() * (2 - Utils.softCapFormula(playerCooldownModifier)) * itemCoolDownModifer);
    }

    public void setAttackTarget(LivingEntity livingEntity) {
        this.user.getThreatTable().addThreat(livingEntity,0);
        target = livingEntity;
    }
}
