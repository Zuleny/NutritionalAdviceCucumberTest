Feature: Create MealPlan

  Scenario: Perform mealplan creation with valid data
    Given a mealplan with valid data
      | name                        | description                                                 | goal        | dailyCalories | dailyProtein | dailyCarbohydrates | dailyFats | nutritionistId                           | patientId                               |
      | Plan de Alimentación Balanceado | Un plan diseñado para mantener un equilibrio saludable de nutrientes. | Mantener peso | 2000          | 50            | 250                | 70        | 3fa85f64-5717-4562-b3fc-2c963f66afa6      | 3fa85f64-5717-4562-b3fc-2c963f66afa6   |
    When request is submitted for mealplan creation
    Then verify that the MealPlan HTTP response is 200
    And a mealplan id is returned

  Scenario: Perform a failed mealplan creation
    Given a mealplan with invalid data
      | name   | description  | goal    | dailyCalories | dailyProtein | dailyCarbohydrates | dailyFats | nutritionistId                           | patientId                               |
      |        |              |         |  10             | 20             |     30               |  40         | 3fa85f64-5717-4562-b3fc-2c963f66afa6      | 3fa85f64-5717-4562-b3fc-2c963f66afa6   |
    When request is submitted for mealplan creation
    Then verify that the MealPlan HTTP response is 500