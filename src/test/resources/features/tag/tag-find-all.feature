Feature: BDD Scenarios of Tag API - Find All Tags

  Background:
    Given table tag contains data:
      | id                                   | name      |
      | 3f94b930-5ac1-4bd6-b8b6-7d45a119c3a2 | Backend   |
      | 9ae1c682-dcf1-41d6-8cd9-4dbef7b1d17f | Frontend  |

  Scenario: Find all should return correct page
    When call find tag all with page=0, size=10 and sort="sort=name,asc"
    Then the tag http status is 200
    And the tag returned page has following content:
      | name     |
      | Backend  |
      | Frontend |

  Scenario: Find all should return empty page
    When call find tag all with page=1, size=10 and sort="sort=name,asc"
    Then the tag http status is 200
    And the tag returned page has no content

  Scenario: Find all with negative page should return error
    When call find tag all with page=-1, size=10 and sort="sort=name,asc"
    Then the tag http status is 400
    And the tag returned error body looks like:
      | system_id                            | system_name | type       | status | message                               |
      | be7e84a8-9f56-405b-a15a-2646a8012c89 | MS-TODOS    | VALIDATION | 400    | Page index must not be less than zero |

  Scenario: Find all with size less than 1 should return error
    When call find tag all with page=0, size=0 and sort="sort=name,asc"
    Then the tag http status is 400
    And the tag returned error body looks like:
      | system_id                            | system_name | type       | status | message                             |
      | be7e84a8-9f56-405b-a15a-2646a8012c89 | MS-TODOS     | VALIDATION | 400    | Page size must not be less than one |

  Scenario: Find all with size too large should return error
    When call find tag all with page=0, size=100 and sort="sort=name,asc"
    Then the tag http status is 400
    And the tag returned error body looks like:
      | system_id                            | system_name | type       | status | message                         |
      | be7e84a8-9f56-405b-a15a-2646a8012c89 | MS-TODOS     | VALIDATION | 400    | Page size must not be too large |
