#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.

import traceback

import datetime as dt
import holidays
import joblib
import json
import os
import pandas as pd
import pytz
import time
from datetime import datetime
from datetime import timezone, timedelta
import sys, traceback
import argparse
from pathlib import Path
from types import SimpleNamespace
import pathlib
import babel.core as cr
from babel.core import get_global

get_global("ETC/GMT")
standard_timezone_names = cr._global_data["windows_zone_mapping"]
pytz_timezone_names = list(standard_timezone_names.values())

from analytics_logger_rest.analytics_logger_rest import LogAnalyticsLogger
from azure.identity import ClientSecretCredential
from azure.keyvault.secrets import SecretClient

def retrieve_latest_run(parent_folder):
    list_of_ouput_folder_runs = []
    for entry in os.listdir(parent_folder):
        if os.path.isdir(os.path.join(parent_folder, entry)):
            try:
                int_repr = int(entry)
                list_of_ouput_folder_runs.append(int_repr)
            except Exception:
                pass

    list_of_ouput_folder_runs = sorted(list_of_ouput_folder_runs, reverse=True)
    full_path = os.path.join(parent_folder, str(list_of_ouput_folder_runs[0]))
    return full_path


def process_line_spark(json_dict_rec):
    all_records = []

    required_fields = ["puser", "originalStartTimeZone", "originalEndTimeZone",
                       "responseStatus", "start", "end",
                       "attendees", "responseStatus"]
    logger.info(f"Processing: {json_dict_rec}")
    try:
        try:
            json_dict_rec = json_dict_rec
        except Exception as ex:
            logger.exception(f"Failed to process {str(json_dict_rec)}. ", ex=ex)
            pass

        json_dict = dict()
        for key in required_fields:
            param_val = json_dict_rec[key]
            if param_val is None:
                param_val = ""
            json_dict[key] = param_val

        json_dict["isOnlineMeeting"] = ""
        if "isOnlineMeeting" in json_dict_rec:
            json_dict["isOnlineMeeting"] = json_dict_rec["isOnlineMeeting"]

        logger.info(f"processed: {json_dict}")
        all_records.append(json_dict)
    except Exception as ex:
        trace = traceback.format_exc()
        logger.exception(f"Exception encountered on json", ex, trace)
        print(ex)
        print(trace)
        return []

    return all_records


class Person(object):
    def __init__(self, name="", mail="", skills=None, tzinfo=None):
        self.name = name
        self.mail = mail
        if skills is None:
            skills = []
        self.skills = skills
        self.events = dict()
        self.tzinfo = tzinfo

    def is_available(self, day_ts, hour, timezone):
        if len(self.events) == 0:
            return True
        if day_ts not in self.events:
            return True

        planned_start_time = day_ts + dt.timedelta(hours=hour)
        planned_end_time = planned_start_time + dt.timedelta(minutes=59)
        events_for_day = self.events[day_ts]
        is_available_for_meeting = True
        for event in events_for_day:
            if planned_start_time >= event[0] and planned_end_time <= event[1]:
                is_available_for_meeting = False
                break
            if planned_start_time < event[0] and (event[0] <= planned_end_time <= event[1]):
                is_available_for_meeting = False
                break
            if planned_start_time < event[0] and planned_end_time >= event[1]:
                is_available_for_meeting = False
                break

        if is_available_for_meeting:
            return True
        return False

    def retrieve_free_time_slots(self, working_days):
        time_slots = dict()
        for day in working_days:
            day = self.tzinfo.localize(day)
            for hour in range(9, 18):
                if self.is_available(day, hour, self.tzinfo):
                    planned_start_time = day + dt.timedelta(hours=hour)
                    time_slots.setdefault(day, []).append(planned_start_time)
        return time_slots


def build_persons_from_csv(profile_folder_full_path: str):
    all_files_to_be_processed = []
    for entry in os.listdir(profile_folder_full_path):
        if entry.endswith("json"):
            nested_file = os.path.join(profile_folder_full_path, entry)
            all_files_to_be_processed.append(nested_file)

    persons_dict = dict()
    for entry in all_files_to_be_processed:
        with open(entry) as f:
            for line in f.readlines():
                entry = json.loads(line.strip())
                fields = ["id",
                          'version', 'mail', 'display_name', 'about_me', 'job_title',
                          'company_name', 'department', 'country', 'office_location', 'city',
                          'state', 'skills', 'responsibilities', 'engagement', 'image']
                rec_dict = dict()
                for field in fields:
                    if field not in entry or entry[field] is None:
                        rec_dict[field] = ""
                    elif str(entry[field]) in ["nan"] or str(entry[field]) in [""]:
                        rec_dict[field] = ""
                    else:
                        rec_dict[field] = entry[field]
                print(rec_dict)
                persons_dict[rec_dict["id"]] = rec_dict
    return persons_dict


def retrieve_timezone_to_mail_to_persons(calendar_events_input_path: str,
                                         persons_dict: dict,
                                         puser_to_timezone_dict: dict):
    all_recs = []
    print("=====retrieve_timezone_to_mail_to_persons")
    full_path_events_folder = calendar_events_input_path
    print("[full_path_events_folder]:", full_path_events_folder)
    all_files = []
    for entry in os.listdir(full_path_events_folder):
        entry_full_path = os.path.join(full_path_events_folder, entry)
        if os.path.isfile(entry_full_path):
            all_files.append(entry_full_path)

    for file in all_files:
        try:
            with open(file) as f:
                for line in f.readlines():
                    rec_input = json.loads(line)
                    rec_out = process_line_spark(rec_input)
                    all_recs.extend(rec_out)
        except Exception as e:
            print("======Error reading file:", file)

    all_persons = dict()
    marked_users = set()
    for rec in all_recs:
        owner_id = rec["puser"]

        start_time = rec["start"]["dateTime"]
        start_timezone_uppercase = rec["start"]["timeZone"]
        start_timezone = start_timezone_uppercase.lower()
        end_time = rec["end"]["dateTime"]
        end_timezone = rec["end"]["timeZone"].lower()

        # pytz_start_event_timezone = pytz.timezone(start_timezone)
        # pytz_end_event_timezone = pytz.timezone(end_timezone)

        pytz_start_event_timezone = pytz.timezone(start_timezone)
        pytz_end_event_timezone = pytz.timezone(end_timezone)

        if owner_id in puser_to_timezone_dict:
            rec["originalStartTimeZone"] = puser_to_timezone_dict[owner_id]

        associated_profile = persons_dict[owner_id]
        mail = associated_profile["mail"]
        skills = associated_profile["skills"]
        about_me = associated_profile["about_me"]
        print("======Processing user:" + mail)

        skills = skills + " " + about_me
        name = associated_profile["display_name"]

        try:
            # retrieve_extended_name_to_code(rec["originalStartTimeZone"])
            if rec["originalStartTimeZone"] in standard_timezone_names:
                formatted_original_start_timezone = standard_timezone_names[rec["originalStartTimeZone"]]
            elif rec["originalStartTimeZone"] in pytz_timezone_names:
                formatted_original_start_timezone = rec["originalStartTimeZone"]
            else:
                raise Exception("Unidentified timezone:" + formatted_original_start_timezone)
        except Exception as e:
            logger.error("Exception caught on parsing timezone for user: " + str(mail), e)
            continue

        person_pytz_original_start_timezone = rec["originalStartTimeZone"].lower()
        # pytz.timezone(formatted_original_start_timezone)
        person_pytz_original_end_timezone = rec["originalEndTimeZone"].lower()

        start_date_time = pd.to_datetime(start_time, format='%Y-%m-%dT%H:%M:%S.%f').to_pydatetime()

        end_date_time = pd.to_datetime(end_time, format='%Y-%m-%dT%H:%M:%S.%f').to_pydatetime()

        day = start_date_time.replace(hour=0, minute=0, second=0,
                                      microsecond=0)

        start_date_time = pytz_start_event_timezone.localize(start_date_time)
        end_date_time = pytz_end_event_timezone.localize(end_date_time)

        person_timezone = pytz.timezone(formatted_original_start_timezone)

        if person_timezone not in all_persons:
            all_persons[person_timezone] = dict()

        if mail not in all_persons[person_timezone]:
            person = Person(name, mail, skills, person_timezone)
            all_persons[person_timezone][mail] = person
        else:
            person = all_persons[person_timezone][mail]

        # transposed_start_date_time = start_date_time.astimezone(person_pytz_original_start_timezone)
        # person_pytz_original_start_timezone.localize(start_date_time)
        # transposed_end_date_time = end_date_time.astimezone(person_pytz_original_start_timezone)
        # person_pytz_original_start_timezone.localize(start_date_time)

        transposed_start_date_time = start_date_time.astimezone(pytz.timezone(formatted_original_start_timezone))

        # person_pytz_original_start_timezone.localize(start_date_time)
        transposed_end_date_time = end_date_time.astimezone(pytz.timezone(formatted_original_start_timezone))
        # person_pytz_original_start_timezone.localize(start_date_time)

        person.events.setdefault(day, []).append((transposed_start_date_time, transposed_end_date_time))
        all_persons[person_timezone][mail] = person

        marked_users.add(owner_id)

        print("Done")

    users_without_timezone_information = []
    for puser, user_record in persons_dict.items():
        if puser not in marked_users:

            associated_profile = persons_dict[puser]
            mail = associated_profile["mail"]
            skills = associated_profile["skills"]
            about_me = associated_profile["about_me"]
            name = associated_profile["display_name"]
            # print("======No calendar event information found for user:" + mail)

            if puser not in puser_to_timezone_dict:
                users_without_timezone_information.append((puser, mail, name))
                print("======No timezone information found for user:" + mail + " id:" + str(puser))
                continue
            print("======Processing user:" + mail)
            associatated_timezone = puser_to_timezone_dict[puser]
            try:
                if associatated_timezone in standard_timezone_names:
                    associatated_timezone = standard_timezone_names[associatated_timezone]
                elif associatated_timezone in pytz_timezone_names:
                    associatated_timezone = associatated_timezone
                else:
                    raise Exception("Unidentified timezone:" + associatated_timezone)
            except Exception as e:
                logger.exception("Exception caught on parsing timezone for user: " + str(mail), e)
                continue
            person_timezone = pytz.timezone(associatated_timezone)

            if person_timezone in all_persons and mail not in all_persons[person_timezone]:
                person = Person(name, mail, skills, person_timezone)
                all_persons[person_timezone][mail] = person
            else:
                if person_timezone not in all_persons:
                    all_persons[person_timezone] = dict()
                    all_persons[person_timezone][mail] = person

    print(all_persons)
    return all_persons


def generate_time_interval():
    start_date = datetime.now().replace(hour=0, minute=0, second=0, microsecond=0)
    end_date = start_date + dt.timedelta(days=14)

    us_holidays = []
    for date in holidays.UnitedStates(years=start_date.year).items():
        us_holidays.append(date)

    all_days = pd.date_range(start_date, end_date)
    # print(all_days)
    df = pd.DataFrame()
    df["Dates"] = all_days
    df["is_holiday"] = [
        1 if str(val).split()[0] in us_holidays else 0 for val in df["Dates"]
    ]
    df["we"] = [0 if val.weekday() < 5 else 1 for val in df["Dates"]]

    # print(df.head(10))
    df = df.loc[(df["is_holiday"] != 1) & (df["we"] != 1)]
    # print(df.head(10))
    return df


time_interval_df = generate_time_interval()
working_days = [val for val in time_interval_df["Dates"]]
working_days = working_days[:30]  # we take the first 2 months


# generate_one_day_schedule()
def retrieve_time_slots_for_all_people(tzinfo_people_with_meetings):
    t_start = time.time()
    timezones_list = tzinfo_people_with_meetings.keys()

    tzinfo_to_time_slots_to_persons_dict = dict()
    tzinfo_to_day_to_free_time_slots = dict()

    for tzinfo in timezones_list:
        final_time_slots_to_person = dict()
        day_to_free_timeslots_dict = dict()
        for mail, person in tzinfo_people_with_meetings[tzinfo].items():
            person_free_time_slots_grouped_by_days = person.retrieve_free_time_slots(working_days)
            for day in working_days:
                day = tzinfo.localize(day)
                if day not in person_free_time_slots_grouped_by_days:
                    continue
                person_free_slots_per_day = person_free_time_slots_grouped_by_days[day]

                if day not in day_to_free_timeslots_dict:
                    day_to_free_timeslots_dict[day] = set()
                if day not in final_time_slots_to_person:
                    final_time_slots_to_person[day] = dict()

                for slot in person_free_slots_per_day:

                    if slot not in final_time_slots_to_person[day]:
                        final_time_slots_to_person[day][slot] = []

                    final_time_slots_to_person[day][slot].append(person.mail)
                    day_to_free_timeslots_dict[day].add(slot)

        tzinfo_to_time_slots_to_persons_dict[tzinfo] = final_time_slots_to_person
        tzinfo_to_day_to_free_time_slots[tzinfo] = day_to_free_timeslots_dict

        total_time = time.time() - t_start
        print(f'Free time slots generation: {total_time}s')
    return tzinfo_to_time_slots_to_persons_dict, tzinfo_to_day_to_free_time_slots, tzinfo_people_with_meetings


def contains_json(full_path_entry):
    try:
        with open(full_path_entry) as f:
            for line in f.readlines():
                json.loads(line)
                return True
    except Exception as e:
        return False


def retrieve_timezone_for_user(mailbox_meta_folder: str):
    possible_entries = []

    for entry in os.listdir(mailbox_meta_folder):
        full_path_entry = os.path.join(mailbox_meta_folder, entry)
        if os.path.isfile(full_path_entry):
            if contains_json(full_path_entry):
                mtime = pathlib.Path(full_path_entry).stat().st_mtime
                creation_time = datetime.fromtimestamp(mtime)
                possible_entries.append((creation_time, full_path_entry))

    if len(possible_entries) == 0:
        print("[error] Error retrieving the latest mailbox timezone file from:", mailbox_meta_folder)
        raise Exception("Cannot retry the latest mailbox timezone file from " + str(mailbox_meta_folder))
    possible_entries = sorted(possible_entries, key=lambda x: x[0], reverse=True)
    mailbox_timezon_information = possible_entries[0][1]
    user_id_to_timezonestr = dict()
    with open(mailbox_timezon_information) as f:
        for line in f.readlines():
            rec = json.loads(line)
            puser_id = rec["puser"]
            if "timeZone" not in rec:
                print("Problem  with the record")
                print(json.dumps(rec, indent=4))
                continue
            timezone_str = rec["timeZone"]
            if puser_id not in user_id_to_timezonestr:
                user_id_to_timezonestr[puser_id] = timezone_str
            else:
                print("WARNING: we have double information for userid: " + puser_id + " Current tz information is " +
                      user_id_to_timezonestr[puser_id] + " and the new one is: " + timezone_str)

    return user_id_to_timezonestr

logger = None
args = None
SERVICE_PRINCIPAL_SECRET = None


if __name__ == '__main__':
    debug_people_meetings = True

    if len(sys.argv) > 2:
        parser = argparse.ArgumentParser(description='Process Arguments')
        parser.add_argument('--application-id', type=str,
                            help='application id')
        parser.add_argument('--directory-id', type=str,
                            help='directory id')
        parser.add_argument('--user-profiles-input-path', type=str,
                            help='Full input path in dbfs for user profiles')
        parser.add_argument('--calendar-events-input-path', type=str,
                            help='Full input path in dbfs for calendar events')
        parser.add_argument('--output-path', type=str,
                            help='Full output path in dbfs for assembled data')
        parser.add_argument('--mailbox-meta-folder-path', type=str,
                            help='Mailbox meta folder path')
        parser.add_argument('--timezone-names-path', type=str,
                            help='Mailbox meta folder path')
        parser.add_argument('--log-analytics-workspace-id', type=str,
                            help='Log Analytics workspace id')
        parser.add_argument('--log-analytics-workspace-key-name', type=str,
                            help='Log Analytics workspace key secret name')
        parser.add_argument('--adb-secret-scope-name', type=str,
                            help='secret scope name')
        parser.add_argument('--adb-sp-client-key-secret-name', type=str,
                            help='Azure Databricks Service Principal client key secret name in Databricks Secrets')
        parser.add_argument('--key-vault-url', type=str,
                            help='Azure Key Vault url')
        # for the following parameters, the "required" option is set to False, we assume that the names of the assembled data files
        # and the generated groups output file, will be the default in most of the cases
        parser.add_argument('--tzinfo-to-time-slots-to-persons-file-name', type=str,
                            default="tzinfo_to_time_slots_to_persons_dict.dict.gz",
                            help="File name for 'time zone info to time slots to persons assembled data' file",
                            required=False)
        parser.add_argument('--tzinfo-to-day-to-freetimeslots-file-name', type=str,
                            default="tzinfo_to_day_to_freetimeslots.dict.gz",
                            help="File name for 'time zone info to  day to free time slots' file", required=False)
        parser.add_argument('--tzinfo-people-with-meetings-file-name', type=str,
                            default="tzinfo_people_with_meetings.dict.gz",
                            help="File name for 'people time zone with meetings info' data file", required=False)
        parser.add_argument('--timezone-names-file-name', type=str, default="windows_timezone_names.txt",
                            help="File name for file that contains timezone names ", required=False)

        args = parser.parse_args()

        SERVICE_PRINCIPAL_SECRET = dbutils.secrets.get(scope=args.adb_secret_scope_name,
                                                       key=args.adb_sp_client_key_secret_name)

    else:

        params_path = os.path.expanduser("~/.watercooler/03_persons_to_events_dill_assembler_params.json")
        params = json.load(open(Path(params_path)))

        default_params = {k.replace('--', '').replace('-', '_'): v['default'] for k, v in params.items()}
        default_params = {k: (v if v not in ('True', 'False') else eval(v)) for k, v in default_params.items()}

        args = SimpleNamespace(**default_params)

        SERVICE_PRINCIPAL_SECRET = json.load(open("config_test.json"))["SERVICE_PRINCIPAL_SECRET"]

    timezone_names = os.path.join(args.timezone_names_path, args.timezone_names_file_name)

    user_profiles_input_path = retrieve_latest_run(args.user_profiles_input_path)
    calendar_events_input_path = retrieve_latest_run(args.calendar_events_input_path)
    out_folder = args.output_path
    tzinfo_to_time_slots_to_persons_file_name = args.tzinfo_to_time_slots_to_persons_file_name
    tzinfo_to_day_to_freetimeslots_file_name = args.tzinfo_to_day_to_freetimeslots_file_name
    tzinfo_people_with_meetings_file_name = args.tzinfo_people_with_meetings_file_name
    mailbox_meta_folder = args.mailbox_meta_folder_path

    if args.log_analytics_workspace_id is None or not (args.log_analytics_workspace_id.strip()):
        logger = LogAnalyticsLogger(name="[profiles_to_events_assembler]")
    else:
        credential = ClientSecretCredential(tenant_id=args.directory_id,
                                            client_id=args.application_id,
                                            client_secret=SERVICE_PRINCIPAL_SECRET)

        client = SecretClient(vault_url=args.key_vault_url, credential=credential)

        try:
            logAnalyticsApiKey = client.get_secret(name=args.log_analytics_workspace_key_name).value
            logger = LogAnalyticsLogger(workspace_id=args.log_analytics_workspace_id,
                                        shared_key=logAnalyticsApiKey,
                                        log_type="ProfileToEventsAssembler",
                                        log_server_time=True,
                                        name="[profiles_to_events_assembler]")
        except Exception as e:
            logger = LogAnalyticsLogger(name="[profiles_to_events_assembler]")
            logger.error("Failed to get Log Analytics api key secret from key vault. " + str(e))


    try:
        puser_to_timezone_dict = retrieve_timezone_for_user(mailbox_meta_folder)
    except Exception as ex:
        print("Exception in user code:")
        print("-" * 60)
        traceback.print_exc(file=sys.stdout)
        print("-" * 60)
        raise ex

    try:
        persons_dict = build_persons_from_csv(user_profiles_input_path)
        people_dict = retrieve_timezone_to_mail_to_persons(calendar_events_input_path, persons_dict,
                                                           puser_to_timezone_dict)

        print("=====[retrieve_time_slots_for_all_people]")
        all_information_dicts_tuple = retrieve_time_slots_for_all_people(people_dict)
        tzinfo_to_time_slots_to_persons_dict = all_information_dicts_tuple[0]
        tzinfo_to_day_to_free_time_slots = all_information_dicts_tuple[1]
        tzinfo_people_with_meetings = all_information_dicts_tuple[2]
    except Exception as e:
        print("Exception in user code:")
        print("-" * 60)
        traceback.print_exc(file=sys.stdout)
        print("-" * 60)
        raise e

    if os.path.exists(out_folder) is False:
        os.mkdir(out_folder)

    subfolder = str(datetime.now().strftime("%Y%m%d%H%M"))

    if os.path.exists(os.path.join(out_folder, subfolder)) is False:
        os.mkdir(os.path.join(out_folder, subfolder))

    print("=====[writing debug information]")
    if debug_people_meetings:
        for timezone_info_name in people_dict.keys():
            debug_file = os.path.join(out_folder, subfolder,
                                      "debug_" + str(timezone_info_name).lower().replace(" ", "_").replace("/",
                                                                                                           "_") + ".txt")
            with open(debug_file, "w") as f:
                people_to_days_events = people_dict[timezone_info_name]
                for person in people_to_days_events.keys():
                    f.write("=" * 10 + "\n")
                    f.write(person + "\n")
                    f.write("\n")
                    person_events_days = people_to_days_events[person].events.keys()
                    person_events_days = sorted(person_events_days)
                    for day in person_events_days:
                        if day < datetime.utcnow() - timedelta(days=3): continue
                        f.write("Day: " + str(day) + "\n")
                        for event_entry in people_to_days_events[person].events[day]:
                            f.write("event: " + str(event_entry[0].hour) + "\n")

    print(f"=====[serializing: {tzinfo_to_time_slots_to_persons_file_name}]")
    joblib.dump(tzinfo_to_time_slots_to_persons_dict,
                os.path.join(out_folder, subfolder, tzinfo_to_time_slots_to_persons_file_name))

    print(f"=====[serializing: {tzinfo_to_day_to_freetimeslots_file_name}]")
    joblib.dump(tzinfo_to_day_to_free_time_slots,
                os.path.join(out_folder, subfolder, tzinfo_to_day_to_freetimeslots_file_name))

    print(f"=====[serializing: {tzinfo_people_with_meetings_file_name}]")
    joblib.dump(tzinfo_people_with_meetings,
                os.path.join(out_folder, subfolder, tzinfo_people_with_meetings_file_name))
