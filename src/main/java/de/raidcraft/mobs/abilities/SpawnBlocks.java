package de.raidcraft.mobs.abilities;

import de.raidcraft.RaidCraft;
import de.raidcraft.mobs.api.Mob;
import de.raidcraft.mobs.api.MobAbility;
import de.raidcraft.skills.api.ability.AbilityInformation;
import de.raidcraft.skills.api.events.RCCombatEvent;
import de.raidcraft.skills.api.persistance.AbilityProperties;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.CombatTrigger;
import de.raidcraft.util.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
@AbilityInformation(
        name = "Spawn Blocks",
        description = "Spawns blocks at the defined coordinates."
)
public class SpawnBlocks extends MobAbility implements Triggered {

    private Map<Location, Material> locations = new HashMap<>();
    private Material material;

    public SpawnBlocks(Mob holder, AbilityProperties data) {

        super(holder, data);
    }

    @Override
    public void load(ConfigurationSection data) {

        material = ItemUtils.getItem(data.getString("type"));
        for (String key : data.getStringList("locations")) {
            String[] split = key.split(",");
            if (split.length >= 3) {
                Location location = new Location(getHolder().getEntity().getWorld(),
                        Integer.parseInt(split[0]),
                        Integer.parseInt(split[1]),
                        Integer.parseInt(split[2]));
                locations.put(location, location.getBlock().getType());
            } else {
                RaidCraft.LOGGER.warning("Wrong formatted location in spawn blocks config of " + getFriendlyName());
            }
        }
    }

    @TriggerHandler(ignoreCancelled = true)
    public void onCombat(CombatTrigger trigger) {

        if (material == null || locations.isEmpty()) {
            return;
        }
        if (trigger.getEvent().getType() == RCCombatEvent.Type.ENTER) {
            for (Location location : locations.keySet()) {
                location.getBlock().setType(material);
            }
        } else {
            for (Location location : locations.keySet()) {
                location.getBlock().setType(locations.get(location));
            }
        }
    }
}
