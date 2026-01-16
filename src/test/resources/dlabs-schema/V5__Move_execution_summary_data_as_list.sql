ALTER TABLE ddemo_session_step_info
ADD COLUMN new_data jsonb;

UPDATE ddemo_session_step_info SET new_data = jsonb_build_array(data);

ALTER TABLE ddemo_session_step_info
DROP COLUMN data;
