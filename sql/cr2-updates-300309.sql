alter table HARVEST_SOURCE change column SCHEDULE_CRON INTERVAL_MINUTES bigint(20) not null default 0;