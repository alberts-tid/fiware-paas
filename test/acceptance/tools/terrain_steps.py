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

__author__ = 'jfernandez'

from lettuce import world
from tools.product_sdc_request import ProductSdcRequest
from tools.constants import PAAS, KEYSTONE_URL, TENANT, USER, PASSWORD, VDC, SDC_URL


def init_products_in_sdc():
    """ Create new products and releases in SDC for PaaS testing """
    world.product_sdc_request = ProductSdcRequest(world.config[PAAS][KEYSTONE_URL],
                                                  world.config[PAAS][SDC_URL],
                                                  world.config[PAAS][TENANT],
                                                  world.config[PAAS][USER],
                                                  world.config[PAAS][PASSWORD],
                                                  world.config[PAAS][VDC])

    world.product_sdc_request.create_product_and_release("git", "1.7")
    world.product_sdc_request.create_product_and_release("mediawiki", "1.17.0")


def remove_testing_products_in_sdc():
    """ Remove created products in SDC """
    world.product_sdc_request.delete_product_and_release("git", "1.7")
    world.product_sdc_request.delete_product_and_release("mediawiki", "1.17.0")
