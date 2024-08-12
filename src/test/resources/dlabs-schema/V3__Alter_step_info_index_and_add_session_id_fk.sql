drop index if exists ix_ddemo_session_step_info_ddemo_session_id_chapter_name;

alter table ddemo_session_step_info  add constraint  fk_ddemo_session_step_info_demo_session_id foreign key (demo_session_id) references ddemo_session(id) on delete cascade;

create index ix_ddemo_session_step_info_demo_session_id on ddemo_session_step_info using btree(demo_session_id);
