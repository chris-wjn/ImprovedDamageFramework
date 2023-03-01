package net.cwjn.idf.config.json.data;

import net.cwjn.idf.config.json.data.subtypes.AuxiliaryData;
import net.cwjn.idf.config.json.data.subtypes.DefensiveData;
import net.cwjn.idf.config.json.data.subtypes.OffensiveData;

public enum EntityDataTemplate {
    HUMANOID_STANDARD(new EntityData(null, "strike", OffensiveData.entityStandard(), DefensiveData.resistance(5, 12, 12, 12, 12, 12, 0, 24), AuxiliaryData.empty())),
    HUMANOID_STRONG(new EntityData(null, "strike", OffensiveData.entityStandard(), DefensiveData.resistance(7, 23, 20, 20, 20, 21, 0, 30), AuxiliaryData.empty())),
    MELEE_STANDARD(new EntityData(null, "strike", OffensiveData.entityStandard(), DefensiveData.resistance(5, 11, 10, 10, 10, 10, 10, 10), AuxiliaryData.empty())),
    MELEE_NETHER(),
    MELEE_END(),
    RANGED_STANDARD(new EntityData(null, "strike", OffensiveData.entityStandard(), DefensiveData.resistance(4, 9, 8, 8, 8, 8, 8, 8), AuxiliaryData.empty())),
    RANGED_NETHER(),
    RANGED_END(),
    PASSIVE_SMALL(new EntityData(null, "strike", OffensiveData.entityStandard(), DefensiveData.resistance(1, 5, 0, 0, 0, 0, 0, 0), AuxiliaryData.empty())),
    PASSIVE_MEDIUM(new EntityData(null, "strike", OffensiveData.entityStandard(), DefensiveData.resistance(2, 10, 5, 5, 5, 5, 5, 5), AuxiliaryData.empty())),
    PASSIVE_LARGE(new EntityData(null, "strike", OffensiveData.entityStandard(), DefensiveData.resistance(3, 10, 10, 10, 10, 10, 10, 10), AuxiliaryData.empty())),
    MARINE_SMALL(new EntityData(null, "strike", OffensiveData.entityStandard(), DefensiveData.resistance(1, 5, 5, 5, -10, 0, 0, 0), AuxiliaryData.empty())),
    MARINE_MEDIUM(new EntityData(null, "strike", OffensiveData.entityStandard(), DefensiveData.resistance(2, 10, 10, 10, -15, 5, 5, 5), AuxiliaryData.empty())),
    MARINE_LARGE(new EntityData(null, "strike", OffensiveData.entityStandard(), DefensiveData.resistance(3, 10, 15, 15, -20, 10, 10, 10), AuxiliaryData.empty())),


    final EntityData data;
    EntityDataTemplate(EntityData e) {
        data = e;
    }

    public OffensiveData getOffensiveData() {
        return data.oData();
    }
    public DefensiveData getDefensiveData() {
        return data.dData();
    }
    public AuxiliaryData getAuxiliaryData() {
        return data.aData();
    }

}
