package com.chen1335.spellColonies.mixins.main;

import com.chen1335.spellColonies.common.events.DefineSpellTypeEvent;
import com.chen1335.spellColonies.mixins.IAbstractSpellMixin;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractSpell.class)
public class AbstractSpellMixin implements IAbstractSpellMixin {
    @Unique
    private DefineSpellTypeEvent.SpellType sc$spellType;

    @Override
    public void sc$defineType(DefineSpellTypeEvent.SpellType spellType) {
        sc$spellType = spellType;
    }

    @Override
    public DefineSpellTypeEvent.SpellType sc$getType() {
        return sc$spellType;
    }
}
