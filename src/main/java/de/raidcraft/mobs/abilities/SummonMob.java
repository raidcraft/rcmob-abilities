package de.raidcraft.mobs.abilities;

import de.raidcraft.RaidCraft;
import de.raidcraft.mobs.MobManager;
import de.raidcraft.mobs.SpawnableMob;
import de.raidcraft.mobs.UnknownMobException;
import de.raidcraft.mobs.api.Mob;
import de.raidcraft.mobs.api.MobAbility;
import de.raidcraft.skills.api.ability.AbilityInformation;
import de.raidcraft.skills.api.ability.Useable;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.AbilityProperties;
import de.raidcraft.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@AbilityInformation(
        name = "Summon Mob",
        description = "Summons mobs into the casters party.",
        types = {EffectType.HEALING, EffectType.SUMMON}
)
public class SummonMob extends MobAbility implements Useable {

    private SpawnableMob spawnableMob;
    private int amount;

    public SummonMob(Mob holder, AbilityProperties data) {

        super(holder, data);
    }

    @Override
    public void load(ConfigurationSection data) {

        try {
            spawnableMob = RaidCraft.getComponent(MobManager.class).getSpwanableMob(data.getString("mob"));
        } catch (UnknownMobException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }

    @Override
    public void use() throws CombatException {

        if (spawnableMob == null) {
            return;
        }
        Location location = getHolder().getEntity().getLocation();
        for (int i = 0; i < amount; i++) {
            Location spawnLoc;
            int spawnIterations = 0;
            do {
                spawnLoc = location.clone().add(MathUtil.RANDOM.nextInt(3), 0, MathUtil.RANDOM.nextInt(3));
                spawnIterations++;
                if (spawnIterations > 50) {
                    // we tried all locations
                    return;
                }
            }
            while (spawnLoc.getBlock().getType() != Material.AIR && spawnLoc.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR);
            spawnableMob.spawn(spawnLoc);
        }
    }
}
