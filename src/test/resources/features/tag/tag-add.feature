
Feature:  BDD Scenarios of Tag API

Feature:  BDD Scenarios of Tag API - Add Tag

  Background:
    Given table tag contains data:
      | id | name |
      | 3f94b930-5ac1-4bd6-b8b6-7d45a119c3a2 | Backend |
      | 9ae1c682-dcf1-41d6-8cd9-4dbef7b1d17f  | Frontend |

  Scenario: Add tag should return 200
    And the following tag to add:
      | name            |
      | Base de donnees |
    When call add tag
    Then the tag http status is 201
    And the returned tag has following properties:
      | name            |
      | Base de donnees |

  Scenario Outline: Add tag with name not matching size constraintes should return 400
    And the following tag to add:
      | name   |
      | <name> |
    When call add tag
    Then the tag http status is 400
    And the tag returned error body looks like:
      | system_id                            | system_name | type       | status | message                                          |
      | be7e84a8-9f56-405b-a15a-2646a8012c89 | MS-TODOS    | VALIDATION | 400    | name: size must be between 2 and 50 |
    Examples:
      | name                                                                                          |
      | n                                                                                              |
      | You must change the name of the group to have access and modify the file|

  Scenario: Add tag with null name should return 400
    And the following tag to add:
      | name |
      | null  |
    When call add tag
    Then the tag http status is 400
    And the tag returned error body looks like:
      | system_id                            | system_name | type       | status | message                  |
      | be7e84a8-9f56-405b-a15a-2646a8012c89 | MS-TODOS    | VALIDATION | 400    | name: must not be blank |




