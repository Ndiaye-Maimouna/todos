Feature:  BDD Scenarios of Tag API - Delete All Tag

  Background:
    Given table tag contains data:
      | id | name |
      | 3f94b930-5ac1-4bd6-b8b6-7d45a119c3a2 | Backend |
      | 9ae1c682-dcf1-41d6-8cd9-4dbef7b1d17f  | Frontend |

  Scenario: Delete all tags should return 500
    When call delete all tags
    Then the tag http status is 500


