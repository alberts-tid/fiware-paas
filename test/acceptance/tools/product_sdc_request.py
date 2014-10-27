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

import http
from lettuce import world


class ProductSdcRequest:
    def __init__(self, keystone_url, sdc_url, tenant, user, password, vdc):
        """
        Init class vars and get initial token from keystone
        """
        self.sdc_url = sdc_url
        self.vdc = vdc
        self.keystone_url = keystone_url

        self.user = user
        self.password = password
        self.tenant = tenant

        self.token = self.__get__token()

    def __get__token(self):
        """ Get token from keystone """
        return http.get_token(self.keystone_url + '/tokens', self.tenant, self.user, self.password)

    def __get_product_sdc(self, url):
        """ Get product from SDC """
        headers = {'X-Auth-Token': self.token, 'Tenant-Id': self.vdc,
                   'Content-Type': "application/xml"}
        return http.get(url, headers)

    def __add_product_sdc(self, url, product_sdc_payload):
        """ Add product to SDC catalog """
        headers = {'X-Auth-Token': self.token, 'Tenant-Id': self.vdc,
                   'Content-Type': "application/xml"}
        return http.post(url, headers, product_sdc_payload)

    def __delete_product_sdc(self, url):
        """ Delete product from SDC catalog """
        headers = {'X-Auth-Token': self.token, 'Tenant-Id': self.vdc,
                   'Content-Type': "application/xml"}
        return http.delete(url, headers)

    def get_product(self, product_name):
        """ Get product from SDC catalog """
        url = "%s/%s/%s/%s" % (self.sdc_url, "catalog", "product", product_name)

        world.response = self.__get_product_sdc(url)

    def get_product_release(self, product_name, product_release):
        """ Get product release from SDC catalog """
        url = "%s/%s/%s/%s/%s/%s" % (self.sdc_url, "catalog", "product", product_name, "release", product_release)

        world.response = self.__get_product_sdc(url)

    def add_product(self, product_name, product_description):
        """ Get product release from SDC catalog """
        url = "%s/%s/%s" % (self.sdc_url, "catalog", "product")

        payload = "<product><name>%s</name><description>%s</description></product>" \
                  % (product_name, product_description)
        world.response = self.__add_product_sdc(url, payload)

    def add_product_release(self, product_name, product_release):
        """ Add product release to SDC catalog """
        url = "%s/%s/%s/%s/%s" % (self.sdc_url, "catalog", "product", product_name, "release")

        payload = "<productReleaseDto><version>%s</version></productReleaseDto>" % product_release
        world.response = self.__add_product_sdc(url, payload)

    def delete_product(self, product_name):
        """ Delete product from SDC catalog """
        url = "%s/%s/%s/%s" % (self.sdc_url, "catalog", "product", product_name)

        world.response = self.__delete_product_sdc(url)

    def delete_product_release(self, product_name, product_release):
        """ Delete product release from SDC catalog """
        url = "%s/%s/%s/%s/%s/%s" % (self.sdc_url, "catalog", "product", product_name, "release", product_release)

        world.response = self.__delete_product_sdc(url)

    def create_product_and_release(self, product_name, product_release):
        """ Helper: Create product and product release """
        self.get_product(product_name)
        if world.response.status is not 200:
            self.add_product(product_name, 'QA Tests - PaaS Manager')
            self.add_product_release(product_name, product_release)
        else:
            self.get_product_release(product_name, product_release)
            if world.response.status is not 200:
                self.add_product_release(product_name, product_release)

    def delete_product_and_release(self, product_name, product_release):
        """ Helper: Delete product and product release """
        self.get_product_release(product_name, product_release)
        if world.response.status is 200:
            self.delete_product_release(product_name, product_release)
            self.delete_product(product_name)
        else:
            self.get_product(product_name)
            if world.response.status is 200:
                self.delete_product(product_name)
