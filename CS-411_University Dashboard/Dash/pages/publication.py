import dash
from dash import dcc
from dash import html
import dash_bootstrap_components as dbc

import components.list as comp_list
from components.network import publication_network


dash.register_page(__name__, path_template='/publication/<publication_id>')


styles = {
    "title-top": {
        "border": "0.75px solid grey",
        'border-radius': '10px',
        "padding": "20px",
        "margin": "16px",
    },

    "container-down": {
        "margin": "16px",
    },

    "row-left": {
        "border": "0.75px solid grey",
        'border-radius': '10px',
        "padding": "20px",
        "marginRight": "8px",
        "width": "30%",
    },

    "row-right": {
        "border": "0.75px solid grey",
        'border-radius': '10px',
        "padding": "20px",
        "marginLeft": "8px",
        "width": "68%",
    },
}


def layout(publication_id=None):

    return dbc.Col(

        children=[

            html.Div(
                children=html.H3(
                    children='Meet the publication',
                    style={
                        'textAlign': 'center',
                    },
                ),

                style=styles['title-top']
            ),

            dbc.Row(
                children=[
                    html.Div(
                        dbc.Col(
                            children=[
                                comp_list.publication_profile(publication_id),
                                comp_list.publication_profile_author(publication_id),
                                comp_list.publication_profile_keyword(publication_id),
                            ],

                        ),

                        style=styles['row-left']
                    ),

                    html.Div(
                        dbc.Col(
                            children=[
                                publication_network(publication_id)
                            ],

                        ),

                        style=styles['row-right']
                    ),
                ],

                style=styles['container-down']
            ),

        ],
    )
