TYPE=VIEW
query=(select lcase(`performance_schema`.`global_status`.`VARIABLE_NAME`) AS `Variable_name`,`performance_schema`.`global_status`.`VARIABLE_VALUE` AS `Variable_value`,\'Global Status\' AS `Type`,\'YES\' AS `Enabled` from `performance_schema`.`global_status`) union all (select `information_schema`.`innodb_metrics`.`NAME` AS `Variable_name`,`information_schema`.`innodb_metrics`.`COUNT` AS `Variable_value`,concat(\'InnoDB Metrics - \',`information_schema`.`innodb_metrics`.`SUBSYSTEM`) AS `Type`,\'YES\' AS `Enabled` from `information_schema`.`innodb_metrics` where `information_schema`.`innodb_metrics`.`NAME` not in (\'lock_row_lock_time\',\'lock_row_lock_time_avg\',\'lock_row_lock_time_max\',\'lock_row_lock_waits\',\'buffer_pool_reads\',\'buffer_pool_read_requests\',\'buffer_pool_write_requests\',\'buffer_pool_wait_free\',\'buffer_pool_read_ahead\',\'buffer_pool_read_ahead_evicted\',\'buffer_pool_pages_total\',\'buffer_pool_pages_misc\',\'buffer_pool_pages_data\',\'buffer_pool_bytes_data\',\'buffer_pool_pages_dirty\',\'buffer_pool_bytes_dirty\',\'buffer_pool_pages_free\',\'buffer_pages_created\',\'buffer_pages_written\',\'buffer_pages_read\',\'buffer_data_reads\',\'buffer_data_written\',\'file_num_open_files\',\'os_log_bytes_written\',\'os_log_fsyncs\',\'os_log_pending_fsyncs\',\'os_log_pending_writes\',\'log_waits\',\'log_write_requests\',\'log_writes\',\'innodb_dblwr_writes\',\'innodb_dblwr_pages_written\',\'innodb_page_size\')) union all (select \'NOW()\' AS `Variable_name`,current_timestamp(3) AS `Variable_value`,\'System Time\' AS `Type`,\'YES\' AS `Enabled`) union all (select \'UNIX_TIMESTAMP()\' AS `Variable_name`,round(unix_timestamp(current_timestamp(3)),3) AS `Variable_value`,\'System Time\' AS `Type`,\'YES\' AS `Enabled`) order by `Type`,`Variable_name`
md5=4eec971a353c3babe46668653b2280ff
updatable=0
algorithm=2
definer_user=mariadb.sys
definer_host=localhost
suid=0
with_check_option=0
timestamp=2022-05-13 08:13:37
create-version=2
source=(\nSELECT LOWER(VARIABLE_NAME) AS Variable_name, VARIABLE_VALUE AS Variable_value, \'Global Status\' AS Type, \'YES\' AS Enabled\n  FROM performance_schema.global_status\n) UNION ALL (\nSELECT NAME AS Variable_name, COUNT AS Variable_value,\n       CONCAT(\'InnoDB Metrics - \', SUBSYSTEM) AS Type,\n      \'YES\' AS Enabled\n  FROM information_schema.INNODB_METRICS\n WHERE NAME NOT IN (\n     \'lock_row_lock_time\', \'lock_row_lock_time_avg\', \'lock_row_lock_time_max\', \'lock_row_lock_waits\',\n     \'buffer_pool_reads\', \'buffer_pool_read_requests\', \'buffer_pool_write_requests\', \'buffer_pool_wait_free\',\n     \'buffer_pool_read_ahead\', \'buffer_pool_read_ahead_evicted\', \'buffer_pool_pages_total\', \'buffer_pool_pages_misc\',\n     \'buffer_pool_pages_data\', \'buffer_pool_bytes_data\', \'buffer_pool_pages_dirty\', \'buffer_pool_bytes_dirty\',\n     \'buffer_pool_pages_free\', \'buffer_pages_created\', \'buffer_pages_written\', \'buffer_pages_read\',\n     \'buffer_data_reads\', \'buffer_data_written\', \'file_num_open_files\',\n     \'os_log_bytes_written\', \'os_log_fsyncs\', \'os_log_pending_fsyncs\', \'os_log_pending_writes\',\n     \'log_waits\', \'log_write_requests\', \'log_writes\', \'innodb_dblwr_writes\', \'innodb_dblwr_pages_written\', \'innodb_page_size\')\n) \n  UNION ALL (\nSELECT \'NOW()\' AS Variable_name, NOW(3) AS Variable_value, \'System Time\' AS Type, \'YES\' AS Enabled\n) UNION ALL (\nSELECT \'UNIX_TIMESTAMP()\' AS Variable_name, ROUND(UNIX_TIMESTAMP(NOW(3)), 3) AS Variable_value, \'System Time\' AS Type, \'YES\' AS Enabled\n)\n ORDER BY Type, Variable_name;
client_cs_name=utf8mb3
connection_cl_name=utf8mb3_general_ci
view_body_utf8=(select lcase(`performance_schema`.`global_status`.`VARIABLE_NAME`) AS `Variable_name`,`performance_schema`.`global_status`.`VARIABLE_VALUE` AS `Variable_value`,\'Global Status\' AS `Type`,\'YES\' AS `Enabled` from `performance_schema`.`global_status`) union all (select `information_schema`.`innodb_metrics`.`NAME` AS `Variable_name`,`information_schema`.`innodb_metrics`.`COUNT` AS `Variable_value`,concat(\'InnoDB Metrics - \',`information_schema`.`innodb_metrics`.`SUBSYSTEM`) AS `Type`,\'YES\' AS `Enabled` from `information_schema`.`innodb_metrics` where `information_schema`.`innodb_metrics`.`NAME` not in (\'lock_row_lock_time\',\'lock_row_lock_time_avg\',\'lock_row_lock_time_max\',\'lock_row_lock_waits\',\'buffer_pool_reads\',\'buffer_pool_read_requests\',\'buffer_pool_write_requests\',\'buffer_pool_wait_free\',\'buffer_pool_read_ahead\',\'buffer_pool_read_ahead_evicted\',\'buffer_pool_pages_total\',\'buffer_pool_pages_misc\',\'buffer_pool_pages_data\',\'buffer_pool_bytes_data\',\'buffer_pool_pages_dirty\',\'buffer_pool_bytes_dirty\',\'buffer_pool_pages_free\',\'buffer_pages_created\',\'buffer_pages_written\',\'buffer_pages_read\',\'buffer_data_reads\',\'buffer_data_written\',\'file_num_open_files\',\'os_log_bytes_written\',\'os_log_fsyncs\',\'os_log_pending_fsyncs\',\'os_log_pending_writes\',\'log_waits\',\'log_write_requests\',\'log_writes\',\'innodb_dblwr_writes\',\'innodb_dblwr_pages_written\',\'innodb_page_size\')) union all (select \'NOW()\' AS `Variable_name`,current_timestamp(3) AS `Variable_value`,\'System Time\' AS `Type`,\'YES\' AS `Enabled`) union all (select \'UNIX_TIMESTAMP()\' AS `Variable_name`,round(unix_timestamp(current_timestamp(3)),3) AS `Variable_value`,\'System Time\' AS `Type`,\'YES\' AS `Enabled`) order by `Type`,`Variable_name`
mariadb-version=100703
