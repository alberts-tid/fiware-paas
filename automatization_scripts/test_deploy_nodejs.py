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

'''
Created on 16/04/2013

@author: henar
'''

from tools.enviornmentrequest import EnvironmentRequest
from tools.environment_instance import EnvironmentInstance
from tools.enviornment_instance_request import EnvironmentInstanceRequest
from xml.etree.ElementTree import tostring


config = {}
execfile("sdc.conf", config)

g = EnvironmentRequest(config['keystone_url'], config['paasmanager_url'], config['tenant'], config['user'],
    config['password'],
    config['vdc'], config['image'], config['sdc_url'])

instance_request = EnvironmentInstanceRequest(config['keystone_url'], config['paasmanager_url'], config['tenant'],
    config['user'], config['password'],
    config['vdc'], config['sdc_url'])

environment_name = 'dddsdd'
blueprintname = 'dddsdd'

print('Create a blueprint Template No tiers for nodemysql: ')
g.add_environment(environment_name, 'description')
print("  OK")

print('Create createTierNoProduct: node')
tier_name = 'nodejsjust66'
g.add_tier_environment(environment_name, tier_name, "tomcat=6")
print("  OK")

print('Get Information about the Blueprint: ' + environment_name )
env = g.get_environment(environment_name)
env.to_xml()
print("  OK")

print('Deploy an environment Instance' + blueprintname )
blueprint_instance = EnvironmentInstance(blueprintname, 'description', env, 'INIT')
print tostring(blueprint_instance.to_xml())
instance_request.add_blueprint_instance(blueprint_instance)
print ('OK')

print('Delete an environment Instance' + blueprintname )
#instance_request.delete_blueprint_instance(blueprintname)

print('borrado del blueprint Template No tiers for contextbroker: ')
#g.delete_environment(environment_name)

print("  OK")


