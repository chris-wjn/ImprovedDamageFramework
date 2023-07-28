package net.cwjn.idf.config.json.records;

import net.cwjn.idf.config.json.records.subtypes.AuxiliaryData;
import net.cwjn.idf.config.json.records.subtypes.DefenceData;
import net.cwjn.idf.config.json.records.subtypes.OffenseData;

public enum EntityTag {

    //Size
    TINY(OffenseData.setForce(3), DefenceData.weightAndPhysical(2, 5), AuxiliaryData.create(0, 1.1, 0)),
    SMALL(OffenseData.setForce(4), DefenceData.weightAndPhysical(3, 7), AuxiliaryData.empty()),
    MEDIUM(OffenseData.setForce(7), DefenceData.weightAndPhysical(5, 11), AuxiliaryData.empty()),
    LARGE(OffenseData.setForce(8), DefenceData.weightAndPhysical(6, 14), AuxiliaryData.empty()),
    GIANT(OffenseData.setForce(10), DefenceData.weightAndPhysical(8, 20), AuxiliaryData.create(0, 0.9, 0)),

    //Origin
    AETHER(OffenseData.empty(), DefenceData.resistance(-1, 0, 0, 0, 10, 0, -10, 10), AuxiliaryData.empty()),
    NETHER(OffenseData.empty(), DefenceData.resistance(0, 0, 10, -10, 0, 0, 0, 0), AuxiliaryData.empty()),
    END(OffenseData.empty(), DefenceData.resistance(0, 0, 0, -10, 0, 10, 0, 0), AuxiliaryData.empty()),
    MAGICAL(OffenseData.empty(), DefenceData.resistance(0, -10, 0, 0, 0, 10, 0, 0), AuxiliaryData.empty()),
    AQUATIC(OffenseData.empty(), DefenceData.resistance(0, 0, 10, 10, -20, 0, 0, 0), AuxiliaryData.empty()),
    LIVING(OffenseData.empty(), DefenceData.resistance(0, 0, 0, 0, 0, 0, -10, 10), AuxiliaryData.empty()),
    UNDEAD(OffenseData.empty(), DefenceData.resistance(0, 0, 0, 0, 0, 0, 10, -10), AuxiliaryData.empty()),

    //Strengths and Weaknesses
    SHELLED(OffenseData.empty(), DefenceData.dmgClass(0.0, 0.0, -0.25), AuxiliaryData.empty()),
    SOFT(OffenseData.empty(), DefenceData.dmgClass(0.0, 0.0, 0.25), AuxiliaryData.empty()),
    STURDY(OffenseData.empty(), DefenceData.dmgClass(-0.25, 0.0, 0.0), AuxiliaryData.empty()),
    FRAGILE(OffenseData.empty(), DefenceData.dmgClass(0.25, 0.0, 0.0), AuxiliaryData.empty()),
    IMPENETRABLE(OffenseData.empty(), DefenceData.dmgClass(0.0, -0.25, 0.0), AuxiliaryData.empty()),
    PENETRABLE(OffenseData.empty(), DefenceData.dmgClass(0.0, 0.25, 0.0), AuxiliaryData.empty()),
    FLAMMABLE(OffenseData.empty(), DefenceData.resistance(0, 0, -10, 0, 0, 0, 0, 0), AuxiliaryData.empty()),

    //Traits
    EVASIVE(OffenseData.empty(), new DefenceData(0, 0, 0, 0, 0, 0, 0, 0, 25, 0, 0, 0, 0), AuxiliaryData.empty());

    final OffenseData oData;
    final DefenceData dData;
    final AuxiliaryData aData;
    EntityTag(OffenseData oData, DefenceData dData, AuxiliaryData aData) {
        this.oData = oData;
        this.dData = dData;
        this.aData = aData;
    }
    public OffenseData getOffensiveData() {
        return oData;
    }
    public DefenceData getDefensiveData() {
        return dData;
    }
    public AuxiliaryData getAuxiliaryData() {
        return aData;
    }

}
