package de.raidcraft.mobs.abilities;

import de.raidcraft.RaidCraft;
import de.raidcraft.mobs.MobManager;
import de.raidcraft.mobs.SpawnableMob;
import de.raidcraft.mobs.UnknownMobException;
import de.raidcraft.mobs.api.Mob;
import de.raidcraft.mobs.api.MobAbility;
import de.raidcraft.skills.api.ability.AbilityInformation;
import de.raidcraft.skills.api.ability.Useable;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.events.RCCombatEvent;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.AbilityProperties;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.CombatTrigger;
import de.raidcraft.skills.trigger.EntityDeathTrigger;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
@AbilityInformation(
        name = "Summon Mob",
        description = "Summons mobs into the casters party.",
        types = {EffectType.HEALING, EffectType.SUMMON}
)
public class SummonMob extends MobAbility implements Useable, Triggered {

    private final List<CharacterTemplate> spawnedMobs = new ArrayList<>();
    private SpawnableMob spawnableMob;
    private boolean killOnDeath;
    private boolean killOnCombatLeave;
    private int amount;

    public SummonMob(Mob holder, AbilityProperties data) {

        super(holder, data);
    }

    @Override
    public void load(ConfigurationSection data) {

        try {
            spawnableMob = RaidCraft.getComponent(MobManager.class).getSpwanableMob(data.getString("mob"));
            amount = data.getInt("amount", 1);
            killOnDeath = data.getBoolean("kill-on-death", false);
            killOnCombatLeave = data.getBoolean("kill-on-combat-leave", true);
        } catch (UnknownMobException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }

    @TriggerHandler(ignoreCancelled = true)
    public void onDeath(EntityDeathTrigger trigger) {

        if (killOnDeath) {
            for (CharacterTemplate characterTemplate : spawnedMobs) {
                characterTemplate.kill(getHolder());
            }
        }
        spawnedMobs.clear();
    }

    @TriggerHandler(ignoreCancelled = true)
    public void onCombatLeave(CombatTrigger trigger) {

        if (trigger.getEvent().getType() == RCCombatEvent.Type.LEAVE) {
            if (killOnCombatLeave) {
                for (CharacterTemplate characterTemplate : spawnedMobs) {
                    characterTemplate.kill(getHolder());
                }
            }
            spawnedMobs.clear();
        }
    }

    @Override
    public void use() throws CombatException {

        if (spawnableMob == null) {
            return;
        }
        for (int i = 0; i < amount; i++) {
            spawnedMobs.addAll(spawnableMob.spawn(getHolder().getEntity().getLocation()));
        }
    }
}
