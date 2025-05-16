## Overview

This project, **sunking-got-cs**, is a microservice designed to manage and provide APIs for the Game of Thrones universe. It includes features for managing characters, houses, and events.

## Database Table Summary
1. regions: Stores distinct regions where battles occurred.
- region_id (UUID/PK): Unique identifier for each region.
- name (VARCHAR): Name of the region (e.g., “The North”).

2. locations: Stores unique locations within each region.
- location_id (UUID/PK): Unique ID.
- name (VARCHAR): Name of the location (e.g., “Winterfell”).
- region_id (FK): Foreign key to regions.region_id.

3. battles: Stores detailed information about each battle.
- battle_id (UUID/PK): Unique identifier.
- name (VARCHAR): Name of the battle.
- year, battle_number, attacker_outcome, battle_type, note, etc.
- region_id (FK): Region where the battle took place.
- location_id (FK): Location of the battle.

4. participants: Stores names of houses, kings, commanders, etc.
- participant_id (UUID/PK): Unique ID.
- name (VARCHAR): House, king, or commander name.

5. battle_participants: Many-to-many join table linking battles with participants and their roles.
- Composite Key: (battle_id, participant_id)
- role (ENUM): ATTACKER, DEFENDER, COMMANDER_ATTACKER, COMMANDER_DEFENDER, ATTACKER_KING, DEFENDER_KING
- Foreign Keys:
    battle_id → battles.battle_id
    participant_id → participants.participant_id

6. battle_locations: Table to store if a battle spans multiple locations.
- Composite Key: (battle_id, location_id)
- Foreign Keys:
    battle_id → battles.battle_id
    location_id → participants.location_id

## API Summary
### 1. **Count API**
- **Endpoint**: `/v1/count`
- **Method**: GET
- **Description**: Returns aggregated statistics from the Game of Thrones dataset, including total number of battles, participants, regions, and locations.
- **Validations**:
    - This is a read-only endpoint — no input parameters required.
- **Database Tables**:
    - regions
    - locations
    - battles
    - participants
    - battle_locations
    - battle_participants


### 2. **List Regions and Locations API**
- **Endpoint**: `/v1/got/list`
- **Method**: GET
- **Description**: Retrieves a list of all regions and their associated locations where battles have occurred in the Game of Thrones dataset.
- **Validations**:
    - This is a read-only endpoint — no input parameters required.
- **Database Tables**:
    - regions
    - locations

### 3. **Battle Details API**
- **Endpoint**: `/v1/got/battle?name=??`
- **Method**: GET
- **Description**:Retrieves detailed information about a specific battle in the Game of Thrones universe, including the battle metadata, associated region and location, and all related participants (kings, commanders, attackers, defenders) along with their roles.
- **Validations**:
    - ✅ "name" query parameter is required and must be a non-empty string.
    - ✅ Returns 400 Bad Request if name is missing or empty.
    - ✅ Returns 404 Not Found if no battle is found with the given name.
- **Database Tables**:
    - regions
    - locations
    - battles
    - participants
    - battle_locations
    - battle_participants

### 4. **Bulk Upload API**
- **Endpoint**: `/v1/bulk/bulk-upload`
- **Method**: POST
- **Content-Type**: multipart/form-data
- **Description**: Allows uploading a CSV file to bulk ingest battle data into the system. The file must include battles and all related data (participants, region, location, etc.) in the expected format.
- **Validations**:
    - ✅ File Presence: CSV file must be provided in the file field.
    - ✅ File Format: Only .csv files are accepted.
    - ✅ Header Validation: Required headers must be present (e.g. name, year, battle_number, attacker_king, etc.).
    - ✅ Empty Columns: No critical column should be entirely empty.
    - ✅ Empty Rows: Blank lines in between rows are skipped or flagged.
    - ✅ Content Validation:
        - year, battle_number, attacker_size, and defender_size should be numeric
        - Fields like attacker_king, region, location must not be null
**Note**:
    - When a new file is uploaded, previous data would be truncated and new data would be added.This is to achieve full data refresh. 

## Deployment Plan
   -Push code to main.
   -GitHub Actions triggers deploy.yml.
   -The workflow connects EC2 via SSH.
   -Code is pulled, Docker image is built, container is deployed.

## Notes
- Ensure the EC2 instance has Docker installed and is properly configured.
- Use environment variables for sensitive data like database credentials and API keys.
- Monitor the deployment using logs and health checks.


## Cloud Services:
- AWS EC2
- AWS RDS (MySQL)


## TODO:
- Enable Offline Upload Mechanism
    Decouple direct API upload and support large file ingestion via AWS S3
    User uploads CSV to a specific S3 path (e.g., s3://sunking-got-cs/uploads/).
    S3 triggers a Lambda or AWS Batch job for background processing.
    Lambda reads the file, validates data, and performs batch inserts into the DB.

- Enable Email/Slack Notifications
    Notify stakeholders on success or failure of CSV uploads or batch jobs
    Publish results to SNS Topics (success_notifications, failure_notifications)

- Integrate CloudWatch Logging
    Ensure structured and searchable logs for all backend operations.
    
