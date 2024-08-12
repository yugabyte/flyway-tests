create table dcluster (
    id uuid not null,
    cluster_details jsonb,
    cluster_name character varying(255),
    data jsonb,
    demo_type character varying(255),
    seed_cluster boolean not null,
    sessions_available integer not null,
    cluster_type_id uuid,
    constraint ck_dcluster_demo_type check ( demo_type in ('GEO_PARTITIONED','RESILIENCY')),
    constraint pk_dcluster primary key (id)
);

create table dcluster_template (
    id uuid not null,
    cluster_info jsonb,
    name character varying(255),
    constraint uq_dcluster_template_name unique (name),
    constraint ck_dcluster_template_name check ( name in ('SINGLE_REGION','MULTI_REGION','GEO_PARTITIONED','RESILIENCY')),
    constraint pk_dcluster_template primary key (id)
);

create table ddemo (
    id uuid not null,
    available_for_use boolean not null,
    demo_details jsonb,
    duration_in_hours integer not null,
    name character varying(255),
    constraint uq_ddemo_name unique (name),
    constraint ck_ddemo_name check ( name in ('GEO_PARTITIONED','RESILIENCY')),
    constraint pk_ddemo primary key (id)
);

create table ddemo_cluster_templates (
    demos_id uuid not null,
    cluster_templates_id uuid not null,
    constraint pk_ddemo_cluster_templates primary key (demos_id, cluster_templates_id)
);

create table ddemo_session (
    id uuid not null,
    account_id uuid,
    demo_type character varying(255),
    end_time timestamp with time zone,
    owner_id uuid,
    progress jsonb,
    project_id uuid,
    start_time timestamp with time zone,
    state character varying(255),
    demo_id uuid,
    constraint ck_ddemo_session_demo_type check ( demo_type in ('GEO_PARTITIONED','RESILIENCY')),
    constraint ck_ddemo_session_state check ( state in ('ACTIVE','EXPIRED')),
    constraint pk_ddemo_session primary key (id)
);

create table ddemo_session_dcluster (
    demo_session_id uuid not null,
    cluster_id uuid not null,
    constraint pk_ddemo_session_dcluster primary key (demo_session_id, cluster_id)
);

create table ddemo_session_step_info (
    id uuid not null,
    chapter_name character varying(255),
    data jsonb,
    demo_session_id uuid,
    step_name character varying(255),
    constraint pk_ddemo_session_step_info primary key (id)
);

create table ddemo_steps (
    demos_id uuid not null,
    steps_id uuid not null,
    constraint pk_ddemo_steps primary key (demos_id, steps_id)
);

create table dstep (
    id uuid not null,
    action_api character varying(255),
    action_json jsonb,
    name character varying(255),
    service character varying(255),
    status_api character varying(255),
    constraint uq_dstep_name unique (name),
    constraint ck_dstep_service check ( service in ('API_SERVER','LAUNCHER_SERVICE')),
    constraint pk_dstep primary key (id)
);

create index ix_ddemo_session_step_info_ddemo_session_id_chapter_name on ddemo_session_step_info using btree (demo_session_id, chapter_name);

create index ix_dcluster_cluster_name on dcluster using btree(cluster_name);

create index ix_dcluster_sessions_available on dcluster using btree(sessions_available);

create index ix_ddemo_session_account_id on ddemo_session using btree(account_id);

create index ix_ddemo_session_project_id on ddemo_session using btree(project_id);

create index ix_ddemo_session_end_time on ddemo_session using btree(end_time);





alter table ddemo_cluster_templates add constraint fk_ddemo_cluster_templates_cluster_templates_id foreign key (cluster_templates_id) references dcluster_template(id);

alter table ddemo_steps add constraint fk_ddemo_steps_steps_id foreign key (steps_id) references dstep(id);

alter table dcluster add constraint fk_dcluster_cluster_type_id foreign key (cluster_type_id) references dcluster_template(id);

alter table ddemo_steps add constraint fk_ddemo_steps_demos_id foreign key (demos_id) references ddemo(id);

alter table ddemo_cluster_templates add constraint fk_ddemo_cluster_templates_demos_id foreign key (demos_id) references ddemo(id);

alter table ddemo_session_dcluster add constraint fk_ddemo_session_dcluster_demo_session_id foreign key (demo_session_id) references ddemo_session(id);

alter table ddemo_session add constraint fk_ddemo_session_demo_id foreign key (demo_id) references ddemo(id);
