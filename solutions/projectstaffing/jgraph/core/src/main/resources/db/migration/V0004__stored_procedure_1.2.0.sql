create or alter procedure [dbo].[find_recommended_employees]
    @size_param 									BIGINT,
    @offset_param 									BIGINT,
    @order_by_availability_and_name 				BIT,
    @available_since  								VARCHAR(16),
    @team_owner_email 								VARCHAR(128),
    @recommended_candidates_emails 			        VARCHAR(MAX),
    @latest_profile_version_str                     VARCHAR(32),
    @latest_airtable_version_str                    VARCHAR(32),
    @is_hr_data_mandatory                           BIT
AS

begin

    declare
        @sql 										NVARCHAR(MAX),      -- query to be executed
        @sql_order 									VARCHAR(MAX),       -- 'order by' clause of the query to be executed
        @sql_where   								VARCHAR(MAX),       -- 'where' clause of the query to be executed
        @sql_values 								VARCHAR(MAX),       -- ordered @recommended_candidates_emails and position
        @sql_hr_data_join_type 						VARCHAR(MAX),       -- type of join query with HR Data (Airtable data). Depends on @is_hr_data_mandatory
        @tmp_pos    								INT,
        @tmp_len 									INT,
        @tmp_ind 									INT,
        @tmp_value 									VARCHAR(MAX),
        @latest_profile_version 					DATETIME2,
        @latest_airtable_version 					DATETIME2;

    set NOCOUNT ON;

    set @recommended_candidates_emails = TRIM(@recommended_candidates_emails);
    set @available_since = TRIM(@available_since);
    set @team_owner_email = TRIM(@team_owner_email);

    set @sql = '';
    set @sql_values = '';
    set @tmp_pos = 0;
    set @tmp_len = 0;
    set @tmp_ind = 1;

    -- if latest versions are not set, use the oldest value possible
    set @latest_profile_version = CONVERT(datetime2, ISNULL(@latest_profile_version_str, '1753-01-01 00:00:00.000000'));
    set @latest_airtable_version = CONVERT(datetime2, ISNULL(@latest_airtable_version_str, '1753-01-01 00:00:00.000000'));

    -- select latest data
    set @sql_where = 'ep.version = ''' + CONVERT(varchar, @latest_profile_version) + '''';

    -- create the 'order by' syntax
    if @order_by_availability_and_name = 1
        set @sql_order = '(CASE WHEN CONVERT (date, aep.up_for_redeployment_date, 23) IS NULL THEN 0 ELSE 1 END), CONVERT (date, aep.up_for_redeployment_date, 23) ASC, name ASC ';
    ELSE
        SET @sql_order = '(SELECT NULL) ';	-- 'offset' + 'fetch' pagination syntax without 'order by' clause does not work

    if @is_hr_data_mandatory = 1
        set @sql_hr_data_join_type = ' INNER ';
    ELSE
        set @sql_hr_data_join_type = ' LEFT ';

    -- define the recommended emails filtering of the where clause
    IF @order_by_availability_and_name = 0
        begin
            set @recommended_candidates_emails = ISNULL(@recommended_candidates_emails, '''''');
            -- select only recommended emails. 'IN' operator is case insensitive
            set @sql_where = @sql_where + ' AND ep.mail IN (' + @recommended_candidates_emails + ') ';
            if @recommended_candidates_emails NOT LIKE '%,'
                begin
                    set @recommended_candidates_emails = @recommended_candidates_emails + ',';
                end

            -- create the 'values' syntax to be used by order by. it contains the received email and the position of each email
            -- this 'values' are used to select results in the order of emails in '@recommended_candidates_emails'
            -- eg input: 'a@bpcs.com', 'b@bpcs.com', 'x@bpcs.com'
            -- eg output: (1, 'a@bpcs.com'), (2, 'b@bpcs.com'), (3, 'x@bpcs.com')
            while CHARINDEX(',', @recommended_candidates_emails, @tmp_pos+1)>0
                begin
                    set @tmp_len = CHARINDEX(',', @recommended_candidates_emails, @tmp_pos+1) - @tmp_pos;

                    set @tmp_value = SUBSTRING(@recommended_candidates_emails, @tmp_pos, @tmp_len)
                    set @tmp_value = TRIM(REPLACE(@tmp_value, '''', ''))
                    set @tmp_value = CONCAT('(', CAST(@tmp_ind AS VARCHAR(10)), ',', '''' , @tmp_value , ''')');

                    if @sql_values <> ''
                        set @sql_values = CONCAT(@sql_values, ', ', @tmp_value);
                    ELSE
                        set @sql_values = CONCAT(@sql_values, @tmp_value);

                    set @tmp_pos = CHARINDEX(',', @recommended_candidates_emails, @tmp_pos+@tmp_len) + 1;
                    set @tmp_ind = @tmp_ind + 1;
                end
        END


    -- include the @available_since filter in the where clause
    IF @available_since IS NOT NULL AND @available_since <> ''
        begin
            set @sql_where = @sql_where + ' AND ( aep.up_for_redeployment_date IS NULL OR CONVERT (date, aep.up_for_redeployment_date, 23) <= CONVERT (date, ''' + @available_since + ''' , 23) ) ';
        end

    -- filter out the team members of the current team.
    IF @team_owner_email IS NOT NULL AND @team_owner_email <> ''
        begin
            set @sql_where = @sql_where + ' AND ep.id NOT IN (SELECT tm.member_id FROM team_members tm WHERE tm.owner_email = ''' + @team_owner_email + ''') ';
        end

    -- if the order of the candidates was recommended, then we dont join with created 'values'
    IF @order_by_availability_and_name = 0 AND @sql_values <> ''
        begin
            set @sql = 'SELECT unordered_results.*
			FROM (VALUES' + @sql_values + ') ordered_results([order_number], [mail])
			INNER JOIN (';
        end

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
				ISNULL(NULLIF(ep.manager_email, ''''), aep.manager_email)
				                                as manager_email,
				CONVERT (date, aep.up_for_redeployment_date, 23) 			-- https://www.mssqltips.com/sqlservertip/1145/date-and-time-conversions-using-sql-server/
												as available_since,
				aep.linkedin_profile  		    as linkedin_profile,
				grouped_employee_skills.skills_json
												as skills_json,
				COUNT(*) OVER() 				as total

			FROM employee_profile ep
			' + @sql_hr_data_join_type + ' JOIN
			   (select * from airtable_employee_profile where version = ''' + CONVERT(varchar, @latest_airtable_version) +  ''') aep
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
        begin
            set @sql = @sql + ') as unordered_results
		ON ordered_results.[mail] = unordered_results.[mail]
		ORDER BY ordered_results.order_number ASC

		OFFSET ' + convert(varchar, @offset_param) + ' ROWS
		FETCH NEXT ' + convert(varchar, @size_param) + ' ROWS ONLY
		';
        end


    EXEC sp_executesql @sql
END;

GO
