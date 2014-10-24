# -*- coding: utf-8 -*-
# Copyright 2014 Telefonica Investigación y Desarrollo, S.A.U
#
# This file is part of FI-WARE project.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
#
# You may obtain a copy of the License at:
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#
# See the License for the specific language governing permissions and
# limitations under the License.
#
# For those usages not covered by the Apache version 2.0 License please
# contact with opensource@tid.es

__author__ = 'henar'

import json
from xml.etree.ElementTree import tostring

from tools import http, tier
from tools.environment_instance import EnvironmentInstance
from lettuce import world


class EnvironmentInstanceRequest:
    def __init__(self, keystone_url, paas_manager_url,
                 tenant, user, password, vdc, sdc_url=''):
        self.paasmanager_url = paas_manager_url
        self.sdc_url = sdc_url
        self.vdc = vdc
        self.keystone_url = keystone_url

        self.user = user
        self.password = password
        self.tenant = tenant

        self.token = self.__get__token()

    def __get__token(self):
        return http.get_token(self.keystone_url + '/tokens',
            self.tenant, self.user, self.password)

    def __process_env_inst(self, data):
        envIns = EnvironmentInstance(data['blueprintName'], data['description'],
            None, data['status'])
        return envIns

    def add_instance(self, environment_instance):
        url = "%s/%s/%s/%s/%s" % (self.paasmanager_url, "envInst/org/FIWARE",
                                  "vdc", self.vdc, "environmentInstance")
        headers = {'X-Auth-Token': self.token, 'Tenant-Id': self.vdc,
                   'Content-Type': "application/xml", 'Accept': "application/json"}
        payload = tostring(environment_instance.to_xml())
        world.response = http.post(url, headers, payload)

        """Store it in the world to tear it down later"""
        try:
            world.instances.append(environment_instance.blueprint_name)
        except AttributeError:
            world.instances = [environment_instance.blueprint_name]

        if world.response.status == 200:
            """Wait for the associated task to finish and store its data"""
            world.task_data = http.wait_for_task(json.loads(world.response.read()), headers)

    def delete_instance(self, instance_name):
        url = "%s/%s/%s/%s/%s/%s" % (self.paasmanager_url, "envInst/org/FIWARE",
                                     "vdc", self.vdc, "environmentInstance", instance_name)
        headers = {'X-Auth-Token': self.token, 'Tenant-Id': self.vdc,
                   'Content-Type': "application/xml", 'Accept': "application/json"}
        world.response = http.delete(url, headers)

        """Remove it from the world too"""
        try:
            world.instances.remove(instance_name)
        except:
            pass

        if world.response.status == 200:
            """Wait for the associated task to finish and store its data"""
            world.task_data = http.wait_for_task(json.loads(world.response.read()), headers)

    def get_instances(self):
        url = "%s/%s/%s/%s/%s" % (self.paasmanager_url, "envInst/org/FIWARE",
                                  "vdc", self.vdc, "environmentInstance")
        headers = {'X-Auth-Token': self.token, 'Tenant-Id': self.vdc,
                   'Content-Type': "application/xml", 'Accept': "application/json"}
        world.response = http.get(url, headers)

    def get_instance(self, instance_name):
        url = "%s/%s/%s/%s/%s/%s" % (self.paasmanager_url, "envInst/org/FIWARE",
                                     "vdc", self.vdc, "environmentInstance", instance_name)
        headers = {'X-Auth-Token': self.token, 'Tenant-Id': self.vdc,
                   'Content-Type': "application/xml", 'Accept': "application/json"}
        world.response = http.get(url, headers)


def delete_created_instances():
    """
    Delete the instances created so far in the tests.
    """
    try:
        while len(world.instances) > 0:
            world.inst_requests.delete_instance(world.instances[0])
        del world.instances
    except AttributeError:
        pass


def process_instances(instances):
    """
    Process the instances provided as a list of dictionaries.
    :param instances: instances to be processed.
    :return: a list of EnvironmentInstance objects.
    """
    processed_instances = []
    if isinstance(instances, list):
        for env in instances:
            processed_instances.append(process_instance(env))
    else:
        # Single instance received
        processed_instances.append(process_instance(instances))
    return processed_instances


def process_instance(instance):
    """
    Process the instance provided as a dictionary.
    :param instance: instance to be processed.
    :return: a EnvironmentInstance object.
    """
    processed_instance = EnvironmentInstance(instance['blueprintName'],
        instance['description'],
        status=instance['status'])
    try:
        tiers = tier.process_tiers(instance['tierDto'])
        processed_instance.add_tiers(tiers)
    except:
        pass
    return processed_instance


def check_add_instance_response(response, expected_status_code):
    """
    Check that the response for an add instance request is the
    expected one.
    :param response: Response to be checked.
    :param expected_status_code: Expected status code of the response.
    """
    assert response.status == expected_status_code,\
    "Wrong status code received: %d. Expected: %d. Body content: %s"\
    % (response.status, expected_status_code, response.read())


def check_delete_instance_response(response, expected_status_code):
    """
    Check that the response for a delete instance request is the
    expected one.
    :param response: Response to be checked.
    :param expected_status_code: Expected status code of the response.
    """
    assert response.status == expected_status_code,\
    "Wrong status code received: %d. Expected: %d. Body content: %s"\
    % (response.status, expected_status_code, response.read())


def check_get_instances_response(response, expected_status_code,
                                 expected_instances_number=None):
    """
    Check that the response for a get instances request is the
    expected one.
    :param response: Response to be checked.
    :param expected_status_code: Expected status code of the response.
    :param expected_instances_number: Expected number of instances.
    """
    assert response.status == expected_status_code,\
    "Wrong status code received: %d. Expected: %d. Body content: %s"\
    % (response.status, expected_status_code, response.read())

    if expected_instances_number is not None:
        data = json.loads(response.read())
        #print data, "\n\n\n\n"
        if expected_instances_number == 0:
            # No content expected when the lists of instances is empty
            assert data == None, "Unexpected content received: %s" % data
        else:
            instances = data["environmentInstancePDto"]
            world.response.instances = process_instances(instances)

            assert len(world.response.instances) == expected_instances_number,\
            "Wrong number of instances received: %d. Expected: %d."\
            % (len(world.response.instances), expected_instances_number)


def check_instance_in_list(instances_list, instance_name, instance_description, tiers_number=0):
    """
    Check that a certain instance is in the list of instances provided.
    :param instances_list: List of instances to be checked.
    :param instance_name: Name of the instance to be found.
    :param instance_description: Description of the instance to be found.
    :param env_name: Name of the template environment of the instance to be found.
    :param tiers_number: Number of tiers of the environment to be found.
    """
    for inst in instances_list:
        if inst.blueprint_name == instance_name:  # Expected instance found
            assert inst.blueprint_description == instance_description,\
            "Wrong description received for instance %s: %s. Expected: %s."\
            % (inst.blueprint_name, inst.blueprint_description, instance_description)

            assert len(inst.get_tiers()) == tiers_number,\
            "Wrong number of tiers received for instance %s: %d. Expected: %d."\
            % (inst.blueprint_name, len(inst.get_tiers()), tiers_number)

            return

    assert False, "No instance found in the list with name %s" % (instance_name)


def check_get_instance_response(response, expected_status_code,
                                expected_instance_name=None,
                                expected_instance_description=None,
                                expected_tiers=None):
    """
    Check that the response for a get instance request is the
    expected one.
    :param response: Response to be checked.
    :param expected_status_code: Expected status code of the response.
    :param expected_instance_name: Expected name of the instance.
    :param expected_instance_description: Expected description of the instance.
    :param expected_tiers: Expected tiers of the instance.
    """
    assert response.status == expected_status_code,\
    "Wrong status code received: %d. Expected: %d. Body content: %s"\
    % (response.status, expected_status_code, response.read())

    if expected_instance_name is not None:
        data = json.loads(response.read())
        #print data, "\n\n\n\n"
        instance = process_instance(data)

        assert instance.blueprint_name == expected_instance_name,\
        "Wrong name received: %s. Expected: %s."\
        % (instance.blueprint_name, expected_instance_name)

    if expected_instance_description is not None:
        assert instance.blueprint_description == expected_instance_description,\
        "Wrong description received: %s. Expected: %s."\
        % (instance.blueprint_description, expected_instance_description)

    if expected_tiers is not None:
        assert len(instance.get_tiers()) == len(expected_tiers),\
        "Wrong number of tiers received: %d. Expected: %d."\
        % (len(instance.get_tiers()), len(expected_tiers))

        for expected_tier in expected_tiers:
            # Find the tier that matches each of the expected ones and compare
            received_tier = None
            for tier in instance.get_tiers():
                if tier.name == expected_tier.name:
                    received_tier = tier
                    break

            assert received_tier is not None,\
            "Tier not found in response: %s" % (expected_tier.name)

            assert received_tier == expected_tier,\
            "The data for tier %s does not match the expected one. Received: %s. Expected: %s."\
            % (received_tier.name, tostring(received_tier.to_xml()), tostring(expected_tier.to_xml()))


def check_task_status(task_data, expected_status):
    """
    Check that the status of a task is the expected one, provided its data.
    :param task_data: Dictionary with the task data.
    :param expected_status: Expected status of the task.
    """
    assert task_data["status"] == expected_status,\
    "Wrong status received: %s. Expected: %s. Task data: %s"\
    % (task_data["status"], expected_status, task_data)
