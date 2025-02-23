Feature: Create Recipe

  Scenario: Perform recipe creation with valid data
    Given a recipe with valid data
      | name         | description                                                       | preparationTime | cookingTime | portions | instructions                                                                 |
      | Ensalada César 2 | Una deliciosa ensalada con pollo a la parrilla, lechuga, crutones y aderezo César cremoso. | 15             | 10          | 4        |1. Cocinar el pollo a la parrilla hasta que esté bien dorado y cocido por dentro.;2. Cortar la lechuga en trozos medianos.;3. Hacer crutones con pan tostado o comprados y colocarlos en un bowl grande.;4. Mezclar el pollo, la lechuga y los crutones en un tazón grande.;5. Agregar el aderezo César al gusto y mezclar bien.;6. Servir con un poco de queso parmesano rallado por encima. |
    When request is submitted for recipe creation
    Then verify that the Recipe HTTP response is 200
    And a recipe id is returned

  Scenario: Perform a failed recipe creation
    Given a recipe with invalid data
      | name | description | preparationTime | cookingTime | portions | instructions |
      |      |            | 15              | 10          | 4        |              |
    When request is submitted for recipe creation
    Then verify that the Recipe HTTP response is 500