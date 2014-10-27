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

from tools import tier

__author__ = 'henar'

import http
import json
from xml.etree.ElementTree import tostring

from environment import Environment
from lettuce import world


class EnvironmentRequest:
    def __init__(self, keystone_url, paas_manager_url, tenant, user, password, vdc, image, sdc_url=''):
        self.paasmanager_url = paas_manager_url
        self.sdc_url = sdc_url
        self.vdc = vdc
        self.keystone_url = keystone_url

        self.user = user
        self.password = password
        self.tenant = tenant

        self.token = self.__get__token()

    def __get__token(self):
        return http.get_token(self.keystone_url + '/tokens', self.tenant, self.user, self.password)

    def __add_environment(self, url, environment_payload):
        headers = {'X-Auth-Token': self.token, 'Tenant-Id': self.vdc,
                   'Content-Type': "application/xml"}
        return http.post(url, headers, environment_payload)

    def __update_environment(self, url, environment_payload):
        headers = {'X-Auth-Token': self.token, 'Tenant-Id': self.vdc,
                   'Content-Type': "application/xml"}
        return http.put(url, headers, environment_payload)

    def __delete_environment(self, url):
        headers = {'X-Auth-Token': self.token, 'Tenant-Id': self.vdc,
                   'Accept': "application/json"}
        return http.delete(url, headers)

    def __get_environments(self, url):
        headers = {'X-Auth-Token': self.token, 'Tenant-Id': self.vdc,
                   'Accept': "application/json"}
        return http.get(url, headers)

    def __get_environment(self, url):
        headers = {'X-Auth-Token': self.token, 'Tenant-Id': self.vdc,
                   'Accept': "application/json"}
        return http.get(url, headers)

    def __add_tier(self, url, tier_payload):
        headers = {'X-Auth-Token': self.token, 'Tenant-Id': self.vdc,
                   'Content-Type': "application/xml"}
        return http.post(url, headers, tier_payload)

    def __update_tier(self, url, tier_payload):
        headers = {'X-Auth-Token': self.token, 'Tenant-Id': self.vdc,
                   'Content-Type': "application/xml"}
        return http.put(url, headers, tier_payload)

    def __delete_tier_environment(self, url):
        headers = {'X-Auth-Token': self.token, 'Tenant-Id': self.vdc,
                   'Accept': "application/json"}
        return http.delete(url, headers)

    def __get_tiers_environment(self, url):
        headers = {'X-Auth-Token': self.token, 'Tenant-Id': self.vdc,
                   'Accept': "application/json"}
        return http.get(url, headers)

    def __get_tier_environment(self, url):
        headers = {'X-Auth-Token': self.token, 'Tenant-Id': self.vdc,
                   'Accept': "application/json"}
        return http.get(url, headers)

    def add_abstract_environment(self, environment_name, environment_description, tiers=None):
        url = "%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                            "environment")

        env = Environment(environment_name, environment_description, tiers)

        payload = tostring(env.to_env_xml())
        world.response = self.__add_environment(url, payload)

        """Store it in the world to tear it down later"""
        try:
            world.abstract_environments.append(environment_name)
        except AttributeError:
            world.abstract_environments = [environment_name]

    def add_environment(self, environment_name, environment_description, tiers=None):
        """
        Adds a new environment with the data provided.
        :param environment_name: Name of the environment.
        :param environment_description: Description of the environment.
        :param tiers: List of tiers of the environment.
        """
        url = "%s/%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                                  "vdc", self.vdc, "environment")

        env = Environment(environment_name, environment_description, tiers)

        payload = tostring(env.to_env_xml())
        world.response = self.__add_environment(url, payload)

        """Store it in the world to tear it down later"""
        try:
            world.environments.append(environment_name)
        except AttributeError:
            world.environments = [environment_name]

    def delete_abstract_environment(self, environment_name):
        url = "%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                               "environment", environment_name)

        world.response = self.__delete_environment(url)

        """Remove it from the world too"""
        try:
            world.abstract_environments.remove(environment_name)
        except:
            pass

    def delete_environment(self, environment_name):
        url = "%s/%s/%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                                     "vdc", self.vdc, "environment", environment_name)

        world.response = self.__delete_environment(url)

        """Remove it from the world too"""
        try:
            world.environments.remove(environment_name)
        except:
            pass

    def update_abstract_environment(self, environment_name, new_name, new_description, new_tiers=None):
        url = "%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                               "environment", environment_name)

        env = Environment(new_name, new_description, new_tiers)

        payload = tostring(env.to_env_xml())
        world.response = self.__update_environment(url, payload)

    def update_environment(self, environment_name, new_name, new_description, new_tiers=None):
        """
        Updates the environment with the name provided setting the data provided.
        :param environment_name: Name of the environment.
        :param new_name: New name of the environment.
        :param new_description: New description of the environment.
        :param new_tiers: New list of tiers of the environment.
        """
        url = "%s/%s/%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                                     "vdc", self.vdc, "environment", environment_name)

        env = Environment(new_name, new_description, new_tiers)

        payload = tostring(env.to_env_xml())
        world.response = self.__update_environment(url, payload)

    def get_abstract_environments(self):
        url = "%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                            "environment")

        world.response = self.__get_environments(url)

    def get_environments(self):
        """
        Gets the list of environments.
        """
        url = "%s/%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                                  "vdc", self.vdc, "environment")

        world.response = self.__get_environments(url)

    def get_abstract_environment(self, environment_name):
        url = "%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                               "environment", environment_name)

        world.response = self.__get_environment(url)

    def get_environment(self, environment_name):
        """
        Gets the data of the environment with the name provided.
        :param environment_name: Name of the environment.
        """
        url = "%s/%s/%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                                     "vdc", self.vdc, "environment", environment_name)

        world.response = self.__get_environment(url)

    def add_tier_abstract_environment(self, environment_name, tier):
        url = "%s/%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                                  "environment", environment_name, "tier")

        payload = tostring(tier.to_tier_xml())
        world.response = self.__add_tier(url, payload)

        """Store it in the world to track it later"""
        try:
            world.tiers.append(tier)
        except AttributeError:
            world.tiers = [tier]

    def add_tier_environment(self, environment_name, tier):
        url = "%s/%s/%s/%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                                        "vdc", self.vdc, "environment", environment_name, "tier")

        payload = tostring(tier.to_tier_xml())
        world.response = self.__add_tier(url, payload)

        """Store it in the world to track it later"""
        try:
            world.tiers.append(tier)
        except AttributeError:
            world.tiers = [tier]

    def delete_tier_abstract_environment(self, environment_name, tier_name):
        url = "%s/%s/%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                                     "environment", environment_name, "tier", tier_name)

        world.response = self.__delete_tier_environment(url)

        """Remove it from the world too"""
        try:
            world.tiers.remove(tier_name)
        except:
            pass

    def delete_tier_environment(self, environment_name, tier_name):
        url = "%s/%s/%s/%s/%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                                           "vdc", self.vdc, "environment", environment_name, "tier", tier_name)

        world.response = self.__delete_tier_environment(url)

        """Remove it from the world too"""
        try:
            world.tiers.remove(tier_name)
        except:
            pass

    def update_tier_abstract_environment(self, environment_name, tier_name, tier):
        """
        Updates the tier with the name provided in the abstract environment
        provided setting a new one.
        :param environment_name: Name of the abstract environment.
        :param tier_name: Name of the tier.
        :param tier: New tier.
        """
        url = "%s/%s/%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                                     "environment", environment_name, "tier", tier_name)

        payload = tostring(tier.to_tier_xml())
        world.response = self.__update_tier(url, payload)

    def update_tier_environment(self, environment_name, tier_name, tier):
        """
        Updates the tier with the name provided in the environment provided
        setting a new one.
        :param environment_name: Name of the environment.
        :param tier_name: Name of the tier.
        :param tier: New tier.
        """
        url = "%s/%s/%s/%s/%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                                           "vdc", self.vdc, "environment", environment_name, "tier", tier_name)

        payload = tostring(tier.to_tier_xml())
        world.response = self.__update_tier(url, payload)

    def get_tiers_abstract_environment(self, environment_name):
        """
        Gets the tiers of the abstract environment provided.
        :param environment_name: Name of the abstract environment.
        """
        url = "%s/%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                                  "environment", environment_name, "tier")

        world.response = self.__get_tiers_environment(url)

    def get_tiers_environment(self, environment_name):
        """
        Gets the tiers of the environment provided.
        :param environment_name: Name of the environment.
        """
        url = "%s/%s/%s/%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                                        "vdc", self.vdc, "environment", environment_name, "tier")

        world.response = self.__get_tiers_environment(url)

    def get_tier_abstract_environment(self, environment_name, tier_name):
        """
        Gets the tier with the name provided of the abstract environment provided.
        :param environment_name: Name of the abstract environment.
        :param tier_name: Name of the tier.
        """
        url = "%s/%s/%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                                     "environment", environment_name, "tier", tier_name)

        world.response = self.__get_tier_environment(url)

    def get_tier_environment(self, environment_name, tier_name):
        """
        Gets the tier with the name provided of the environment provided.
        :param environment_name: Name of the environment.
        :param tier_name: Name of the tier.
        """
        url = "%s/%s/%s/%s/%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                                           "vdc", self.vdc, "environment", environment_name, "tier", tier_name)

        world.response = self.__get_tier_environment(url)

    def __add_product_to_tier(self, url, products_information):
        product = self.process_products(products_information)
        payload = tostring(product[0].to_product_xml())

        self.__add_tier(url, payload)

    def add_product_to_tier(self, environment_name, tier_name, products_information):
        url = "%s/%s/%s/%s/%s/%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                                              "vdc", self.vdc, "environment", environment_name, "tier", tier_name,
                                              "productRelease")

        self.__add_product_to_tier(url, products_information)

    def add_abstract_product_to_tier(self, environment_name, tier_name, products_information):
        url = "%s/%s/%s/%s/%s/%s/%s" % (self.paasmanager_url, "catalog/org/FIWARE",
                                        "environment", environment_name, "tier", tier_name,
                                        "productRelease")

        self.__add_product_to_tier(url, products_information)


def delete_created_environments():
    """
    Delete the environment created so far in the tests.
    """
    try:
        while len(world.environments) > 0:
            world.env_requests.delete_environment(world.environments[0])
        del world.environments
    except AttributeError:
        pass


def delete_created_abstract_environments():
    """
    Delete the abstract environment created so far in the tests.
    """
    try:
        while len(world.abstract_environments) > 0:
            world.env_requests.delete_abstract_environment(world.abstract_environments[0])
        del world.abstract_environments
    except AttributeError:
        pass


def process_environments(environments):
    """
    Process the environments provided as a list of dictionaries.
    :param environments: environments to be processed.
    :return: a list of Environment objects.
    """
    processed_environments = []
    if isinstance(environments, list):
        for env in environments:
            processed_environments.append(process_environment(env))
    else:
        # Single environment received
        processed_environments.append(process_environment(environments))
    return processed_environments


def process_environment(environment):
    """
    Process the environment provided as a dictionary.
    :param environment: environment to be processed.
    :return: a Environment object.
    """
    processed_environment = Environment(environment['name'], environment['description'])
    try:
        tiers = tier.process_tiers(environment['tierDtos'])
        processed_environment.add_tiers(tiers)
    except:
        pass
    return processed_environment


def check_add_environment_response(response, expected_status_code):
    """
    Check that the response for an add environment request is the
    expected one.
    :param response: Response to be checked.
    :param expected_status_code: Expected status code of the response.
    """
    assert response.status == expected_status_code,\
    "Wrong status code received: %d. Expected: %d. Body content: %s"\
    % (response.status, expected_status_code, response.read())


def check_update_environment_response(response, expected_status_code):
    """
    Check that the response for an update environment request is the
    expected one.
    :param response: Response to be checked.
    :param expected_status_code: Expected status code of the response.
    """
    assert response.status == expected_status_code,\
    "Wrong status code received: %d. Expected: %d. Body content: %s"\
    % (response.status, expected_status_code, response.read())


def check_delete_environment_response(response, expected_status_code):
    """
    Check that the response for a delete environment request is the
    expected one.
    :param response: Response to be checked.
    :param expected_status_code: Expected status code of the response.
    """
    assert response.status == expected_status_code,\
    "Wrong status code received: %d. Expected: %d. Body content: %s"\
    % (response.status, expected_status_code, response.read())


def check_get_environments_response(response, expected_status_code,
                                    expected_environments_number=None):
    """
    Check that the response for a get environments request is the
    expected one.
    :param response: Response to be checked.
    :param expected_status_code: Expected status code of the response.
    :param expected_environments_number: Expected number of environments.
    """
    assert response.status == expected_status_code,\
    "Wrong status code received: %d. Expected: %d. Body content: %s"\
    % (response.status, expected_status_code, response.read())

    if expected_environments_number is not None:
        data = json.loads(response.read())
        #print data, "\n\n\n\n"
        if expected_environments_number == 0:
            # No content expected when the lists of environments is empty
            assert data is None or len(data) == 0, "Unexpected content received: %s" % data
        else:
            world.response.environments = process_environments(data)

            if expected_environments_number == "+":  # The "+" wildcard is allowed
                assert len(world.response.environments) > 0,\
                "Wrong number of envs received: %d. Expected more than 0."\
                % (len(world.response.environments))
            else:
                assert len(world.response.environments) == expected_environments_number,\
                "Wrong number of envs received: %d. Expected: %d."\
                % (len(world.response.environments), expected_environments_number)


def check_environment_in_list(environments_list, environment_name,
                              environment_description, tiers_number=0):
    """
    Check that a certain environment is in the list of environments provided.
    :param environments_list: List of environments to be checked.
    :param environment_name: Name of the environment to be found.
    :param environment_description: Description of the environment to be found.
    :param tiers_number: Number of tiers of the environment to be found.
    """
    for env in environments_list:
        if env.name == environment_name:  # Expected environment found
            assert env.description == environment_description,\
            "Wrong description received for environment %s: %s. Expected: %s."\
            % (env.name, env.description, environment_description)

            assert len(env.tiers) == tiers_number,\
            "Wrong number of tiers received for environment %s: %d. Expected: %d."\
            % (env.name, len(env.tiers), tiers_number)

            return

    assert False, "No environment found in the list with name %s" % (environment_name)


def check_get_environment_response(response, expected_status_code,
                                   expected_environment_name=None,
                                   expected_environment_description=None,
                                   expected_environment_tiers=None):
    """
    Check that the response for a get environment request is the
    expected one.
    :param response: Response to be checked.
    :param expected_status_code: Expected status code of the response.
    :param expected_environment_name: Expected name of the environment.
    :param expected_environment_description: Expected description of the environment.
    :param expected_environment_tiers: Expected tiers of the environment.
    """
    assert response.status == expected_status_code,\
    "Wrong status code received: %d. Expected: %d. Body content: %s"\
    % (response.status, expected_status_code, response.read())

    if expected_environment_name is not None:
        environment = process_environment(json.loads(response.read()))

        assert environment.name == expected_environment_name,\
        "Wrong name received: %s. Expected: %s."\
        % (environment.name, expected_environment_name)

    if expected_environment_description is not None:
        assert environment.description == expected_environment_description,\
        "Wrong description received: %s. Expected: %s."\
        % (environment.description, expected_environment_description)

    if expected_environment_tiers is not None:
        assert len(environment.tiers) == len(expected_environment_tiers),\
        "Wrong number of tiers received: %d. Expected: %d."\
        % (len(environment.tiers), len(expected_environment_tiers))

        for expected_tier in expected_environment_tiers:
            # Find the tier that matches each of the expected ones and compare
            received_tier = None
            for tier in environment.tiers:
                if tier.name == expected_tier.name:
                    received_tier = tier
                    break

            assert received_tier is not None,\
            "Tier not found in response: %s" % (expected_tier.name)

            assert received_tier == expected_tier,\
            "The data for tier %s does not match the expected one. Received: %s. Expected: %s."\
            % (received_tier.name, tostring(received_tier.to_xml()), tostring(expected_tier.to_xml()))
