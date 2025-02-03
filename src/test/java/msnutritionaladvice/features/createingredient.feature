Feature: Create Ingredient

  Scenario: Perform ingredient creation with valid data
    Given a ingredient with valid data
      | name           | variety      | benefits         | dishCategory |
      | Aguacate       | hass         | Fuente de grasas saludables, ayuda a reducir el colesterol y mejora la salud del corazón | guacamole    |
    When request is submitted for ingredient creation
    Then verify that the Ingredient HTTP response is 200
    And a ingredient id is returned

  Scenario: Perform a failed ingredient creation
    Given a ingredient with invalid data
      | name         | variety      | benefits         | dishCategory |
      |              | hass         |                  |  guacamole   |
    When request is submitted for ingredient creation
    Then verify that the Ingredient HTTP response is 500