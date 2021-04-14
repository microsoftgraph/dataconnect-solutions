create or alter procedure [dbo].[find_recommended_employees]
    @size_param 									BIGINT,
    @offset_param 									BIGINT,
    @order_by_availability_and_name 				BIT,
    @available_since  								VARCHAR(16),
    @team_owner_email 								VARCHAR(128),
    @recommended_candidates_emails 			        VARCHAR(MAX),
    @latest_profile_version_str                     VARCHAR(32),
    @latest_airtable_version_str                    VARCHAR(32),
    @latest_inferred_roles_version_str              VARCHAR(32),
    @is_hr_data_mandatory                           BIT,
    @json_filters                                   VARCHAR(MAX)
AS

begin

    declare
        @sql 										NVARCHAR(MAX),      -- query to be executed
        @sql_order 									VARCHAR(MAX),       -- 'order by' clause of the query to be executed
        @sql_where   								VARCHAR(MAX),       -- 'where' clause of the query to be executed
        @sql_values 								VARCHAR(MAX),       -- ordered @recommended_candidates_emails and position
        @sql_hr_data_join_type 						VARCHAR(MAX),       -- type of join query with HR Data (Airtable data). Depends on @is_hr_data_mandatory
        @sql_is_included_in_current_team            VARCHAR(MAX),       -- evaluates to true if the employee on the current row is included in the current user's team
        @tmp_pos    								INT,
        @tmp_len 									INT,
        @tmp_ind 									INT,
        @tmp_value 									VARCHAR(MAX),
        @latest_profile_version 					DATETIME2,
        @latest_airtable_version 					DATETIME2,
        @latest_inferred_roles_version              DATETIME2,
        @data_sources                               NVARCHAR(4000),
        @array_first_element                        NVARCHAR(4000),
        @first_data_source                          NVARCHAR(4000),
        @is_m365_first_data_source                  BIT,
        @location_query                             VARCHAR(MAX),
        @role_query                                 VARCHAR(MAX),
        @reports_to_query                           VARCHAR(MAX),
        @manager_email_query                        VARCHAR(MAX),
        @filters_cursor                             CURSOR,
        @json_filter                                VARCHAR(MAX),
        @table_name                                 VARCHAR(MAX),
        @field_name                                 VARCHAR(MAX),
        @values                                     VARCHAR(MAX);


    select @data_sources = ss.data_sources from gdc_database.dbo.search_settings ss WHERE ss.user_email = @team_owner_email;
    SELECT @array_first_element = JSON_QUERY(@data_sources, '$[0]') ;
    SELECT @first_data_source = JSON_VALUE(@array_first_element, '$.dataSourceType');

    IF  @first_data_source = 'M365'
    	SET @is_m365_first_data_source = 1;
    else
    	SET @is_m365_first_data_source = 0;

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
    set @latest_inferred_roles_version = CONVERT(datetime2, ISNULL(@latest_inferred_roles_version_str, '1753-01-01 00:00:00.000000'));

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

    IF @json_filters IS NOT NULL AND @json_filters <> ''
    	begin
    		set @filters_cursor = CURSOR for select value from OPENJSON ( @json_filters )
    		open @filters_cursor
    		fetch NEXT FROM @filters_cursor INTO @json_filter


    	    while @@FETCH_STATUS = 0
    	    begin
    			select @table_name = JSON_VALUE(@json_filter, '$.dbTable')
    		    set @table_name = case @table_name
    		                      WHEN 'employee_profile' THEN 'ep'
    		                      WHEN 'airtable_employee_profile' THEN 'aep'
    		                      end


    			select @field_name = JSON_VALUE(@json_filter, '$.dbColumn')
    			select @values = JSON_QUERY(@json_filter, '$.filterValues')

    			select @values = REPLACE(@values, '[', '(')
    			select @values = REPLACE(@values, ']', ')')

    			select @values = REPLACE(@values, '"', '''')

    			set @sql_where = @sql_where + ' AND ' + @table_name + '.[' + @field_name + '] IN ' + @values

    	  		fetch NEXT FROM @filters_cursor INTO @json_filter
    	    end

    	    close @filters_cursor ;
    	    DEALLOCATE @filters_cursor;

    	end


    -- include the @available_since filter in the where clause
    IF @available_since IS NOT NULL AND @available_since <> ''
        begin
            set @sql_where = @sql_where + ' AND ( aep.up_for_redeployment_date IS NULL OR CONVERT (date, aep.up_for_redeployment_date, 23) <= CONVERT (date, ''' + @available_since + ''' , 23) ) ';
        end

    -- mark employees which are members of the current user's team.
    IF @team_owner_email IS NOT NULL AND @team_owner_email <> ''
        begin
            set @sql_is_included_in_current_team = 'IIF(ep.id IN (SELECT DISTINCT tm.member_id FROM team_members tm WHERE tm.owner_email = ''' + @team_owner_email + '''), 1, 0) ';
        end
    ELSE
        set @sql_is_included_in_current_team = '0';

    -- if the order of the candidates was recommended, then we dont join with created 'values'
    IF @order_by_availability_and_name = 0 AND @sql_values <> ''
        begin
            set @sql = 'SELECT unordered_results.*
			FROM (VALUES' + @sql_values + ') ordered_results([order_number], [mail])
			INNER JOIN (';
        end

    IF @is_m365_first_data_source = 1
        begin
            set @location_query = 'COALESCE(NULLIF(CONCAT_WS('', '', NULLIF(ep.city, ''''), NULLIF(ep.state, ''''), NULLIF(ep.country, '''')), ''''), aep.locate)'
            set @role_query = 'COALESCE(NULLIF(ep.job_title, ''''), aep.[role])'
            set @reports_to_query = 'COALESCE(NULLIF(ep.reports_to, ''''), aep.reports_to)'
            set @manager_email_query = 'COALESCE(NULLIF(ep.manager_email, ''''), aep.manager_email)'
        end
    ELSE
        begin
            set @location_query = 'COALESCE(NULLIF(aep.locate, ''''), CONCAT_WS('', '', NULLIF(ep.city, ''''), NULLIF(ep.state, ''''), NULLIF(ep.country, '''')))'
            set @role_query = 'COALESCE(NULLIF(aep.[role], ''''), ep.job_title)'
            set @reports_to_query = 'COALESCE(NULLIF(aep.reports_to, ''''), ep.reports_to)'
            set @manager_email_query = 'COALESCE(NULLIF(aep.manager_email, ''''), ep.manager_email)'
        end

    SET @sql = @sql + '
			SELECT
				ep.id 							as employee_id,
				ep.display_name 				as name,
				ep.mail							as mail,
				' + @role_query + ' 			as employee_role,
				JSON_QUERY(CONCAT(''["'',CONCAT_WS(''","'', NULLIF(STRING_ESCAPE(ir.role_proposal_0,''json''), ''''), NULLIF(STRING_ESCAPE(ir.role_proposal_1,''json''), ''''), NULLIF(STRING_ESCAPE(ir.role_proposal_2,''json''), ''''),NULLIF(STRING_ESCAPE(ir.frequent_role_0,''json''), ''''), NULLIF(STRING_ESCAPE(ir.frequent_role_1,''json''), ''''), NULLIF(STRING_ESCAPE(ir.frequent_role_2,''json''), ''''), NULLIF(STRING_ESCAPE(ir.highest_score_role_0,''json''), ''''), NULLIF(STRING_ESCAPE(ir.highest_score_role_1,''json''), ''''), NULLIF(STRING_ESCAPE(ir.highest_score_role_2,''json''), '''')),''"]''))
				                                as inferred_roles_json,
				' + @location_query + ' 		as location,
				ep.about_me						as about_me,
				aep.current_engagement  		as current_engagement,
				ep.department                   as department,
				' + @reports_to_query + '       as reports_to,
				' + @manager_email_query + '    as manager_email,
				CONVERT (date, aep.up_for_redeployment_date, 23) 			-- https://www.mssqltips.com/sqlservertip/1145/date-and-time-conversions-using-sql-server/
												as available_since,
				aep.linkedin_profile  		    as linkedin_profile,
				' + @sql_is_included_in_current_team + '
				                                as included_in_current_team,
				grouped_employee_skills.skills_json
												as skills_json,
				COUNT(*) OVER() 				as total,
				ep.profile_picture 				as profile_picture

			FROM employee_profile ep
			' + @sql_hr_data_join_type + ' JOIN
			   (select * from airtable_employee_profile where version = ''' + CONVERT(varchar, @latest_airtable_version) +  ''') aep
			ON ep.mail = aep.mail

            LEFT JOIN (select * from inferred_roles where version = ''' + CONVERT(varchar, @latest_inferred_roles_version) +  ''') ir
            			ON ep.mail = ir.email

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
