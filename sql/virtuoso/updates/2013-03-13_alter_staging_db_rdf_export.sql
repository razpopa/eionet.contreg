create table CR.cr3user.staging_db_rdf_export_new (
    export_id integer NOT NULL IDENTITY,
    database_id integer NOT NULL,
    export_name varchar(150) NOT NULL,
    user_name varchar(80) NOT NULL,
    query_conf long varchar NOT NULL,
    started datetime NOT NULL,
    finished datetime DEFAULT NULL,
    status varchar(30) NOT NULL,
    export_log long varchar DEFAULT NULL,
    row_count integer DEFAULT NULL,
    noof_subjects integer DEFAULT NULL,
    noof_triples integer DEFAULT NULL,
    missing_concepts long varchar DEFAULT NULL,
    UNIQUE (database_id, user_name, started),
    UNIQUE (database_id, export_name),
    PRIMARY KEY (export_id)
);

insert into CR.cr3user.staging_db_rdf_export_new (export_id, database_id, export_name, user_name, query_conf, started, finished, status, export_log, noof_subjects, noof_triples)
select export_id, database_id, export_name, user_name, query_conf, started, finished, status, export_log, noof_subjects, noof_triples from CR.cr3user.staging_db_rdf_export;

drop table CR.cr3user.staging_db_rdf_export;
alter table CR.cr3user.staging_db_rdf_export_new rename CR.cr3user.staging_db_rdf_export;