# Lottery Draw Demo (lottery-draw-demo)

A simulation draw system for the Sports Lottery Super Lotto, based on historical winning data.

This project fetches and stores historical lottery draw data, then uses that data to perform weighted random draws,
simulating new lottery numbers. The system also supports saving user-selected numbers and provides functionality to
check their historical winning status.

## ✨ Features

* **Historical Data Sync**: Fetches and stores all historical draw data from the `webapi.sporttery.cn` API.
* **Incremental Updates**: Automatically detects and backfills the latest draw data on application startup.
* **Simulation Draw**: Generates new 5+2 numbers using a weighted random draw based on all historical data (including
  past winning numbers and self-chosen winning numbers).
* **Self-Chosen Number Management**: Allows users to save their own lottery number combinations.
* **Historical Prize Check**: Can query any set of 5+2 numbers against the historical data to find winning instances and
  details (from 1st to 9th prize).
* **Winning Data Sync**: Automatically updates the winning status and details for saved self-chosen numbers.

## 🛠️ Tech Stack

* **Backend**: Spring Boot 3
* **Persistence**: Spring Data JPA (Hibernate)
* **Database**: MySQL
* **HTTP Client**: OkHttp
* **JSON Parsing**: FastJSON2
* **Build Tool**: Gradle
* **Language**: Java 24

## 🚀 Quick Start

### 1. Database Setup

1. Ensure you have MySQL 8.0+ installed and running.
2. Create a new database (e.g., `lottery_draw_demo`).
3. Import the SQL dump file from the `sql/` directory (`lottery_draw_demo_...-dump.sql`) to create the required tables (
   `lottery_data`, `self_chosen`, `self_chosen_winning`).
4. Modify `src/main/resources/application-dev.yml` with your database URL, username, and password.

### 2. Run the Project

1. Clone this repository:
   ```bash
   git clone [Your_Repository_URL]
   cd lottery-draw-demo
   ```

2. Build and run the project using the Gradle Wrapper:
   ```bash
   # (Recommended)
   ./gradlew bootRun
   ```

3. The project will start using the `dev` profile, connecting to your configured database.

### 3. Data Initialization

On the first run, if the `lottery_data` table is empty, the system will automatically fetch all historical draw data
from the API via `LotteryRequestManager`. This initial sync may take a few minutes.

### 4. View Draw Result

Once the application has started, the `CommandLineRunner` will execute and print a randomly generated set of lottery
numbers to the console.