
# Keyworld
> Final project for CS411DSO-SP23

## Purpose
- This application - **Keyworld** - is a web app that helps users find computer science faculty members, affiliations, and publications based on specific keyword. Users can search for a keyword, getting faculty members and their publications. The app is useful for researchers, students, and academics looking to connect with other professionals in the field and stay up-to-date with the latest research.

## Demo

## Installation
> This application is not deployment ready. Suppose localhost running environment only!

1. For the dash project, simply install all packages and run `dash/app.py` and visit the url in terminal.

2. For MySQL and Neo4j, all username and password are hardcoded. Please replace them with local machine p/w.

3. For MySQL, need to create a new table. See [below](#database-modifications) for details.

## Usage
Upon executing the `dash/app.py` script, you will be directed to a URL provided by the dash framework, leading to our web application page. To explore the contents of our application, input the keyword of interest into the search bar provided and subsequently click on any of the listed faculties, institutes or publications. This will redirect you to a more detailed page with additional information.
## Design

The Keyworld web application is intended to allow individuals to explore and identify faculties, affiliations, and publications of interest by performing a simple keyword search. The application's architecture may be divided into three primary components: the client-side, the server-side, and the database.
1. Client-Side:
> The client-side component of the Keyworld web application serves as the user interface and is responsible for displaying relevant information and analytical diagrams related to the input keyword. To implement this component, we utilize an open-source framework called dash, which facilitates the development of data visualization interfaces. Additionally, we employ a REST API to enable the creation of multipage views for enhanced data representation. Please refer to the following diagram to visualize the overall design of the web page.
2. Server-Side:
> As the Keyworld web application is currently in demo mode, we do not offer any server-side services at this time. Therefore, we kindly request that you test the application locally on your device.
3. Database:
> The database component of the Keyworld web application plays a crucial role in storing the necessary data for the application. To cater to different query requirements and enhance overall performance, we employ three different database management systems: MySQL, MongoDB, and Neo4j. Each of these database systems is optimized for specific use cases, and together, they work seamlessly to provide users with a robust and efficient web application experience.
## Implementation
To enable the features of the Keyworld web application, we have utilized the following implementation methods:
1. For most of the widgets, we have employed the default graph provided by the dash plotly library and have retrieved the required data from the three databases used by the application.
2. For enhanced visualization of certain diagrams, such as network relationships between elements, we have imported additional libraries, including `Dash Core Components`, `Dash Bootstrap Components`, and `dash_cytoscape`.
3. To facilitate the "similar keywords" function, we have utilized the `word_forms` library to generate all possible English word forms and subsequently created new relations in the Neo4j database to query associated keywords.
## Database Techniques
1. Foreign Key Constraint on new table
2. Indexing on keyword names
3. Rest API for accessing resources in the database
## Extra-Credit Capabilities
1. Multi page application access by links of resources
2. Some function of the Keyworld web application like "similar keyword" utilizes multiple databases(for example use the query result from MySql as input to Neo4J) along with new algorithms to improve performance. 
## Contributions
Yixiao Wei:


Yulu Wang (25h): 
1. implementation of relational widgets using Neo4J.
2. Documentation 
3. 
             


### Database modifications
There is a book with id 2147483647(which is INT_MAX) with 5000+ authors and 28000+ keywords.
For some cases, avoid search publications in MySQL.

Database manipulation:
Create keyword search table in mysql

    CREATE TABLE keyword_search(
    id INT, 
    stamp TIMESTAMP NOT NULL,
    PRIMARY KEY(id, stamp),
    FOREIGN KEY (id) REFERENCES keyword(id)
    );

    INSERT INTO keyword_search
    VALUES(2, now());



Create index for keyword table in mysql

    CREATE INDEX keyword_name_index
    ON Keyword(name);


Insert new relationship in neo4j

create mutual similar_keyword relationship between keywords that are similar.


Techniques:
    Foreign Key Constraint on new table
    Indexing on keyword names
    Rest API for accessing resources in the database


Extra point functions:
    Multi-page application access by links of resources
    Similar keyword function that uses both MySQL and Neo4J, new algorithms and provide improving performance.

