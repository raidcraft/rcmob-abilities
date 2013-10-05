package de.raidcraft.mobs.abilities;

import de.raidcraft.mobs.api.Mob;
import de.raidcraft.mobs.api.MobAbility;
import de.raidcraft.skills.api.ability.AbilityInformation;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.persistance.AbilityProperties;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.DamageTrigger;
import de.raidcraft.skills.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@AbilityInformation(
        name = "Knockback Resistance",
        description = "Has a chance to prevent attack knockbacks.",
        types = {EffectType.HELPFUL, EffectType.PROTECTION}
)
public class KnockbackResistance extends MobAbility implements Triggered {

    private double chance;

    public KnockbackResistance(Mob holder, AbilityProperties data) {

        super(holder, data);
    }

    @Override
    public void load(ConfigurationSection data) {

        chance = ConfigUtil.getTotalValue(this, data.getConfigurationSection("chance"));
    }

    @TriggerHandler(ignoreCancelled = true)
    public void onDamage(DamageTrigger trigger) {

        if (chance > 0 && Math.random() > chance) {
            return;
        }
        trigger.getAttack().setKnockback(false);
    }
}
