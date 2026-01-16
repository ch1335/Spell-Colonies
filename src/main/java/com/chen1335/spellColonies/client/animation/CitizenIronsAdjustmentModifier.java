package com.chen1335.spellColonies.client.animation;

import dev.kosmx.playerAnim.api.layered.modifier.AdjustmentModifier;
import io.redspace.ironsspellbooks.setup.IronsAdjustmentModifier;

import java.util.Optional;
import java.util.function.BiFunction;

public class CitizenIronsAdjustmentModifier extends IronsAdjustmentModifier {
    public static CitizenIronsAdjustmentModifier INSTANCE;

    public CitizenIronsAdjustmentModifier(BiFunction<String, Float, Optional<AdjustmentModifier.PartModifier>> transformFunction) {
        super(transformFunction);
    }
}
