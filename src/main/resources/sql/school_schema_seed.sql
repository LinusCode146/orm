DROP TABLE IF EXISTS kurs_raum CASCADE;
DROP TABLE IF EXISTS schueler_kurs CASCADE;
DROP TABLE IF EXISTS fachraum CASCADE;
DROP TABLE IF EXISTS schueler_schulen CASCADE;
DROP TABLE IF EXISTS lehrer_faecher CASCADE;
DROP TABLE IF EXISTS kurs CASCADE;
DROP TABLE IF EXISTS schueler CASCADE;
DROP TABLE IF EXISTS lehrer CASCADE;
DROP TABLE IF EXISTS raum CASCADE;

CREATE TABLE lehrer (
    lehrer_nr SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    vorname VARCHAR(100) NOT NULL,
    titel VARCHAR(50)
);

CREATE TABLE schueler (
    schueler_nr SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    vorname VARCHAR(100) NOT NULL,
    geburtsdatum VARCHAR(10) NOT NULL,
    schuleintrittsdatum VARCHAR(10),
    geschlecht VARCHAR(20),
    plz VARCHAR(10),
    strasse_hausnummer VARCHAR(150),
    wohnort VARCHAR(100),
    tutor_lehrer_nr INTEGER,
    tutorgruppenbeziehung VARCHAR(100),
    CONSTRAINT fk_schueler_tutor
        FOREIGN KEY (tutor_lehrer_nr)
        REFERENCES lehrer(lehrer_nr)
        ON DELETE SET NULL
        ON UPDATE NO ACTION
);

CREATE TABLE kurs (
    kurs_nr SERIAL PRIMARY KEY,
    thema VARCHAR(200),
    typ VARCHAR(100),
    fach VARCHAR(100) NOT NULL,
    jahrgangsstufe INTEGER,
    lehrer_nr INTEGER NOT NULL,
    CONSTRAINT fk_kurs_lehrer
        FOREIGN KEY (lehrer_nr)
        REFERENCES lehrer(lehrer_nr)
        ON DELETE RESTRICT
        ON UPDATE NO ACTION
);

CREATE TABLE raum (
    raum_nr SERIAL PRIMARY KEY,
    sitzplaetze INTEGER
);

CREATE TABLE lehrer_faecher (
    lehrer_nr INTEGER NOT NULL,
    fach VARCHAR(100) NOT NULL,
    CONSTRAINT pk_lehrer_faecher PRIMARY KEY (lehrer_nr, fach),
    CONSTRAINT fk_lehrer_faecher_lehrer
        FOREIGN KEY (lehrer_nr)
        REFERENCES lehrer(lehrer_nr)
        ON DELETE CASCADE
        ON UPDATE NO ACTION
);

CREATE TABLE schueler_schulen (
    schueler_nr INTEGER NOT NULL,
    vorher_besuchte_schule VARCHAR(200) NOT NULL,
    CONSTRAINT pk_schueler_schulen PRIMARY KEY (schueler_nr, vorher_besuchte_schule),
    CONSTRAINT fk_schueler_schulen_schueler
        FOREIGN KEY (schueler_nr)
        REFERENCES schueler(schueler_nr)
        ON DELETE CASCADE
        ON UPDATE NO ACTION
);

CREATE TABLE fachraum (
    raum_nr INTEGER PRIMARY KEY,
    fach VARCHAR(100) NOT NULL,
    sonderausstattung TEXT,
    CONSTRAINT fk_fachraum_raum
        FOREIGN KEY (raum_nr)
        REFERENCES "raum"(raum_nr)
        ON DELETE CASCADE
        ON UPDATE NO ACTION
);

CREATE TABLE schueler_kurs (
    schueler_nr INTEGER NOT NULL,
    kurs_nr INTEGER NOT NULL,
    note INTEGER,
    fehlstunden INTEGER DEFAULT 0,
    CONSTRAINT pk_schueler_kurs PRIMARY KEY (schueler_nr, kurs_nr),
    CONSTRAINT fk_schueler_kurs_schueler
        FOREIGN KEY (schueler_nr)
        REFERENCES schueler(schueler_nr)
        ON DELETE CASCADE
        ON UPDATE NO ACTION,
    CONSTRAINT fk_schueler_kurs_kurs
        FOREIGN KEY (kurs_nr)
        REFERENCES kurs(kurs_nr)
        ON DELETE CASCADE
        ON UPDATE NO ACTION
);

CREATE TABLE kurs_raum (
    kurs_nr INTEGER NOT NULL,
    raum_nr INTEGER NOT NULL,
    zeiten VARCHAR(500),
    CONSTRAINT pk_kurs_raum PRIMARY KEY (kurs_nr, raum_nr),
    CONSTRAINT fk_kurs_raum_kurs
        FOREIGN KEY (kurs_nr)
        REFERENCES kurs(kurs_nr)
        ON DELETE CASCADE
        ON UPDATE NO ACTION,
    CONSTRAINT fk_kurs_raum_raum
        FOREIGN KEY (raum_nr)
        REFERENCES raum(raum_nr)
        ON DELETE RESTRICT
        ON UPDATE NO ACTION
);

INSERT INTO lehrer (name, vorname, titel) VALUES
    ('Meyer', 'Anna', 'Dr.'),
    ('Schmidt', 'Lukas', NULL),
    ('Klein', 'Eva', 'Prof.');

INSERT INTO lehrer_faecher (lehrer_nr, fach) VALUES
    (1, 'Mathematik'),
    (1, 'Informatik'),
    (2, 'Deutsch'),
    (3, 'Physik');

INSERT INTO schueler (
    name, vorname, geburtsdatum, schuleintrittsdatum, geschlecht,
    plz, strasse_hausnummer, wohnort, tutor_lehrer_nr, tutorgruppenbeziehung
) VALUES
    ('Mueller', 'Tom', '2008-05-11', '2018-08-20', 'männlich', '10115', 'Musterweg 1', 'Frankfurt', 1, 'Mentor'),
    ('Meier', 'Lea', '2007-09-02', '2017-08-20', 'weiblich', '10117', 'Parkstrasse 7', 'Gießen', 2, 'Mentor'),
    ('Wagner', 'Noah', '2008-01-22', '2018-08-20', 'divers', '10119', 'Rosenplatz 3', 'Kassel', 1, 'Mentor');

INSERT INTO schueler_schulen (schueler_nr, vorher_besuchte_schule) VALUES
    (1, 'Grundschule Nord'),
    (2, 'Grundschule Ost'),
    (3, 'Grundschule Nord');

INSERT INTO kurs (thema, typ, fach, jahrgangsstufe, lehrer_nr) VALUES
    ('Analysis I', 'Grundkurs', 'Mathematik', 11, 1),
    ('Datenstrukturen', 'Leistungskurs', 'Informatik', 13, 1),
    ('Lyrik', 'Grundkurs', 'Deutsch', 11, 2),
    ('Mechanik', 'Leistungskurs', 'Physik', 12, 3);

INSERT INTO raum (sitzplaetze) VALUES
    (28),
    (32),
    (20);

INSERT INTO fachraum (raum_nr, fach, sonderausstattung) VALUES
    (2, 'Informatik', 'Desktop PCs, Beamer'),
    (3, 'Physik', 'Messgeräte und Laborfläche');

INSERT INTO schueler_kurs (schueler_nr, kurs_nr, note, fehlstunden) VALUES
    (1, 1, 15, 2),
    (1, 2, 13, 0),
    (2, 1, 10, 1),
    (2, 3, 9, 0),
    (3, 2, 14, 3),
    (3, 4, 10, 2);

INSERT INTO kurs_raum (kurs_nr, raum_nr, zeiten) VALUES
    (1, 1, 'Mon 08:00-09:30'),
    (2, 2, 'Tue 10:00-11:30'),
    (3, 1, 'Wed 08:00-09:30'),
    (4, 3, 'Thu 12:00-13:30');
