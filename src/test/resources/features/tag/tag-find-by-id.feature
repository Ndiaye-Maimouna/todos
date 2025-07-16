Feature: BDD Scenarios of Tag API - Find Tag By Id

  Background:
    Given table tag contains data:
      | id                                   | name     |
      | 3f94b930-5ac1-4bd6-b8b6-7d45a119c3a2 | Backend  |
      | 9ae1c682-dcf1-41d6-8cd9-4dbef7b1d17f | Frontend |

  Scenario: Find by existing id should return corresponding tag
    When call find tag by id with id="3f94b930-5ac1-4bd6-b8b6-7d45a119c3a2"
    Then the tag http status is 200
    And the returned tag has following properties:
      | name    |
      | Backend |

  Scenario: Find by id with a non-existing id should return 404
    When call find tag by id with id="99999999-1111-2222-3333-000000000000"
    Then the tag http status is 404
    And the tag returned error body looks like:
      | system_id                            | system_name | type       | status | message                                                                     |
      | be7e84a8-9f56-405b-a15a-2646a8012c89 | MS-TODOS     | VALIDATION | 404    | Can not find tag with id=99999999-1111-2222-3333-000000000000              |
