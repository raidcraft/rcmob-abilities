package de.raidcraft.mobs.abilities;

import de.raidcraft.mobs.api.Mob;
import de.raidcraft.mobs.api.MobAbility;
import de.raidcraft.skills.api.ability.AbilityInformation;
import de.raidcraft.skills.api.ability.Useable;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.HealAction;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.AbilityProperties;
import de.raidcraft.util.MathUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
@AbilityInformation(
        name = "Heal Random Target",
        description = "Heals a random target in the group",
        types = {EffectType.HELPFUL, EffectType.HEALING},
        elements = {EffectElement.HOLY}
)
public class HealRandomTarget extends MobAbility implements Useable {

    private boolean healLowestTarget;
    private boolean smartHealing;

    public HealRandomTarget(Mob holder, AbilityProperties data) {

        super(holder, data);
    }

    @Override
    public void load(ConfigurationSection data) {

        healLowestTarget = data.getBoolean("heal-lowest", false);
        smartHealing = data.getBoolean("smart-healing", true);
    }

    @Override
    public void use() throws CombatException {

        List<CharacterTemplate> members = new ArrayList<>(getHolder().getParty().getMembers());
        if (members.isEmpty()) {
            return;
        }

        CharacterTemplate characterTemplate = null;
        if (healLowestTarget) {
            for (CharacterTemplate member : members) {
                if (characterTemplate == null) {
                    characterTemplate = member;
                } else if (member.getHealth() < characterTemplate.getHealth()) {
                    characterTemplate = member;
                }
            }
        } else {
            characterTemplate = members.get(MathUtil.RANDOM.nextInt(members.size()));
        }

        int healAmount = getTotalDamage();
        if (characterTemplate == null || (smartHealing && characterTemplate.getHealth() > healAmount)) {
            throw new CombatException(CombatException.Type.CANCELLED);
        }
        new HealAction<>(this, characterTemplate, healAmount).run();
    }
}
