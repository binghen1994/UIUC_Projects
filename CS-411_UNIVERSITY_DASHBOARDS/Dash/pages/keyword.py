import dash
from dash import dcc
from dash import html
import dash_bootstrap_components as dbc

import components.graph as graph_comp
import components.list as comp_list
import utils.slug_conversion_utils as cvt
from utils.mysql_utils import insert_keyword_search
from components.similar_keyword import similar_keyword

dash.register_page(__name__, path_template='/keyword/<keyword_slug>')


styles = {
    "row": {
        "border": "0.75px solid grey",
        'border-radius': '10px',
        "padding": "20px",
        "margin": "10px",
    },

}


def layout(keyword_slug=None):

    keyword_name = cvt.toName(keyword_slug)

    # add to search key
    insert_keyword_search(keyword_name)


    return html.Div(

        children=[

            html.Div(
                children=html.H3(
                    children=f'current search keyword is {keyword_name}',
                    style={
                        'textAlign': 'center',
                    },
                ),

                style=styles['row']
            ),

            dbc.Row(
                children=[
                    dbc.Col(
                        children=[
                            html.H5(
                                children=f" Faculty with highest scores on {keyword_name}",
                            ),
                            comp_list.top_faculty_list(keyword_name)
                        ],
                        style={
                            'width': '30%',
                        },
                    ),

                    graph_comp.faculty_key_year(keyword_name)
                ],

                style=styles['row']
            ),

            dbc.Row(
                children=[
                    dbc.Col(
                        children=[
                            html.H5(
                                children=f" Affiliation with highest scores on {keyword_name}",
                            ),
                            comp_list.top_affiliation_list(keyword_name)
                        ],
                        style={
                            'width': '30%',
                        },
                    ),

                    graph_comp.affiliation_key_faculty_year(keyword_name)
                ],

                style=styles['row']
            ),

            dbc.Row(
                children=[
                    dbc.Col(
                        children=[
                            html.H5(
                                children=f" Publication with highest scores on {keyword_name}",
                            ),
                            comp_list.top_publication_list(keyword_name)
                        ],
                        style={
                            'width': '30%',
                        },
                    ),

                    graph_comp.publication_keyword_year(keyword_name)
                ],

                style=styles['row']
            ),

            similar_keyword(keyword_name),

        ],
    )
