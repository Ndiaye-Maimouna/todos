Feature:  BDD Scenarios of Todo API - Delete All Todo

  Background:
    Given table todo contains data:
      | id                                   | title        | description        | completed | date_debut          | date_fin            |
      | 17a281a6-0882-4460-9d95-9c28f5852db1 | Rendre notes | Rendre notes DIC 1 | false     | 2025-08-19T12:00:00 | 2025-08-19T12:15:00 |
      | 18a81a6-0882-4460-9d95-9c28f5852db1  | Presentation | Presentation DIC 1 | false     | 2025-08-12T15:00:00 | 2030-09-02T19:00:00 |

  Scenario: Delete all todos should return 500
    When call delete all todos
    Then the http status is 500


