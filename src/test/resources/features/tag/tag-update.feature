Feature: BDD Scenarios of Tag API - Update Tag

  Background:
    Given table tag contains data:
      | id                                   | name     |
      | 3f94b930-5ac1-4bd6-b8b6-7d45a119c3a2 | Backend  |
      | 9ae1c682-dcf1-41d6-8cd9-4dbef7b1d17f | Frontend |

  Scenario: Update an existing tag should return 202
    And the following tag to update:
      | name   |
      | Backend |
    When call update tag with id="3f94b930-5ac1-4bd6-b8b6-7d45a119c3a2"
    Then the tag http status is 202
    And the returned tag has following properties:
      | name   |
      | Backend |

  Scenario: Update a non-existing tag should return 404
    And the following tag to update:
      | name  |
      | Backend    |
    When call update tag with id="18a81a6-0882-4460-9d95-9c28f5852db1"
    Then the tag http status is 404
    And the tag returned error body looks like:
      | system_id                            | system_name | type       | status | message                                                        |
      | be7e84a8-9f56-405b-a15a-2646a8012c89 | MS-TODOS     | VALIDATION | 404    | Can not find tag with id=null |

  Scenario Outline: Update tag with name not matching size constraints should return 400
    And the following tag to update:
      | name   |
      | <name> |
    When call update tag with id="3f94b930-5ac1-4bd6-b8b6-7d45a119c3a2"
    Then the tag http status is 400
    And the tag returned error body looks like:
      | system_id                            | system_name | type       | status | message                                         |
      | be7e84a8-9f56-405b-a15a-2646a8012c89 | MS-TODOS    | VALIDATION | 400    | name: size must be between 2 and 50 |
    Examples:
      | name                                                                 |
      | a                                                                    |
      | Review and finalize the quarterly financial report for publication. |

  Scenario: Update tag with null name should return 400
    And the following tag to update:
      | name |
      | null |
    When call update tag with id="3f94b930-5ac1-4bd6-b8b6-7d45a119c3a2"
    Then the tag http status is 400
    And the tag returned error body looks like:
      | system_id                            | system_name | type       | status | message                 |
      | be7e84a8-9f56-405b-a15a-2646a8012c89 | MS-TODOS     | VALIDATION | 400    | name: must not be blank |
