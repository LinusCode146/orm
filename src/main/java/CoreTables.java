import schema.Column;
import schema.Table;
import schema.constraints.ForeignKeyConstraint;

/**
 * Java equivalent of src/main/orm/example/CoreTables.kt.
 */
public final class CoreTables {
    public static final LehrerTable LEHRER = new LehrerTable();
    public static final SchuelerTable SCHUELER = new SchuelerTable();
    public static final KursTable KURS = new KursTable();
    public static final RaumTable RAUM = new RaumTable();

    private CoreTables() {
    }

    public static final class LehrerTable extends Table {
        public final Column<Integer> lehrerNr;
        public final Column<String> name;
        public final Column<String> vorname;
        public final Column<String> titel;

        private LehrerTable() {
            super("Lehrer");
            this.lehrerNr = integer("lehrer_nr").autoIncrement().primaryKey(null);
            this.name = varchar("name", 100).notNull();
            this.vorname = varchar("vorname", 100).notNull();
            this.titel = varchar("titel", 50);
        }
    }

    public static final class SchuelerTable extends Table {
        public final Column<Integer> schuelerNr;
        public final Column<String> name;
        public final Column<String> vorname;
        public final Column<String> geburtsdatum;
        public final Column<String> schuleintrittsdatum;
        public final Column<String> geschlecht;

        public final Column<String> plz;
        public final Column<String> strasseHausnummer;
        public final Column<String> wohnort;

        public final Column<Integer> tutorLehrerNr;
        public final Column<String> tutorgruppenbez;

        private SchuelerTable() {
            super("Schueler");
            this.schuelerNr = integer("schueler_nr").autoIncrement().primaryKey(null);
            this.name = varchar("name", 100).notNull();
            this.vorname = varchar("vorname", 100).notNull();
            this.geburtsdatum = varchar("geburtsdatum", 10).notNull();
            this.schuleintrittsdatum = varchar("schuleintrittsdatum", 10);
            this.geschlecht = varchar("geschlecht", 20);

            this.plz = varchar("plz", 10);
            this.strasseHausnummer = varchar("strasse_hausnummer", 150);
            this.wohnort = varchar("wohnort", 100);

            this.tutorLehrerNr = integer("tutor_lehrer_nr")
                    .references(
                            LEHRER,
                            LEHRER.lehrerNr,
                            ForeignKeyConstraint.ReferentialAction.SET_NULL,
                            ForeignKeyConstraint.ReferentialAction.NO_ACTION,
                            null
                    );
            this.tutorgruppenbez = varchar("tutorgruppenbeziehung", 100);
        }
    }

    public static final class KursTable extends Table {
        public final Column<Integer> kursNr;
        public final Column<String> thema;
        public final Column<String> typ;
        public final Column<String> fach;
        public final Column<Integer> jahrgangsstufe;
        public final Column<Integer> lehrerNr;

        private KursTable() {
            super("Kurs");
            this.kursNr = integer("kurs_nr").autoIncrement().primaryKey(null);
            this.thema = varchar("thema", 200);
            this.typ = varchar("typ", 100);
            this.fach = varchar("fach", 100).notNull();
            this.jahrgangsstufe = integer("jahrgangsstufe");
            this.lehrerNr = integer("lehrer_nr")
                    .notNull()
                    .references(
                            LEHRER,
                            LEHRER.lehrerNr,
                            ForeignKeyConstraint.ReferentialAction.RESTRICT,
                            ForeignKeyConstraint.ReferentialAction.NO_ACTION,
                            null
                    );
        }
    }

    public static final class RaumTable extends Table {
        public final Column<Integer> raumNr;
        public final Column<Integer> sitzplaetze;

        private RaumTable() {
            super("Raum");
            this.raumNr = integer("raum_nr").autoIncrement().primaryKey(null);
            this.sitzplaetze = integer("sitzplaetze");
        }
    }
}
