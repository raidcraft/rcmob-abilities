package de.raidcraft.mobs.abilities;

import de.raidcraft.mobs.api.Mob;
import de.raidcraft.mobs.api.MobAbility;
import de.raidcraft.skills.api.ability.AbilityInformation;
import de.raidcraft.skills.api.ability.Useable;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.EntityAttack;
import de.raidcraft.skills.api.combat.action.HealAction;
import de.raidcraft.skills.api.combat.callback.EntityAttackCallback;
import de.raidcraft.skills.api.effect.common.SunderingArmor;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.AbilityProperties;
import de.raidcraft.skills.effects.Bleed;
import de.raidcraft.skills.effects.Burn;
import de.raidcraft.skills.effects.Poison;
import de.raidcraft.skills.effects.Slow;
import de.raidcraft.skills.effects.Weakness;
import de.raidcraft.skills.effects.disabling.Disarm;
import de.raidcraft.skills.effects.disabling.Interrupt;
import de.raidcraft.skills.effects.disabling.KnockBack;
import de.raidcraft.skills.effects.disabling.Stun;
import de.raidcraft.skills.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

/**
 * @author Silthus
 */
@AbilityInformation(
        name = "Magic Bolt",
        description = "Greift ein Ziel mit Magie an.",
        types = {EffectType.MAGICAL, EffectType.SILENCABLE, EffectType.HARMFUL, EffectType.DAMAGING}
)
public class MagicBolt extends MobAbility implements Useable {

    private boolean knockBack = false;
    private boolean bleed = false;
    private boolean stun = false;
    private boolean sunderArmor = false;
    private boolean disarm = false;
    private boolean slow = false;
    private boolean weaken = false;
    private boolean burn = false;
    private boolean interrupt = false;
    private boolean disable = false;
    private boolean poison = false;
    private double throwUp;
    private ConfigurationSection entityDamageBonus;
    private ConfigurationSection lifeLeech;

    public MagicBolt(Mob holder, AbilityProperties data) {

        super(holder, data);
    }

    @Override
    public void load(ConfigurationSection data) {

        addElements(EffectElement.fromString(data.getString("element")));
        if (data.getBoolean("default-attack", false)) {
            addTypes(EffectType.DEFAULT_ATTACK);
        }
        knockBack = data.getBoolean("knockback", false);
        bleed = data.getBoolean("bleed", false);
        stun = data.getBoolean("stun", false);
        sunderArmor = data.getBoolean("sunder-armor", false);
        disarm = data.getBoolean("disarm", false);
        slow = data.getBoolean("slow", false);
        weaken = data.getBoolean("weaken", false);
        burn = data.getBoolean("burn", false);
        interrupt = data.getBoolean("interrupt", false);
        disable = data.getBoolean("disable", false);
        poison = data.getBoolean("poison", false);
        throwUp = data.getDouble("throw-up", 0.0);
        lifeLeech = data.getConfigurationSection("life-leech");
        entityDamageBonus = data.getConfigurationSection("entity-damage-bonus");
    }

    private double getLifeLeechPercentage() {

        return ConfigUtil.getTotalValue(this, lifeLeech);
    }

    @Override
    public void use() throws CombatException {

        magicalAttack(new EntityAttackCallback() {
            @Override
            public void run(EntityAttack attack) throws CombatException {

                if (knockBack) MagicBolt.this.addEffect(getHolder().getEntity().getLocation(), attack.getTarget(), KnockBack.class);
                if (bleed) MagicBolt.this.addEffect(attack.getTarget(), Bleed.class);
                if (stun) MagicBolt.this.addEffect(attack.getTarget(), Stun.class);
                if (sunderArmor) MagicBolt.this.addEffect(attack.getTarget(), SunderingArmor.class);
                if (disarm) MagicBolt.this.addEffect(attack.getTarget(), Disarm.class);
                if (slow) MagicBolt.this.addEffect(attack.getTarget(), Slow.class);
                if (weaken) MagicBolt.this.addEffect(attack.getTarget(), Weakness.class);
                if (burn) MagicBolt.this.addEffect(attack.getTarget(), Burn.class);
                if (interrupt) MagicBolt.this.addEffect(attack.getTarget(), Interrupt.class);
                if (poison) MagicBolt.this.addEffect(attack.getTarget(), Poison.class);
                if (throwUp > 0.0) {
                    attack.getTarget().getEntity().setVelocity(new Vector(0, throwUp, 0));
                }
                if (lifeLeech != null) {
                    new HealAction<>(this, getHolder(), (int) (attack.getDamage() * getLifeLeechPercentage())).run();
                }
                if (!(attack.getTarget() instanceof Hero)) {
                    attack.setDamage((int) (attack.getDamage() + ConfigUtil.getTotalValue(MagicBolt.this, entityDamageBonus)));
                }
            }
        });
    }
}
