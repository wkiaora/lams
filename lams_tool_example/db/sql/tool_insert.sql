-- CVS ID: $Id$

INSERT INTO lams_tool
(
tool_signature,
service_name,
tool_display_name,
description,
tool_identifier,
tool_version,
learning_library_id,
default_tool_content_id,
valid_flag,
grouping_support_type_id,
supports_run_offline_flag,
learner_url,
learner_preview_url,
learner_progress_url,
author_url,
monitor_url,
define_later_url,
export_pfolio_learner_url,
export_pfolio_class_url,
contribute_url,
moderation_url,
help_url,
language_file,
classpath_addition,
context_file,
create_date_time,
modified_date_time
)
VALUES
(
'laex11',
'exampleService',
'Example',
'Example',
'example',
'@tool_version@',
NULL,
NULL,
0,
2,
1,
'tool/laex11/learning/viewForum.do?mode=learner',
'tool/laex11/learning/viewForum.do?mode=author',
'tool/laex11/learning/viewForum.do?mode=teacher',
'tool/laex11/authoring/init.do',
'tool/laex11/monitoring.do',
'tool/laex11/defineLater.do',
'tool/laex11/exportPortfolio?mode=learner',
'tool/laex11/exportPortfolio?mode=teacher',
'tool/laex11/contribute.do',
'tool/laex11/moderate.do',
'http://wiki.lamsfoundation.org/display/lamsdocs/laex11',
'org.lamsfoundation.lams.tool.example.ApplicationResources',
'./lams-tool-laex11.jar',
'/org/lamsfoundation/lams/tool/example/applicationContext.xml'
NOW(),
NOW()
)
