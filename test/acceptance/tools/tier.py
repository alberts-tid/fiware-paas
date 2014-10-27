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

import json
from tools.constants import PAAS, TIER_NUM_MIN, TIER_NUM_MAX, TIER_NUM_INITIAL,\
    TIER_FLAVOUR, TIER_KEYPAIR, TIER_FLOATINGIP, TIER_IMAGE

__author__ = 'henar'

from xml.etree.ElementTree import Element, SubElement
from productrelease import ProductRelease
from lettuce import world


class Network:
    def __init__(self, network_name):
        self.network_name = network_name

    def __eq__(self, other):
        return self.network_name == other.network_name

    def __gt__(self, other):
        return self.network_name > other.network_name

    def to_xml(self):
        networkDto = Element("networkDto")
        network = SubElement(networkDto, "networkName")
        network.text = self.network_name
        return networkDto


class Tier:
    def __init__(self, tier_name, tier_image,
                 tier_num_min=world.config[PAAS][TIER_NUM_MIN],
                 tier_num_max=world.config[PAAS][TIER_NUM_MAX],
                 tier_num_initial=world.config[PAAS][TIER_NUM_INITIAL],
                 tier_flavour=world.config[PAAS][TIER_FLAVOUR],
                 tier_keypair=world.config[PAAS][TIER_KEYPAIR],
                 tier_floatingip=world.config[PAAS][TIER_FLOATINGIP]):
        self.name = tier_name
        self.tier_num_min = tier_num_min
        self.tier_num_max = tier_num_max
        self.tier_num_initial = tier_num_initial
        self.tier_image = tier_image
        self.tier_flavour = tier_flavour
        self.tier_keypair = tier_keypair
        self.tier_floatingip = tier_floatingip
        self.products = []
        self.networks = []
        self.region = "RegionOne"

    def __eq__(self, other):
        return self.name == other.name\
               and self.tier_num_min == other.tier_num_min\
               and self.tier_num_max == other.tier_num_max\
               and self.tier_num_initial == other.tier_num_initial\
               and self.tier_image == other.tier_image\
               and self.tier_flavour == other.tier_flavour\
               and self.tier_keypair == other.tier_keypair\
               and self.tier_floatingip == other.tier_floatingip\
               and self.region == other.region\
               and sorted(self.products) == sorted(other.products)\
        and sorted(self.networks) == sorted(other.networks)

    def add_product(self, product):
        self.products.append(product)

    def add_network(self, network):
        self.networks.append(network)

    def parse_and_add_products(self, products_information):
        self.products.extend(parse_products(products_information))

    def parse_and_add_networks(self, networks_names):
        self.networks.extend(parse_networks(networks_names))

    def delete_product(self, product):
        self.products.pop(product)

    def to_tier_xml(self):
        return self.to_xml("tierDto")

    def to_xml(self, element_name="tierDtos"):
        tier_dtos = Element(element_name)
        min_num_inst = SubElement(tier_dtos, "minimumNumberInstances")
        min_num_inst.text = self.tier_num_min
        ini_num_inst = SubElement(tier_dtos, "initialNumberInstances")
        ini_num_inst.text = self.tier_num_initial
        max_mum_inst = SubElement(tier_dtos, "maximumNumberInstances")
        max_mum_inst.text = self.tier_num_max
        if self.name:
            name_tier = SubElement(tier_dtos, "name")
            name_tier.text = self.name
        image_tier = SubElement(tier_dtos, "image")
        image_tier.text = self.tier_image
        flavour_tier = SubElement(tier_dtos, "flavour")
        flavour_tier.text = self.tier_flavour
        keypair = SubElement(tier_dtos, "keypair")
        keypair.text = self.tier_keypair
        floating_ip = SubElement(tier_dtos, "floatingip")
        floating_ip.text = self.tier_floatingip

        region = SubElement(tier_dtos, "region")
        region.text = self.region

        #print tostring(tier_dtos)
        if self.products:
            for product in self.products:
                prod = product.to_product_xml_env()

                tier_dtos.append(prod)

        if self.networks:
            for net in self.networks:
                prod = net.to_xml()

                tier_dtos.append(prod)
            #print tostring(tier_dtos)
        return tier_dtos


def parse_products(products_information):
    """
    Parses a list of products in the format prod1=vers1,prod2=vers2,...
    :param products_information: string with the products to parse.
    :return: a list of ProductsRelease objects.
    """
    products = []
    if products_information:
        products_info_list = products_information.split(',')

        for product_info in products_info_list:
            product_name, product_version = product_info.split('=')
            products.append(ProductRelease(product_name, product_version))

    return products


def parse_networks(networks_names):
    """
    Parses a list of networks in the format net1,net2,...
    :param networks_names: string with the networks names to parse.
    :return: a list of Network objects.
    """
    networks = []
    if networks_names:
        networks_names_list = networks_names.split(',')

        for network_name in networks_names_list:
            networks.append(Network(network_name))

    return networks


def process_tiers(tiers):
    """
    Process the tiers provided as a list of dictionaries.
    :param tiers: tiers to be processed.
    :return: a list of Tier objects.
    """
    processed_tiers = []
    if isinstance(tiers, list):
        for tier in tiers:
            processed_tiers.append(process_tier(tier))
    else:
        # Single tier received
        processed_tiers.append(process_tier(tiers))

    return processed_tiers


def process_tier(tier):
    """
    Process the tier provided as dictionary.
    :param tiers: tier to be processed.
    :return: a Tier object.
    """
    processed_tier = Tier(tier['name'], world.config[PAAS][TIER_IMAGE])

    try:
        product_dtos = tier['productReleaseDtos']

        if isinstance(product_dtos, list):
            for product_dto in product_dtos:
                processed_tier.add_product(ProductRelease(product_dto['productName'],
                    product_dto['version']))
        else:
            processed_tier.add_product(ProductRelease(product_dtos['productName'],
                product_dtos['version']))
    except:
        pass

    try:
        network_dtos = tier['networkDto']

        if isinstance(network_dtos, list):
            for network_dto in network_dtos:
                processed_tier.add_network(Network(network_dto['networkName']))
        else:
            processed_tier.add_network(Network(network_dtos['networkName']))
    except:
        pass

    return processed_tier


def check_add_tier_response(response, expected_status_code):
    """
    Checks that the response for an add tier request is the
    expected one.
    :param response: Response to be checked.
    :param expected_status_code: Expected status code of the response.
    """
    assert response.status == expected_status_code,\
    "Wrong status code received: %d. Expected: %d. Body content: %s"\
    % (response.status, expected_status_code, response.read())


def check_update_tier_response(response, expected_status_code):
    """
    Checks that the response for an update tier request is the
    expected one.
    :param response: Response to be checked.
    :param expected_status_code: Expected status code of the response.
    """
    assert response.status == expected_status_code,\
    "Wrong status code received: %d. Expected: %d. Body content: %s"\
    % (response.status, expected_status_code, response.read())


def check_delete_tier_response(response, expected_status_code):
    """
    Checks that the response for a delete tier request is the
    expected one.
    :param response: Response to be checked.
    :param expected_status_code: Expected status code of the response.
    """
    assert response.status == expected_status_code,\
    "Wrong status code received: %d. Expected: %d. Body content: %s"\
    % (response.status, expected_status_code, response.read())


def check_get_tiers_response(response, expected_status_code,
                             expected_tiers_number=None):
    """
    Checks that the response for a get tiers request is the
    expected one.
    :param response: Response to be checked.
    :param expected_status_code: Expected status code of the response.
    :param expected_tiers_number: Expected number of tiers.
    """
    assert response.status == expected_status_code,\
    "Wrong status code received: %d. Expected: %d. Body content: %s"\
    % (response.status, expected_status_code, response.read())

    if expected_tiers_number is not None:
        data = json.loads(response.read())
        #print data, "\n\n\n\n"
        if expected_tiers_number == 0:
            # No content expected when the lists of tiers is empty
            assert (data is None or len(data) == 0), "Unexpected content received: %s" % data
        else:
            world.response.tiers = process_tiers(data)

            assert len(world.response.tiers) == expected_tiers_number,\
            "Wrong number of tiers received: %d. Expected: %d."\
            % (len(world.response.tiers), expected_tiers_number)


def check_tier_in_list(tiers_list, tier_name, products=None, networks=None):
    """
    Checks that a certain tier is in the list of tiers provided.
    :param tiers_list: List of tiers to be checked.
    :param tier_name: Name of the tier to be found.
    :param products: Products of the tier to be found.
    :param networks: Networks of the tier to be found.
    """
    for tier in tiers_list:
        if tier.name == tier_name:  # Expected tier found
            assert sorted(tier.products) == sorted(products),\
            "Wrong products list received for tier %s: %s. Expected: %s."\
            % (tier.name, sorted(tier.products), sorted(products))

            assert sorted(tier.networks) == sorted(networks),\
            "Wrong networks list received for tier %s: %s. Expected: %s."\
            % (tier.name, sorted(tier.networks), sorted(networks))

            return

    assert False, "No tier found in the list with name %s" % (tier_name)


def check_get_tier_response(response, expected_status_code,
                            expected_tier_name=None,
                            expected_products=None,
                            expected_networks=None):
    """
    Checks that the response for a get tier request is the
    expected one.
    :param response: Response to be checked.
    :param expected_status_code: Expected status code of the response.
    :param expected_tier_name: Expected name of the tier.
    :param expected_products: Expected products of the tier.
    :param expected_networks: Expected networks of the tier.
    """
    assert response.status == expected_status_code,\
    "Wrong status code received: %d. Expected: %d. Body content: %s"\
    % (response.status, expected_status_code, response.read())

    if expected_tier_name is not None:
        data = json.loads(response.read())
        tier = process_tier(data)

        assert tier.name == expected_tier_name,\
        "Wrong name received: %s. Expected: %s."\
        % (tier.name, expected_tier_name)

    if expected_products is not None:
        assert len(tier.products) == len(expected_products),\
        "Wrong number of products received: %d. Expected: %d."\
        % (len(tier.products), len(expected_products))

        for expected_product in expected_products:
            # Find the product that matches each of the expected ones
            product_found = False
            for product in tier.products:
                if product.product == expected_product.product\
                and product.version == expected_product.version:
                    product_found = True
                    break

            assert product_found,\
            "Product not found in response: %s-%s" % (expected_product.product, expected_product.version)

    if expected_networks is not None:
        assert len(tier.networks) == len(expected_networks),\
        "Wrong number of networks received: %d. Expected: %d."\
        % (len(tier.networks), len(expected_networks))

        for expected_network in expected_networks:
            # Find the network that matches each of the expected ones
            network_found = False
            for network in tier.networks:
                if network.network_name == expected_network.network_name:
                    network_found = True
                    break

            assert network_found,\
            "Network not found in response: %s" % (expected_network.network_name)
