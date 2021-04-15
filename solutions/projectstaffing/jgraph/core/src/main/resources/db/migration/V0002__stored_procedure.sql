CREATE OR ALTER PROCEDURE [dbo].[find_recommended_employees]
    @size_param 									BIGINT,
    @offset_param 									BIGINT,
    @order_by_availability_and_name 				BIT,
    @available_since  								VARCHAR(16),
    @team_owner_email 								VARCHAR(128),
    @recommended_candidates_emails 			        VARCHAR(MAX),
    @latest_profile_version_str                     VARCHAR(32),
    @latest_airtable_version_str                    VARCHAR(32)
AS

BEGIN

    DECLARE
        @sql 										NVARCHAR(MAX),      -- query to be executed
        @sql_order 									VARCHAR(MAX),       -- 'order by' clause of the query to be executed
        @sql_where   								VARCHAR(MAX),       -- 'where' clause of the query to be executed
        @sql_values 								VARCHAR(MAX),       -- ordered @recommended_candidates_emails and position
        @tmp_pos    								INT,
        @tmp_len 									INT,
        @tmp_ind 									INT,
        @tmp_value 									VARCHAR(MAX),
        @latest_profile_version 					DATETIME2,
        @latest_airtable_version 					DATETIME2;

    SET NOCOUNT ON;

    SET @recommended_candidates_emails = TRIM(@recommended_candidates_emails);
    SET @available_since = TRIM(@available_since);
    SET @team_owner_email = TRIM(@team_owner_email);

    SET @sql = '';
    SET @sql_values = '';
    SET @tmp_pos = 0;
    SET @tmp_len = 0;
    SET @tmp_ind = 1;

    -- if latest versions are not set, use the oldest value possible
    SET @latest_profile_version = CONVERT(datetime2, ISNULL(@latest_profile_version_str, '1753-01-01 00:00:00.000000'));
    SET @latest_airtable_version = CONVERT(datetime2, ISNULL(@latest_airtable_version_str, '1753-01-01 00:00:00.000000'));

    -- select latest data
    SET @sql_where = 'ep.version = ''' + CONVERT(varchar, @latest_profile_version) + '''';

    -- create the 'order by' syntax
    IF @order_by_availability_and_name = 1
        SET @sql_order = '(CASE WHEN CONVERT (date, aep.up_for_redeployment_date, 23) IS NULL THEN 0 ELSE 1 END), CONVERT (date, aep.up_for_redeployment_date, 23) ASC, name ASC ';
    ELSE
        SET @sql_order = '(SELECT NULL) ';	-- 'offset' + 'fetch' pagination syntax without 'order by' clause does not work

    -- define the recommended emails filtering of the where clause
    IF @order_by_availability_and_name = 0
        BEGIN
            SET @recommended_candidates_emails = ISNULL(@recommended_candidates_emails, '''''');
            -- select only recommended emails. 'IN' operator is case insensitive
            SET @sql_where = @sql_where + ' AND ep.mail IN (' + @recommended_candidates_emails + ') ';
            IF @recommended_candidates_emails NOT LIKE '%,'
                BEGIN
                    SET @recommended_candidates_emails = @recommended_candidates_emails + ',';
                END

            -- create the 'values' syntax to be used by order by. it contains the received email and the position of each email
            -- this 'values' are used to select results in the order of emails in '@recommended_candidates_emails'
            -- eg input: 'a@bpcs.com', 'b@bpcs.com', 'x@bpcs.com'
            -- eg output: (1, 'a@bpcs.com'), (2, 'b@bpcs.com'), (3, 'x@bpcs.com')
            WHILE CHARINDEX(',', @recommended_candidates_emails, @tmp_pos+1)>0
                BEGIN
                    SET @tmp_len = CHARINDEX(',', @recommended_candidates_emails, @tmp_pos+1) - @tmp_pos;

                    SET @tmp_value = SUBSTRING(@recommended_candidates_emails, @tmp_pos, @tmp_len)
                    SET @tmp_value = TRIM(REPLACE(@tmp_value, '''', ''))
                    SET @tmp_value = CONCAT('(', CAST(@tmp_ind AS VARCHAR(10)), ',', '''' , @tmp_value , ''')');

                    IF @sql_values <> ''
                        SET @sql_values = CONCAT(@sql_values, ', ', @tmp_value);
                    ELSE
                        SET @sql_values = CONCAT(@sql_values, @tmp_value);

                    SET @tmp_pos = CHARINDEX(',', @recommended_candidates_emails, @tmp_pos+@tmp_len) + 1;
                    SET @tmp_ind = @tmp_ind + 1;
                END
        END


    -- include the @available_since filter in the where clause
    IF @available_since IS NOT NULL AND @available_since <> ''
        BEGIN
            SET @sql_where = @sql_where + ' AND ( aep.up_for_redeployment_date IS NULL OR CONVERT (date, aep.up_for_redeployment_date, 23) <= CONVERT (date, ''' + @available_since + ''' , 23) ) ';
        END

    -- filter out the team members of the current team.
    IF @team_owner_email IS NOT NULL AND @team_owner_email <> ''
        BEGIN
            SET @sql_where = @sql_where + ' AND ep.id NOT IN (SELECT tm.member_id FROM team_members tm WHERE tm.owner_email = ''' + @team_owner_email + ''') ';
        END

    -- if the order of the candidates was recommended, then we dont join with created 'values'
    IF @order_by_availability_and_name = 0 AND @sql_values <> ''
        BEGIN
            SET @sql = 'SELECT unordered_results.*
			FROM (VALUES' + @sql_values + ') ordered_results([order_number], [mail])
			INNER JOIN (';
        END

    SET @sql = @sql + '
			SELECT
				ep.id 							as employee_id,
				ep.display_name 				as name,
				ep.mail							as mail,
				ISNULL(NULLIF(ep.job_title, ''''), aep.[role])
												as employee_role,

				ISNULL(NULLIF(CONCAT_WS('', '', NULLIF(ep.city, ''''), NULLIF(ep.state, ''''), NULLIF(ep.country, '''')), ''''), aep.locate)
												as location,

				ep.about_me						as about_me,
				ep.profile_picture 				as profile_picture,

				aep.current_engagement  		as current_engagement,
				ISNULL(NULLIF(ep.reports_to, ''''), aep.reports_to)
				                                as reports_to,
				CONVERT (date, aep.up_for_redeployment_date, 23) 			-- https://www.mssqltips.com/sqlservertip/1145/date-and-time-conversions-using-sql-server/
												as available_since,
				grouped_employee_skills.skills_json
												as skills_json,
				COUNT(*) OVER() 				as total

			FROM employee_profile ep
			-- left join
			LEFT JOIN (select * from airtable_employee_profile where version = ''' + CONVERT(varchar, @latest_airtable_version) +  ''') aep
			ON ep.mail = aep.mail

			LEFT JOIN
		         (
		             SELECT
		                 es.employee_profile_id AS employee_profile_id,
						 es.employee_profile_version AS employee_profile_version,
		                 JSON_QUERY(''["'' + STRING_AGG(STRING_ESCAPE(es.skill, ''json''), ''","'') + ''"]'') AS skills_json
		             FROM
		                 employee_skills es
					 WHERE
						 es.employee_profile_version = ''' +  CONVERT(varchar, @latest_profile_version) + '''
		             GROUP BY
		                 es.employee_profile_id, es.employee_profile_version

		         ) AS grouped_employee_skills
		         ON grouped_employee_skills.employee_profile_id = ep.id and grouped_employee_skills.employee_profile_version = ep.version


			WHERE ' + @sql_where + '
		';
    IF @order_by_availability_and_name = 1
        SET @sql = @sql + '


			ORDER BY ' + @sql_order + '
			OFFSET ' + convert(varchar, @offset_param) + ' ROWS
			FETCH NEXT ' + convert(varchar, @size_param) + ' ROWS ONLY
		';

    IF @order_by_availability_and_name = 0 AND @sql_values <> ''
        BEGIN
            SET @sql = @sql + ') as unordered_results
		ON ordered_results.[mail] = unordered_results.[mail]
		ORDER BY ordered_results.order_number ASC

		OFFSET ' + convert(varchar, @offset_param) + ' ROWS
		FETCH NEXT ' + convert(varchar, @size_param) + ' ROWS ONLY
		';
        END


    EXEC sp_executesql @sql
END;

GO
