#  Copyright (c) Microsoft Corporation. All rights reserved.
#  Licensed under the MIT license. See LICENSE file in the project root for full license information.


import copy

import argparse
import datetime
import joblib
import logging
import math
import os
import random
import shutil
import sys
import time
import json
from pathlib import Path
from types import SimpleNamespace
from sklearn.cluster import KMeans
from sklearn.feature_extraction.text import TfidfVectorizer
import datetime as dt
from analytics_logger_rest.analytics_logger_rest import LogAnalyticsLogger
from azure.identity import ClientSecretCredential
from azure.keyvault.secrets import SecretClient


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
            day = day.replace(tzinfo=self.tzinfo)
            for hour in range(9, 18):
                if self.is_available(day, hour, self.tzinfo):
                    planned_start_time = day + dt.timedelta(hours=hour)
                    time_slots.setdefault(day, []).append(planned_start_time)
        return time_slots



def retrieve_latest_run(assembled_data_folder: str):
    list_of_ouput_folder_runs = []
    for entry in os.listdir(assembled_data_folder):
        if os.path.isdir(os.path.join(assembled_data_folder, entry)):
            try:
                int_repr = int(entry)
                list_of_ouput_folder_runs.append(int_repr)
            except Exception:
                pass

    list_of_ouput_folder_runs = sorted(list_of_ouput_folder_runs, reverse=True)
    full_path = os.path.join(assembled_data_folder, str(list_of_ouput_folder_runs[0]))
    return full_path

def retrieve_persons_from_group_random_strategy(full_group_for_time_slot, group_size=10):
    if group_size < len(full_group_for_time_slot):
        selected_person_indexes = random.sample(range(0, len(full_group_for_time_slot)), group_size)
    else:
        selected_person_indexes = range(0, len(full_group_for_time_slot))
    return selected_person_indexes


def retrieve_persons_from_group_based_on_clusters(full_group_for_time_slot, group_size=10, ratio=0.6):
    nr_of_people_per_cluster = int(ratio * group_size)
    labels_to_working_mails_in_group = dict()

    for working_mail in full_group_for_time_slot:
        associated_label = work_mails_to_cluster[working_mail]
        labels_to_working_mails_in_group.setdefault(associated_label, []).append(working_mail)

    available_labels_in_group = list(labels_to_working_mails_in_group.keys())
    if len(available_labels_in_group) == 0:
        log.info("found")
        return []
    random_label = random.choice(available_labels_in_group)
    log.info(",****", available_labels_in_group, random_label)
    selected_random_label_group = labels_to_working_mails_in_group[random_label]

    selected_for_cluster = []
    for counter in range(0, nr_of_people_per_cluster):
        if len(selected_random_label_group) == 0:
            break
        selected_mail = random.choice(selected_random_label_group)
        selected_random_label_group.remove(selected_mail)
        selected_for_cluster.append(selected_mail)

    selected_person_indexes = []
    for selected_mail in selected_for_cluster:
        returned_index = full_group_for_time_slot.index(selected_mail)
        selected_person_indexes.append(returned_index)

    remaining_number_of_people_to_be_selected = group_size - len(selected_for_cluster)

    if group_size < len(full_group_for_time_slot):
        selected_person_indexes.extend(
            random.sample(range(0, len(full_group_for_time_slot)), remaining_number_of_people_to_be_selected))
    else:
        selected_person_indexes.extend(range(0, len(full_group_for_time_slot)))
    return list(set(selected_person_indexes))


def retrieve_mails_to_buckets(people_with_meetings):
    mail_to_skill_lists = [(mail, p.skills) for mail, p in people_with_meetings.items()]
    skills = [p[1] for p in mail_to_skill_lists]
    mails = [p[0] for p in mail_to_skill_lists]
    vectorizer = TfidfVectorizer(max_df=0.5)
    X = vectorizer.fit_transform(skills)
    km = KMeans(n_clusters=clusters)
    pos_labels = km.fit_transform(X)

    all_labels = km.labels_

    order_centroids = km.cluster_centers_.argsort()[:, ::-1]
    terms = vectorizer.get_feature_names()
    for i in range(clusters):
        log.info("Cluster %d:" % i, end='')
        for ind in order_centroids[i, :10]:
            log.info(' %s' % terms[ind], end='')
        log.info()

    label_distribution = dict()
    for key in all_labels:
        if key not in label_distribution:
            label_distribution[key] = 0
        label_distribution[key] += 1

    mails_to_cluster = dict()
    for mail, cluster_label in zip(mails, all_labels.tolist()):
        mails_to_cluster[mail] = cluster_label

    return mails_to_cluster


def retrieve_shuffled_mails_to_clusters(people_with_meetings, cluster_size):
    mails_to_cluster = dict()

    mails = list(people_with_meetings.keys())
    if len(mails) <= cluster_size:
        for mail in mails:
            mails_to_cluster[mail] = 1
    else:
        cluster_group_count = math.floor(len(mails) / cluster_size)
        if cluster_group_count == 0:
            cluster_group_count = 1
        cluster_size = math.ceil(len(mails) / cluster_group_count)
        random.shuffle(mails)
        counter = 1
        for i in range(0, len(mails), cluster_size):
            group = mails[i:i + cluster_size]
            for mail in group:
                mails_to_cluster[mail] = counter
            counter += 1
    return mails_to_cluster


def retrieve_timezone_generated_groups(mails_to_cluster,
                                       day_to_freetimeslots,
                                       final_time_slots_to_person,
                                       tzinfo):
    start_time = time.time()
    selected_persons = set()
    timezon_generated_groups = dict()

    work_mails_to_cluster = copy.deepcopy(mails_to_cluster)

    day_based_generated_groups = dict()
    days_list = sorted(list(day_to_freetimeslots.keys()))

    for day in days_list:
        timeslots_in_day = sorted(day_to_freetimeslots[day])

        generated_groups = dict()
        log.info(f"{tzinfo} | {day} - timeslots: {len(timeslots_in_day)}")

        while True:
            if len(timeslots_in_day) == 0:
                break
            timeslot_in_day = random.choice(timeslots_in_day)
            if not (day in final_time_slots_to_person and
                    timeslot_in_day in final_time_slots_to_person[day]):
                continue

            full_group_for_time_slot = final_time_slots_to_person[day][timeslot_in_day]
            if len(full_group_for_time_slot) == 0:
                del final_time_slots_to_person[day][timeslot_in_day]
                timeslots_in_day.remove(timeslot_in_day)
                if len(final_time_slots_to_person[day]) == 0:
                    del final_time_slots_to_person[day]
                continue

            # if len(final_time_slots_to_person[day]) == 0:
            #    del final_time_slots_to_person[day]
            #    continue

            full_group_mails_to_labels = dict()
            for group_mail in full_group_for_time_slot:
                full_group_mails_to_labels[group_mail] = mails_to_cluster[group_mail]

            selected_person_indexes = retrieve_persons_from_group_random_strategy(full_group_for_time_slot,
                                                                                  group_size=group_size)
            # selected_person_indexes = retrieve_persons_from_group_based_on_clusters(full_group_for_time_slot, group_size=group_size)

            current_selection = []
            for idx in selected_person_indexes:
                selected_persons.add(full_group_for_time_slot[idx])
                current_selection.append(full_group_for_time_slot[idx])

            for selected_person_mail in current_selection:
                full_group_for_time_slot.remove(selected_person_mail)

            for t_timeslot in timeslots_in_day:
                if t_timeslot not in final_time_slots_to_person[day]: continue
                for selected_person_mail in current_selection:
                    if selected_person_mail in final_time_slots_to_person[day][t_timeslot]:
                        final_time_slots_to_person[day][t_timeslot].remove(selected_person_mail)

            if len(full_group_for_time_slot) == 0:
                del final_time_slots_to_person[day][timeslot_in_day]
                timeslots_in_day.remove(timeslot_in_day)

            if len(final_time_slots_to_person[day]) == 0:
                del final_time_slots_to_person[day]

            if len(current_selection):
                generated_groups.setdefault(timeslot_in_day, []).append(current_selection)

        day_based_generated_groups[day] = generated_groups

    total_time = time.time() - start_time
    log.info(f'Total time generation: {total_time}s')
    for day, generated_groups in day_based_generated_groups.items():
        log.info(f"{tzinfo} | {day} - nr of generated groups: {len(generated_groups)}")

    timezon_generated_groups[tzinfo] = day_based_generated_groups
    return timezon_generated_groups

args = None
log = None
SERVICE_PRINCIPAL_SECRET = None


if __name__ == '__main__':
    debug = True
    if len(sys.argv) > 2:
        parser = argparse.ArgumentParser(description='Process some integers.')
        parser.add_argument('--application-id', type=str,
                            help='application id')
        parser.add_argument('--directory-id', type=str,
                            help='directory id')
        parser.add_argument('--clusters', type=str, default="10",
                            help='number of clusters')
        parser.add_argument('--max-group-size', type=str, default="10",
                            help='maximum group size')
        parser.add_argument('--input-assembled-data-path', type=str,
                            help='Input path of assembled data')
        parser.add_argument('--kmeans-data-output-path', type=str,
                            help='Output path for kmeans data')
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
        parser.add_argument('--tzinfo-to-time-slots-to-persons-file-name', type=str, default="tzinfo_to_time_slots_to_persons_dict.dict.gz",
                            help="File name for 'time zone info to time slots to persons assembled data' file", required=False)
        parser.add_argument('--tzinfo-to-day-to-freetimeslots-file-name', type=str, default="tzinfo_to_day_to_freetimeslots.dict.gz",
                            help="File name for 'time zone info to  day to free time slots' file", required=False)
        parser.add_argument('--tzinfo-people-with-meetings-file-name', type=str, default="tzinfo_people_with_meetings.dict.gz",
                            help="File name for 'people time zone with meetings info' data file", required=False)
        parser.add_argument('--timezone-generated-groups-file-name', type=str, default="timezone_generated_groups.dict.gz",
                            help='Timezone generated groups output file name', required=False)

        args = parser.parse_args()

        SERVICE_PRINCIPAL_SECRET = dbutils.secrets.get(scope=args.adb_secret_scope_name,
                                                       key=args.adb_sp_client_key_secret_name)

    else:
        from types import SimpleNamespace

        params_path = os.path.expanduser("~/.watercooler/04_generate_timetable_kmeans_params.json")
        params = json.load(open(Path(params_path)))

        default_params = {k.replace('--', '').replace('-', '_'): v['default'] for k, v in params.items()}
        default_params = {k: (v if v not in ('True', 'False') else eval(v)) for k, v in default_params.items()}
        args = SimpleNamespace(**default_params)

        SERVICE_PRINCIPAL_SECRET = json.load(open("config_test.json"))["SERVICE_PRINCIPAL_SECRET"]

    assembled_data_folder = args.input_assembled_data_path
    kmeans_data_output_path = args.kmeans_data_output_path
    tzinfo_to_time_slots_to_persons_file_name = args.tzinfo_to_time_slots_to_persons_file_name
    tzinfo_to_day_to_freetimeslots_file_name = args.tzinfo_to_day_to_freetimeslots_file_name
    tzinfo_people_with_meetings_file_name = args.tzinfo_people_with_meetings_file_name
    timezone_generated_groups_file_name = args.timezone_generated_groups_file_name

    if args.log_analytics_workspace_id is None or not (args.log_analytics_workspace_id.strip()):
        log = LogAnalyticsLogger(name="[profiles_information_extractor]")
    else:
        credential = ClientSecretCredential(tenant_id=args.directory_id,
                                            client_id=args.application_id,
                                            client_secret=SERVICE_PRINCIPAL_SECRET)

        client = SecretClient(vault_url=args.key_vault_url, credential=credential)

        try:
            logAnalyticsApiKey = client.get_secret(name=args.log_analytics_workspace_key_name).value
            log = LogAnalyticsLogger(workspace_id=args.log_analytics_workspace_id,
                                        shared_key=logAnalyticsApiKey,
                                        log_type="GenerateTimetableKmeans",
                                        log_server_time=True,
                                        name="[wc_clustering]")
        except Exception as e:
            log = LogAnalyticsLogger(name="[wc_clustering]")
            log.error("Failed to get Log Analytics api key secret from key vault. " + str(e))

    log.info("=" * 10)
    log.info(f"[kmeans] input based folder: {assembled_data_folder}")
    log.info("=" * 10)

    log.info("Retrieving latest data assembly run folder")
    input_folder_with_data = retrieve_latest_run(assembled_data_folder)
    log.info("Latest assemble data folder: " + str(input_folder_with_data))

    log.info("loading tzinfo_to_time_slots_to_persons_dict.dict.gz ")
    tzinfo_to_time_slots_to_persons_dict = joblib.load(
        os.path.join(input_folder_with_data, tzinfo_to_time_slots_to_persons_file_name))

    log.info("loading tzinfo_to_day_to_freetimeslots.dict.gz ")
    tzinfo_to_day_to_freetimeslots = joblib.load(
        os.path.join(input_folder_with_data, tzinfo_to_day_to_freetimeslots_file_name))

    log.info("loading tzinfo_people_with_meetings.dict.gz ")
    tzinfo_people_with_meetings = joblib.load(
        os.path.join(input_folder_with_data, tzinfo_people_with_meetings_file_name))

    group_size = int(args.max_group_size)
    clusters = int(args.clusters)
    minimum_persons_per_timezone = 5

    timezones_as_list = list(tzinfo_people_with_meetings.keys())
    final_timezon_generated_groups = dict()
    for tzinfo in timezones_as_list:
        log.info(f"Processing people in timezone: {tzinfo}")
        people_with_meetings = tzinfo_people_with_meetings[tzinfo]
        log.info(f"In timezone: {tzinfo} there are: {len(people_with_meetings)} people")

        if len(people_with_meetings) < minimum_persons_per_timezone:
            log.info(f"Only {len(people_with_meetings)} people in the {tzinfo} timezone. Will ignore.")

        if tzinfo not in tzinfo_to_day_to_freetimeslots: continue
        day_to_freetimeslots = tzinfo_to_day_to_freetimeslots[tzinfo]

        if tzinfo not in tzinfo_to_time_slots_to_persons_dict: continue
        final_time_slots_to_person = tzinfo_to_time_slots_to_persons_dict[tzinfo]

        if len(people_with_meetings) > 2 * clusters:
            try:
                mails_to_cluster = retrieve_mails_to_buckets(people_with_meetings)
            except Exception as e:
                log.exception("Error clustering using kmeans and tfidf. Random shuffling will be used: ", exc_info=e)
                mails_to_cluster = retrieve_shuffled_mails_to_clusters(people_with_meetings, clusters)
        else:
            mails_to_cluster = retrieve_shuffled_mails_to_clusters(people_with_meetings, clusters)

        local_generated_group = retrieve_timezone_generated_groups(mails_to_cluster,
                                                                   day_to_freetimeslots,
                                                                   final_time_slots_to_person,
                                                                   tzinfo)
        for key, item in local_generated_group.items():
            final_timezon_generated_groups[key] = item

    output_folder = kmeans_data_output_path
    if os.path.exists(output_folder) is False:
        os.mkdir(output_folder)

    subfolder = str(datetime.datetime.now().strftime("%Y%m%d%H%M"))

    if os.path.exists(os.path.join(output_folder, subfolder)) is False:
        os.mkdir(os.path.join(output_folder, subfolder))

    output_folder = os.path.join(output_folder, subfolder)

    file1 = os.path.join(input_folder_with_data, tzinfo_to_time_slots_to_persons_file_name)

    file2 = os.path.join(input_folder_with_data, tzinfo_to_day_to_freetimeslots_file_name)

    file3 = os.path.join(input_folder_with_data, tzinfo_people_with_meetings_file_name)

    joblib.dump(final_timezon_generated_groups, os.path.join(output_folder, timezone_generated_groups_file_name))

    shutil.copy(file1, output_folder)
    shutil.copy(file2, output_folder)
    shutil.copy(file3, output_folder)

    if debug:
        for timezone_info in final_timezon_generated_groups.keys():
            debug_file = os.path.join(output_folder,
                                      "debug_" + str(timezone_info).lower().replace(" ", "_").replace("/",
                                                                                                      "_") + ".txt")
            with open(debug_file, "w") as f:
                days_to_hour_to_generated_groups = final_timezon_generated_groups[timezone_info]
                for day in days_to_hour_to_generated_groups.keys():
                    f.write("=" * 10 + "\n")
                    f.write("Day" + str(day) + "\n\n")

                    hour_to_group = days_to_hour_to_generated_groups[day]
                    for hour, entry in hour_to_group.items():
                        f.write(str(hour) + ":" + str(entry) + "\n")
                    f.write("\n")
