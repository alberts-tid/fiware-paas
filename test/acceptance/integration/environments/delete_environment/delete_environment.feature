# -*- coding: utf-8 -*-
Feature: Delete an environment in a tenant

    As a fi-ware user
    I want to be able to delete an environment in a tenant
    so that I can keep the tenant free of outdated environments 

    @happy_path
    Scenario: Delete environment
        Given the paas manager is up and properly configured
        And an environment has already been created with data:
            | name   | description |
            | nameqa | descqa      |
        When I request the deletion of the environment with name "nameqa"
        Then I receive a "No Content" response
        And the environment with name "nameqa" is no longer available
        
    Scenario Outline: Delete environment with different data
        Given the paas manager is up and properly configured
        And an environment has already been created with data:
            | name   | description   |
            | <name> | <description> |
        When I request the deletion of the environment with name "<name>"
        Then I receive a "No Content" response
        And the environment with name "<name>" is no longer available
        
        Examples:
            | name                    | description              |
            | a                       | descqa                   |
            | nameqa-1                | descqa                   |
            | [STRING_WITH_LENGTH_30] | descqa                   |
            | accountsqa1             | a                        |
            | accountsqa2             | descqa-1                 |
            | accountsqa3             | Symbols: +*=._,;"@#%)/?! |
            | accountsqa4             | Non-ASCII: á.é.í.ñ       |
            | accountsqa5             | [STRING_WITH_LENGTH_150] |

    @skip @CLAUDIA-4237
    Scenario: Delete nonexistent environment
        Given the paas manager is up and properly configured
        And there is no environment with name "nameqa" already created
        When I request the deletion of the environment with name "nameqa"
        Then I receive a "Not Found" response
