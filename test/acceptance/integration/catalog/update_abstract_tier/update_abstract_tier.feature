# -*- coding: utf-8 -*-
Feature: Update a tier of an abstract environment

    As a fi-ware user
    I want to be able to update a tier of an abstract environment
    so that I do not need to delete it and create it again when some change is needed

    @happy_path
    Scenario: Update tier of an abstract environment leaving the same data
        Given the paas manager is up and properly configured
        And an abstract environment has already been created with data:
            | name   | description |
            | nameqa | descqa      |
        And a tier has already been added to the abstract environment "nameqa" with data:
            | name       |
            | tiernameqa |
        When I request the update of the tier "tiernameqa" of the abstract environment "nameqa" with data:
            | name       |
            | tiernameqa |
        Then I receive a "No Content" response
        And the data of the tier "tiernameqa" of the abstract environment "nameqa" becomes:
            | name       |
            | tiernameqa |
        
    Scenario Outline: Update tier of an abstract environment adding new products
        Given the paas manager is up and properly configured
        And an abstract environment has already been created with data:
            | name   | description |
            | <name> | descqa      |
        And a tier has already been added to the abstract environment "<name>" with data:
            | name       |
            | <tiername> |
        When I request the update of the tier "<tiername>" of the abstract environment "<name>" with data:
            | name       | products   |
            | <tiername> | <products> |
        Then I receive a "No Content" response
        And the data of the tier "<tiername>" of the abstract environment "<name>" becomes:
            | name       | products   |
            | <tiername> | <products> |
        
        Examples:
            | name    | tiername    | products                 |
            | nameqa1 | tiernameqa1 | git=1.7                  |
            | nameqa2 | tiernameqa2 | git=1.7,mediawiki=1.17.0 |
        
    Scenario Outline: Update tier of an abstract environment removing its products
        Given the paas manager is up and properly configured
        And an abstract environment has already been created with data:
            | name   | description |
            | <name> | descqa      |
        And a tier has already been added to the abstract environment "<name>" with data:
            | name       | products   |
            | <tiername> | <products> |
        When I request the update of the tier "<tiername>" of the abstract environment "<name>" with data:
            | name       |
            | <tiername> |
        Then I receive a "No Content" response
        And the data of the tier "<tiername>" of the abstract environment "<name>" becomes:
            | name       |
            | <tiername> |
        
        Examples:
            | name    | tiername    | products                 |
            | nameqa1 | tiernameqa1 | git=1.7                  |
            | nameqa2 | tiernameqa2 | git=1.7,mediawiki=1.17.0 |

    Scenario Outline: Update tier of an abstract environment adding new networks
        Given the paas manager is up and properly configured
        And an abstract environment has already been created with data:
            | name   | description |
            | <name> | descqa      |
        And a tier has already been added to the abstract environment "<name>" with data:
            | name       |
            | <tiername> |
        When I request the update of the tier "<tiername>" of the abstract environment "<name>" with data:
            | name       | networks   |
            | <tiername> | <networks> |
        Then I receive a "No Content" response
        And the data of the tier "<tiername>" of the abstract environment "<name>" becomes:
            | name       | networks   |
            | <tiername> | <networks> |
        
        Examples:
            | name    | tiername    | networks      |
            | nameqa1 | tiernameqa1 | netqa1        |
            | nameqa2 | tiernameqa2 | netqa1,netqa2 |

    Scenario Outline: Update tier of an abstract environment removing its networks
        Given the paas manager is up and properly configured
        And an abstract environment has already been created with data:
            | name   | description |
            | <name> | descqa      |
        And a tier has already been added to the abstract environment "<name>" with data:
            | name       | networks   |
            | <tiername> | <networks> |
        When I request the update of the tier "<tiername>" of the abstract environment "<name>" with data:
            | name       |
            | <tiername> |
        Then I receive a "No Content" response
        And the data of the tier "<tiername>" of the abstract environment "<name>" becomes:
            | name       |
            | <tiername> |
        
        Examples:
            | name    | tiername    | networks      |
            | nameqa1 | tiernameqa1 | netqa1        |
            | nameqa2 | tiernameqa2 | netqa1,netqa2 |

    Scenario Outline: Update tier of an abstract environment adding new products and networks
        Given the paas manager is up and properly configured
        And an abstract environment has already been created with data:
            | name   | description |
            | <name> | descqa      |
        And a tier has already been added to the abstract environment "<name>" with data:
            | name       |
            | <tiername> |
        When I request the update of the tier "<tiername>" of the abstract environment "<name>" with data:
            | name       | products   | networks   |
            | <tiername> | <products> | <networks> |
        Then I receive a "No Content" response
        And the data of the tier "<tiername>" of the abstract environment "<name>" becomes:
            | name       | products   | networks   |
            | <tiername> | <products> | <networks> |
        
        Examples:
            | name    | tiername    | products                 | networks      |
            | nameqa1 | tiernameqa1 | git=1.7                  | netqa1        |
            | nameqa2 | tiernameqa2 | git=1.7,mediawiki=1.17.0 | netqa1,netqa2 |
        

    Scenario Outline: Update tier of an abstract environment removing its products and networks
        Given the paas manager is up and properly configured
        And an abstract environment has already been created with data:
            | name   | description |
            | <name> | descqa      |
        And a tier has already been added to the abstract environment "<name>" with data:
            | name       | products   | networks   |
            | <tiername> | <products> | <networks> |
        When I request the update of the tier "<tiername>" of the abstract environment "<name>" with data:
            | name       |
            | <tiername> |
        Then I receive a "No Content" response
        And the data of the tier "<tiername>" of the abstract environment "<name>" becomes:
            | name       |
            | <tiername> |
        
        Examples:
            | name    | tiername    | products                 | networks      |
            | nameqa1 | tiernameqa1 | git=1.7                  | netqa1        |
            | nameqa2 | tiernameqa2 | git=1.7,mediawiki=1.17.0 | netqa1,netqa2 |
        
    Scenario: Update tier of an abstract environment changing its name
        Given the paas manager is up and properly configured
        And an abstract environment has already been created with data:
            | name   | description |
            | nameqa | descqa      |
        And a tier has already been added to the abstract environment "nameqa" with data:
            | name       |
            | tiernameqa |
        When I request the update of the tier "tiernameqa" of the abstract environment "nameqa" with data:
            | name        | networks |
            | newtiername | netqa1   |
        Then I receive a "Bad Request" response
        
    Scenario: Update tier of an abstract environment in the wrong path
        Given the paas manager is up and properly configured
        And an abstract environment has already been created with data:
            | name   | description |
            | nameqa | descqa      |
        And a tier has already been added to the abstract environment "nameqa" with data:
            | name       |
            | tiernameqa |
        When I request the update of the tier "wrong_tiername" of the abstract environment "nameqa" with data:
            | name       | networks |
            | tiernameqa | netqa1   |
        Then I receive a "Not Found" response
        
    Scenario: Update non existing tier of an abstract environment
        Given the paas manager is up and properly configured
        And an abstract environment has already been created with data:
            | name   | description |
            | nameqa | descqa      |
        When I request the update of the tier "tiernameqa" of the abstract environment "nameqa" with data:
            | name       | networks |
            | tiernameqa | netqa1   |
        Then I receive a "Not Found" response
