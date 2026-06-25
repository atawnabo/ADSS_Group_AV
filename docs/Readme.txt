Group members:

-	Bothaina Alatawna : 326014875
-	Zain Yaseen : 213910136
-	Hadi Aben Hmad - 326096476
-	Renad Abu Shareb - 326122272

InventoryModel:

How to run:
  java -jar adss2025_v02.jar
  On startup choose: 1) load from database., 2) start empty.

Modeling tools:
  - draw.io (diagrams.net) — used for all UML diagrams
    (use case, class and sequence )

Note on diagrams: In addition to the merged class diagram (which shows the full integrated model in one view), we also include each diagram separately, on its own page. The combined diagram is large and dense, so the individual diagrams are provided for better readability and easier review of each part.

Built with:
  - Java 17
  - Maven

Libraries:
  - sqlite-jdbc 3.x  (SQLite database engine)
  - JUnit 5          (unit & integration tests)

Database:
  - Data is stored locally in inventory.db (SQLite).

4 - Design Changes and Architectural Justifications

InventoryModel:

Supplier Integration: Added `SupplierMock` to the UML diagram and code, to handle the new requirements .
 

4.1 Corrections Based on Previous Feedback:

The following adjustments were made to the class diagram and their corresponding source code implementations to address the feedback from the previous assignment:

•	Product Code (SKU): Added a sku: String attribute to the ItemType class in the UML and updated the Java model to fulfill the requirement for identifying unique business items.
•	Warehouse Entity: Promoted the warehouse from a simple attribute to an independent Warehouse class in the domain model. The corresponding Java class was created with its specific properties (e.g., capacity, location).
•	Diagram Drift: Cleaned the UML diagram by fixing typos (e.g., ServiceController), removing duplicate association labels, and deleting the accidentally duplicated minQuantity attribute.

4.2 Architectural Changes & GRASP Justifications:

To meet the updated requirements and improve system architecture, the following design decisions were implemented:
•	Introducing the Warehouse Entity
o	The Change: The warehouse is now managed as a dedicated class with its own attributes, rather than a boolean or integer field within ItemType.
o	Justification (High Cohesion & Information Expert): Assigning the Warehouse class the responsibility to manage its own physical properties and inventory lists applies the Information Expert principle. This significantly improves High Cohesion by preventing other classes from managing data that falls outside their core scope.







EmployeeModel:

Rationale for Changes:
● Branch Management: Added Store Branch in order to support branch-based employee and shift management. Each employee and each shift are assigned to a specific branch.

● Employee Update Flow: Added support for updating existing employee details, including name, bank account details, employment terms, and branch assignment.
● Driver Support: Added a Driver class that extends Employee and includes a licenseType attribute. This allows the Employees module to store driver-related information without managing trucks or full transportation scheduling.

● Employee-Side Transport Support: Added support methods that allow checking available drivers according to date, shift type, branch, and required license type.

● Stock Keeper Validation: Added support for checking whether an active Stock Keeper is assigned to a specific shift and branch when a delivery is expected to arrive.

● Mock-Based Integration Testing: Since the full Transport module is not implemented inside the Employees module TransportMock may be used only in integration tests to provide dummy delivery-related data. It is not part of the production Employees model and therefore is not included in the class diagram.

● Requirement Classification Fix: The rule preventing duplicate Employee IDs was changed from Non-Functional to Functional, because it is a business rule enforced by the system.
________________________________________
Priority Adjustments:

● Essential employee management, branch management, scheduling, driver-support, and integration-support requirements were marked as Must have.

● Optional reporting or history-related requirements were marked as Nice to have.

● Priority values were changed from High / Medium / Low to Must have / Nice to have, according to Assignment 2 instructions.
________________________________________


Removed / Modified Requirements:

● No core employee requirements were removed.
● The duplicate Employee ID requirement was changed from Non-Functional to Functional.
● Branch-related requirements were added because the updated design now includes StoreBranch.
● Driver-related requirements were added because the updated design now includes Driver and licenseType.
● Full transport management was not added to the Employees module. Truck management and full delivery scheduling remain outside the Employees module.
________________________________________
Glossary of Terms:
Employee: A worker in the system, including personal details, employment terms, roles, availability, scheduled shifts, active status, and branch assignment.

Driver: A specialized employee that extends Employee and stores a licenseType.

Stock Keeper: An employee role required to be present in a shift and branch where a delivery is expected.

Shift: A work period defined by date, shift type, and store branch.
StoreBranch: A company branch. Each employee and each shift are assigned to one specific branch.

License Type: The driving license type stored for a Driver and used to check driver suitability.

TransportMock: A mock class used only for testing. It provides dummy delivery-related data such as date, shift type, branch, and required license type.
________________________________________
System Assumptions:
1.	Each employee belongs to exactly one StoreBranch.
2.	Each shift belongs to exactly one StoreBranch.
3.	An employee cannot be assigned to a shift that belongs to a different branch.
4.	A Driver is represented as a subclass of Employee and stores a licenseType.
5.	The Employees module stores driver information, but does not manage trucks or full transportation scheduling.
6.	The Employees module can return available drivers according to date, shift type, branch, and required license type.
7.	When a delivery is expected in a shift, an active Stock Keeper must be assigned to the same shift and branch.
8.	If data from the Transport module is needed for testing and the real module is unavailable, Transport Mock may be used.