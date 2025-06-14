import dash_bootstrap_components as dbc
from dash import html
from dash import dcc

import utils.mongodb_utils as mongodb
from utils.mysql_utils import execute_dql
import utils.slug_conversion_utils as cvt


styles = {
    "table": {
        "minHeight": "500px",
    },
}


def top_faculty_list(keyword_name: str):
    mongo_data = mongodb.get_database()['faculty'].aggregate([
        {"$match": {"keywords.name": keyword_name}},
        {"$unwind": "$keywords"},
        {"$match": {"keywords.name": keyword_name}},
        {"$sort": {"keywords.score": -1}},
        {"$limit": 10},
    ])

    data_list = list(mongo_data)

    table_header = [
        html.Thead(
            html.Tr(
                [html.Th("Faculty Name"),
                 html.Th("Affiliation"),
                 html.Th("Score"),
                 ]))
    ]

    table_body = [html.Tbody([
        html.Tr([
            html.Td(
                dcc.Link(
                    children=item['name'],
                    href=f'/faculty/{cvt.toNameSlug(item["name"])}'
                ),
            ),
            html.Td(
                dcc.Link(
                    children=item['affiliation']['name'],
                    href=f'/affiliation/{cvt.toNameSlug(item["affiliation"]["name"])}'
                ),
            ),
            html.Td(item['keywords']['score']),
        ]) for item in data_list
    ])]

    return dbc.Table(
        table_header + table_body,
        bordered=True,
        style=styles['table'],
    )


def top_affiliation_list(keyword_name: str):
    mongo_data = mongodb.get_database()['faculty'].aggregate([
        {"$match": {"keywords.name": keyword_name}},
        {"$unwind": "$keywords"},
        {"$match": {"keywords.name": keyword_name}},
        {"$group": {
            "_id": "$affiliation.id",
            "affiliation_name": {"$first": "$affiliation.name"},
            "total_score": {"$sum": "$keywords.score"}
        }},
        {"$sort": {"total_score": -1}},
        {"$limit": 10},
    ])

    data_list = list(mongo_data)

    # print(data_list)

    table_header = [
        html.Thead(
            html.Tr([
                html.Th("Affiliation"),
                html.Th("Total Score"),
            ])),
    ]

    table_body = [html.Tbody([
        html.Tr([
            html.Td(
                dcc.Link(
                    children=item['affiliation_name'],
                    href=f'/affiliation/{cvt.toNameSlug(item["affiliation_name"])}'
                ),
            ),
            html.Td(item['total_score']),
        ]) for item in data_list
    ])]

    return dbc.Table(
        table_header + table_body,
        bordered=True,
        style=styles['table'],
    )


def top_publication_list(keyword_name: str):
    mongo_data = mongodb.get_database()['publications'].aggregate([
        {"$match": {"keywords.name": keyword_name}},
        {"$unwind": "$keywords"},
        {"$match": {"keywords.name": keyword_name}},
        {"$sort": {"keywords.score": -1}},
        {"$limit": 10},
    ])

    data_list = list(mongo_data)

    # print(data_list)

    table_header = [
        html.Thead(
            html.Tr([
                html.Th("Title"),
                html.Th("Score"),
            ])),
    ]

    table_body = [html.Tbody([
        html.Tr([
            html.Td(
                dcc.Link(
                    children=item['title'],
                    href=f'/publication/{item["id"]}'
                ),
            ),
            html.Td(item['keywords']['score']),
        ]) for item in data_list
    ])]

    return dbc.Table(
        table_header + table_body,
        bordered=True,
        style=styles['table'],
    )


def faculty_profile(faculty_name):
    mongo_data = mongodb.get_database()['faculty'].aggregate([
        {"$match": {"name": faculty_name}},
    ])

    faculty_data = list(mongo_data)[0]

    # print(faculty_data)

    table_body = [html.Tbody([

        html.Tr([
            html.Td("Photo"),
            html.Td(
                html.Img(
                    src=faculty_data["photoUrl"],
                    style={'maxHeight': '240px',
                           'maxWidth': '100%'},
                ),
            ),
        ]),

        html.Tr([
            html.Td("Name"),
            html.Td(faculty_data["name"]),
        ]),

        html.Tr([
            html.Td("Position"),
            html.Td(faculty_data["position"]),
        ]),

        html.Tr([
            html.Td("Research Interest"),
            html.Td(faculty_data["researchInterest"]),
        ]),

        html.Tr([
            html.Td("Email"),
            html.Td(faculty_data["email"]),
        ]),

        html.Tr([
            html.Td("Phone"),
            html.Td(faculty_data["phone"]),
        ]),

        html.Tr([
            html.Td("Affiliation"),
            html.Td(
                dcc.Link(
                    children=faculty_data["affiliation"]["name"],
                    href=f'/affiliation/{cvt.toNameSlug(faculty_data["affiliation"]["name"])}'
                ),
            ),
        ]),

        html.Tr([
            html.Td("Number of keyword"),
            html.Td(len(faculty_data["keywords"])),
        ]),

        html.Tr([
            html.Td("Number of publications"),
            html.Td(len(faculty_data["publications"])),
        ]),

    ])]

    return dbc.Table(table_body, bordered=True)


def affiliation_profile(affiliation_name):
    sql_str = "SELECT * " \
              "FROM University u " \
              f'WHERE u.name = "{affiliation_name}" ' \
              ";"

    affiliation_data = execute_dql(sql_str)[0]

    table_body = [html.Tbody([

        html.Tr([
            html.Td("Photo"),
            html.Td(
                html.Img(
                    src=affiliation_data[2],
                    style={'maxHeight': '240px',
                           'maxWidth': '100%'},
                ),
            ),
        ]),

        html.Tr([
            html.Td("Name"),
            html.Td(affiliation_data[1]),
        ]),

    ])]

    return dbc.Table(table_body, bordered=True)


def publication_profile(publication_id):
    mongo_data = mongodb.get_database()['publications'].aggregate([
        {"$match": {"id": int(publication_id)}},
    ])

    publication_data = list(mongo_data)[0]

    # print(publication_data)

    table_body = [html.Tbody([

        html.Tr([
            html.Td("Title"),
            html.Td(publication_data["title"]),
        ]),

        html.Tr([
            html.Td("Venue"),
            html.Td(publication_data["venue"]),
        ]),

        html.Tr([
            html.Td("Year"),
            html.Td(publication_data["year"]),
        ]),

        html.Tr([
            html.Td("Numbers of citations"),
            html.Td(publication_data["numCitations"]),
        ]),

    ])]

    return dbc.Table(table_body, bordered=True)


def publication_profile_keyword(publication_id):
    mongo_data = mongodb.get_database()['publications'].aggregate([
        {"$match": {"id": int(publication_id)}},
    ])

    publication_data = list(mongo_data)[0]

    # print(publication_data)

    table_header = [
        html.Thead(
            html.Tr([
                html.Th("Keyword"),
                html.Th("Score"),
            ])),
    ]

    table_body = [html.Tbody([
        html.Tr([
            html.Td(
                dcc.Link(
                    children=k['name'],
                    href=f'/keyword/{cvt.toNameSlug(k["name"])}',
                ),
            ),

            html.Td(
                children=k['score'],
            ),
        ])
        for k in publication_data["keywords"]
    ])]

    return dbc.Table(table_header + table_body, bordered=True)


def publication_profile_author(publication_id):
    mongo_data = mongodb.get_database()['faculty'].aggregate([
        {"$match": {"publications": int(publication_id)}},
    ])

    publication_data = list(mongo_data)

    # print(publication_data)

    table_header = [
        html.Thead(
            html.Tr([
                html.Th("Author"),
            ])),
    ]

    table_body = [html.Tbody([
        html.Tr([
            html.Td(
                dcc.Link(
                    children=f['name'],
                    href=f'/faculty/{cvt.toNameSlug(f["name"])}',
                ),
            ),
        ])
        for f in publication_data
    ])]

    return dbc.Table(table_header + table_body, bordered=True)
