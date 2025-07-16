Feature:  BDD Scenarios of Tag API - Delete Tag

  Background:
    Given table tag contains data:
      | id | name |
      | 3f94b930-5ac1-4bd6-b8b6-7d45a119c3a2 | Backend |
      | 9ae1c682-dcf1-41d6-8cd9-4dbef7b1d17f  | Frontend |

  Scenario: Delete an existing tag should return 204
    When call delete tag with id="3f94b930-5ac1-4bd6-b8b6-7d45a119c3a2"
    Then the tag http status is 204

  Scenario: Delete an non existing tag should return 404
    When call delete tag with id="8a1c3b9f0-2e6e-45d4-b7dc-62c91e1a74fd"
    Then the tag http status is 404
    And the tag returned error body looks like:
      | system_id                            | system_name | type       | status | message                                                        |
      | be7e84a8-9f56-405b-a15a-2646a8012c89 | MS-TODOS    | VALIDATION | 404    | Can not find tag with id=8a1c3b9f0-2e6e-45d4-b7dc-62c91e1a74fd |

