import schema.Column;
import schema.Table;
import schema.constraints.ForeignKeyConstraint;

/**
 * Java equivalent of src/main/orm/example/RelationTables.kt.
 */
public final class RelationTables {
    public static final LehrerFaecherTable LEHRER_FAECHER = new LehrerFaecherTable();
    public static final SchuelerSchulenTable SCHUELER_SCHULEN = new SchuelerSchulenTable();
    public static final FachraumTable FACHRAUM = new FachraumTable();
    public static final SchuelerKursTable SCHUELER_KURS = new SchuelerKursTable();
    public static final KursRaumTable KURS_RAUM = new KursRaumTable();

    private RelationTables() {
    }

    public static final class LehrerFaecherTable extends Table {
        public final Column<Integer> lehrerNr;
        public final Column<String> fach;

        private LehrerFaecherTable() {
            super("Lehrer_faecher");
            this.lehrerNr = integer("lehrer_nr")
                    .references(
                            CoreTables.LEHRER,
                            CoreTables.LEHRER.lehrerNr,
                            ForeignKeyConstraint.ReferentialAction.CASCADE,
                            ForeignKeyConstraint.ReferentialAction.NO_ACTION,
                            null
                    );
            this.fach = varchar("fach", 100).notNull();

            primaryKey(new Column[]{this.lehrerNr, this.fach}, "pk_lehrer_faecher");
        }
    }

    public static final class SchuelerSchulenTable extends Table {
        public final Column<Integer> schuelerNr;
        public final Column<String> schule;

        private SchuelerSchulenTable() {
            super("Schueler_schulen");
            this.schuelerNr = integer("schueler_nr")
                    .references(
                            CoreTables.SCHUELER,
                            CoreTables.SCHUELER.schuelerNr,
                            ForeignKeyConstraint.ReferentialAction.CASCADE,
                            ForeignKeyConstraint.ReferentialAction.NO_ACTION,
                            null
                    );
            this.schule = varchar("vorher_besuchte_schule", 200).notNull();

            primaryKey(new Column[]{this.schuelerNr, this.schule}, "pk_schueler_schulen");
        }
    }

    public static final class FachraumTable extends Table {
        public final Column<Integer> raumNr;
        public final Column<String> fach;
        public final Column<String> sonderausstattung;

        private FachraumTable() {
            super("Fachraum");
            this.raumNr = integer("raum_nr")
                    .primaryKey(null)
                    .references(
                            CoreTables.RAUM,
                            CoreTables.RAUM.raumNr,
                            ForeignKeyConstraint.ReferentialAction.CASCADE,
                            ForeignKeyConstraint.ReferentialAction.NO_ACTION,
                            null
                    );
            this.fach = varchar("fach", 100).notNull();
            this.sonderausstattung = text("sonderausstattung");
        }
    }

    public static final class SchuelerKursTable extends Table {
        public final Column<Integer> schuelerNr;
        public final Column<Integer> kursNr;
        public final Column<Integer> note;
        public final Column<Integer> fehlstunden;

        private SchuelerKursTable() {
            super("Schueler_kurs");
            this.schuelerNr = integer("schueler_nr")
                    .references(
                            CoreTables.SCHUELER,
                            CoreTables.SCHUELER.schuelerNr,
                            ForeignKeyConstraint.ReferentialAction.CASCADE,
                            ForeignKeyConstraint.ReferentialAction.NO_ACTION,
                            null
                    );
            this.kursNr = integer("kurs_nr")
                    .references(
                            CoreTables.KURS,
                            CoreTables.KURS.kursNr,
                            ForeignKeyConstraint.ReferentialAction.CASCADE,
                            ForeignKeyConstraint.ReferentialAction.NO_ACTION,
                            null
                    );

            this.note = integer("note");
            this.fehlstunden = integer("fehlstunden").useDefault(0);

            primaryKey(new Column[]{this.schuelerNr, this.kursNr}, "pk_schueler_kurs");
        }
    }

    public static final class KursRaumTable extends Table {
        public final Column<Integer> kursNr;
        public final Column<Integer> raumNr;
        public final Column<String> zeiten;

        private KursRaumTable() {
            super("Kurs_raum");
            this.kursNr = integer("kurs_nr")
                    .references(
                            CoreTables.KURS,
                            CoreTables.KURS.kursNr,
                            ForeignKeyConstraint.ReferentialAction.CASCADE,
                            ForeignKeyConstraint.ReferentialAction.NO_ACTION,
                            null
                    );
            this.raumNr = integer("raum_nr")
                    .references(
                            CoreTables.RAUM,
                            CoreTables.RAUM.raumNr,
                            ForeignKeyConstraint.ReferentialAction.RESTRICT,
                            ForeignKeyConstraint.ReferentialAction.NO_ACTION,
                            null
                    );
            this.zeiten = varchar("zeiten", 500);

            primaryKey(new Column[]{this.kursNr, this.raumNr}, "pk_kurs_raum");
        }
    }
}
