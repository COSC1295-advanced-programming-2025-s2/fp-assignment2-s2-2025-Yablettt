Voluntrack Database Information

Using SQLite

Database file is voluntrack.db which is created in the root directory.

Driver is org.sqlite.JDBC

Databse.init() method automatically creates all required tables.

ProjectStore.seedFromCsvIfEmpty loads initial project data into the projects table if it is empty.

Users information is stored in the 'users' table. Project information is stored in 'projects'.
Confirmed registrations are stored in 'registrations'.

