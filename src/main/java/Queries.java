import kotlin.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import core.Database;
import core.DatabaseConfiguration;
import execution.QueryExecutor;
import execution.ResultRow;
import query.*;

public class Queries {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://ep-lively-mode-ag9j92dh-pooler.c-2.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require";
        String user = "neondb_owner";
        String password = "npg_MIqDV6lkhGL9";

        Database orm = new Database(new DatabaseConfiguration() {
            @Override
            public String getUrl() {
                return url;
            }

            @Override
            public String getUsername() {
                return user;
            }

            @Override
            public String getPassword() {
                return password;
            }
        });

        try {
            QueryExecutor executor = new QueryExecutor(orm::getConnection);
//            einfuegenBeispiele(executor);
//            updateBeispiele(executor);
            selectBeispiele(executor);
            int lehrerNr = ermittleLehrerNr(args);
            komplexeBeispieleOhneJoinSql(executor, lehrerNr);
        } finally {
            orm.closeConnection();
        }
    }

    private static int ermittleLehrerNr(String[] args) {
        if (args != null && args.length > 0) {
            try {
                return Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
                System.out.println("Ungueltige Lehrer-Nr in den Startparametern, verwende Standardwert 1.");
            }
        }
        return 1;
    }

    private static void einfuegenBeispiele(QueryExecutor executor) {
        InsertQuery<CoreTables.LehrerTable> lehrerInsert = new InsertQuery<>(CoreTables.LEHRER, executor);
        int eingefuegt = lehrerInsert
                .values(builder -> {
                    builder.setValue(CoreTables.LEHRER.name, "Beispiel");
                    builder.setValue(CoreTables.LEHRER.vorname, "Nora");
                    builder.setValue(CoreTables.LEHRER.titel, "Dr.");
                    return Unit.INSTANCE;
                })
                .execute();

        if (eingefuegt == 1) {
            System.out.println("Neue Lehrkraft hinzugefügt");
        }

        InsertQuery<CoreTables.RaumTable> raumInsert = new InsertQuery<>(CoreTables.RAUM, executor);
        int eingefuegteRaeume = raumInsert
                .values(
                        builder -> {
                            builder.setValue(CoreTables.RAUM.sitzplaetze, 30);
                            return Unit.INSTANCE;
                        },
                        builder -> {
                            builder.setValue(CoreTables.RAUM.sitzplaetze, 24);
                            return Unit.INSTANCE;
                        }
                )
                .execute();
        System.out.println("Eingefuegte Raeume: " + eingefuegteRaeume);
    }

    private static void updateBeispiele(QueryExecutor executor) {
        UpdateQuery<CoreTables.KursTable> kursUpdate = new UpdateQuery<>(CoreTables.KURS, executor);
        int aktualisiert = kursUpdate
                .set(builder -> {
                    builder.setValue(CoreTables.KURS.typ, "Leistungskurs");
                    return Unit.INSTANCE;
                })
                .where(wb -> new EqCondition<>(CoreTables.KURS.fach, "Mathematik"))
                .execute();

        System.out.println("Aktualisierte Kurse: " + aktualisiert);
    }

    private static void selectBeispiele(QueryExecutor executor) {
        SelectQuery<CoreTables.SchuelerTable> schuelerQuery = new SelectQuery<>(CoreTables.SCHUELER, executor);
        List<ResultRow> schuelerListe = schuelerQuery
                .select(CoreTables.SCHUELER.schuelerNr, CoreTables.SCHUELER.name, CoreTables.SCHUELER.vorname)
                .where(wb -> new LikeCondition(CoreTables.SCHUELER.name, "W%"))
                .orderBy(ExpressionKt.asc(CoreTables.SCHUELER.name))
                .limit(10)
                .execute();
        tabelleAusgeben("Schueler mit Namen W*", schuelerListe);

        SelectQuery<CoreTables.KursTable> kursAbKlasse12 = new SelectQuery<>(CoreTables.KURS, executor);
        List<ResultRow> kurse = kursAbKlasse12
                .select(
                        CoreTables.KURS.kursNr,
                        CoreTables.KURS.fach,
                        CoreTables.KURS.typ,
                        CoreTables.KURS.jahrgangsstufe,
                        CoreTables.KURS.lehrerNr
                )
                .where(wb -> new GreaterThanOrEqCondition<>(CoreTables.KURS.jahrgangsstufe, 12))
                .orderBy(ExpressionKt.desc(CoreTables.KURS.jahrgangsstufe), ExpressionKt.asc(CoreTables.KURS.fach))
                .execute();
        tabelleAusgeben("Kurse ab Jahrgangsstufe 12", kurse);
    }

    private static void komplexeBeispieleOhneJoinSql(QueryExecutor executor, int lehrerNr) {
        SelectQuery<RelationTables.SchuelerKursTable> noteSuche = new SelectQuery<>(RelationTables.SCHUELER_KURS, executor);
        List<ResultRow> schuelerMitStarkemKurs = noteSuche
                .select(
                        RelationTables.SCHUELER_KURS.schuelerNr,
                        RelationTables.SCHUELER_KURS.kursNr,
                        RelationTables.SCHUELER_KURS.note
                )
                .where(wb -> new GreaterThanCondition<>(RelationTables.SCHUELER_KURS.note, 13))
                .orderBy(ExpressionKt.asc(RelationTables.SCHUELER_KURS.schuelerNr), ExpressionKt.asc(RelationTables.SCHUELER_KURS.kursNr))
                .execute();

        Set<Integer> schuelerIds = schuelerMitStarkemKurs.stream()
                .map(zeile -> zeile.getInt("schueler_nr"))
                .collect(Collectors.toSet());

        if (schuelerIds.isEmpty()) {
            tabelleAusgeben("Lehrer als Mentoren von Schuelern mit Kursnote > 13", List.of());
            System.out.println("Keine Schueler mit Kursnote > 13 gefunden.");
        } else {
            SelectQuery<CoreTables.SchuelerTable> schuelerQuery = new SelectQuery<>(CoreTables.SCHUELER, executor);
            List<ResultRow> schuelerMitTutor = schuelerQuery
                    .select(
                            CoreTables.SCHUELER.schuelerNr,
                            CoreTables.SCHUELER.name,
                            CoreTables.SCHUELER.vorname,
                            CoreTables.SCHUELER.tutorLehrerNr,
                            CoreTables.SCHUELER.tutorgruppenbez
                    )
                    .where(wb -> new AndCondition(
                            new InCondition<>(CoreTables.SCHUELER.schuelerNr, new ArrayList<>(schuelerIds)),
                            new IsNotNullCondition(CoreTables.SCHUELER.tutorLehrerNr)
                    ))
                    .orderBy(ExpressionKt.asc(CoreTables.SCHUELER.name), ExpressionKt.asc(CoreTables.SCHUELER.vorname))
                    .execute();

            Set<Integer> tutorLehrerIds = schuelerMitTutor.stream()
                    .map(zeile -> zeile.getInt("tutor_lehrer_nr"))
                    .collect(Collectors.toSet());

            List<ResultRow> tutoren = tutorLehrerIds.isEmpty()
                    ? List.of()
                    : new SelectQuery<>(CoreTables.LEHRER, executor)
                    .select(
                            CoreTables.LEHRER.lehrerNr,
                            CoreTables.LEHRER.name,
                            CoreTables.LEHRER.vorname,
                            CoreTables.LEHRER.titel
                    )
                    .where(wb -> new InCondition<>(CoreTables.LEHRER.lehrerNr, new ArrayList<>(tutorLehrerIds)))
                    .orderBy(ExpressionKt.asc(CoreTables.LEHRER.name), ExpressionKt.asc(CoreTables.LEHRER.vorname))
                    .execute();

            tabelleAusgeben("Lehrer als Mentoren von Schuelern mit Kursnote > 13", tutoren);
        }

        List<ResultRow> kurseVomLehrer = new SelectQuery<>(CoreTables.KURS, executor)
                .select(CoreTables.KURS.kursNr, CoreTables.KURS.fach, CoreTables.KURS.typ, CoreTables.KURS.lehrerNr)
                .where(wb -> new EqCondition<>(CoreTables.KURS.lehrerNr, lehrerNr))
                .orderBy(ExpressionKt.asc(CoreTables.KURS.fach), ExpressionKt.asc(CoreTables.KURS.kursNr))
                .execute();

        Set<Integer> kursIds = kurseVomLehrer.stream()
                .map(zeile -> zeile.getInt("kurs_nr"))
                .collect(Collectors.toSet());

        if (kursIds.isEmpty()) {
            System.out.println("\n=== Raeume und Zeiten fuer Lehrer " + lehrerNr + " ===");
            System.out.println("Keine Kurse zu diesem Lehrer gefunden.");
            return;
        }

        List<ResultRow> raeumeUndZeiten = new SelectQuery<>(RelationTables.KURS_RAUM, executor)
                .select(RelationTables.KURS_RAUM.raumNr, RelationTables.KURS_RAUM.zeiten, RelationTables.KURS_RAUM.kursNr)
                .where(wb -> new InCondition<>(RelationTables.KURS_RAUM.kursNr, new ArrayList<>(kursIds)))
                .orderBy(ExpressionKt.asc(RelationTables.KURS_RAUM.raumNr), ExpressionKt.asc(RelationTables.KURS_RAUM.zeiten))
                .execute();

        tabelleAusgeben("Kurse von Lehrer " + lehrerNr, kurseVomLehrer);
        tabelleAusgeben("Raeume und Zeiten fuer Lehrer " + lehrerNr, raeumeUndZeiten);
    }

    private static void tabelleAusgeben(String titel, List<ResultRow> zeilen) {
        System.out.println("\n=== " + titel + " ===");
        if (zeilen.isEmpty()) {
            System.out.println("Keine Daten gefunden.");
            return;
        }
        zeilen.forEach(zeile -> System.out.println(zeile.toMap()));
    }
}
