import dash
from dash import dcc
from dash import html
import dash_bootstrap_components as dbc

import components.graph as graph_comp
import components.list as comp_list
import utils.slug_conversion_utils as cvt
import utils.mongodb_utils as mongodb
import utils.slug_conversion_utils as cvt
from components.network import professor_network

dash.register_page(__name__, path_template='/faculty/<faculty_slug>')

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


def layout(faculty_slug=None):

    faculty_name = cvt.toName(faculty_slug)

    return dbc.Col(

        children=[

            html.Div(
                children=html.H3(
                    children='Meet the faculty',
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
                                comp_list.faculty_profile(faculty_name)
                            ],

                        ),

                        style=styles['row-left']
                    ),

                    html.Div(
                        dbc.Col(
                            children=[
                                professor_network(faculty_name),
                            ],

                        ),

                        style=styles['row-right']
                    ),
                ],

                style=styles['container-down']
            ),

        ],
    )
