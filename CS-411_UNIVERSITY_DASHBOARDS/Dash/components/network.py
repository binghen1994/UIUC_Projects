from dash import Dash, html, callback, Input, Output
import dash_cytoscape as cyto
import dash_bootstrap_components as dbc

from utils.neo4j_utils import *
import utils.mongodb_utils as mongodb

# CREATE QUERY FOR RELATIVE PROFESSOR

styles = {
    "container-div": {
        'width': '100%',
        'height': '100%',
    },

    "network-graph": {
        'width': '1000px',
        'height': '600px',
    },
}

configs = {
    "cyto_minzoom": 0.5,
    "cyto_maxzoom": 1.5,
}


def professor_network(faculty_name: str):
    mongo_data = mongodb.get_database()['faculty'].aggregate([
        {"$match": {"name": faculty_name}},
    ])

    faculty_data = list(mongo_data)[0]

    comp = html.Div([
        # **********************************START OF PROFESSOR PAGE

        html.P("Network of Professors"),

        dcc.Dropdown(
            id='dpdn',
            value='random',
            clearable=False,
            options=[
                {'label': name.capitalize(), 'value': name}
                for name in ['breadthfirst', 'grid', 'random', 'circle']
            ],
            style={'width': '700px'}
        ),
        cyto.Cytoscape(
            id='professor',
            # input university name and professor name here
            elements=relative_professor(faculty_data['name'], faculty_data['affiliation']['name']),
            minZoom=configs['cyto_minzoom'],
            maxZoom=configs['cyto_maxzoom'],
            layout={'name': 'random'},
            style=styles['network-graph'],
            stylesheet=[
                {
                    'selector': 'node',
                    'style': {
                        'label': 'data(label)'
                    }
                },
                {
                    'selector': 'edge',
                    'style':
                        {
                            'label': 'data(label)',
                            'text-valign': 'bottom',  # Align text to bottom of node
                            'text-margin-y': 10,  # Add margin to the bottom of the text
                            'z-index': 999
                        }
                },
                {
                    'selector': '.center',
                    'style': {
                        'background-color': '#f7dddd',
                        'line-color': '#f7dddd',
                        'width': '80px',
                        'height': '80px',
                        'borderColor': 'grey',  # Set border color to black
                        'borderWidth': 2
                    }
                },
                {
                    'selector': '.red',
                    'style': {
                        'background-color': '#f7eded',
                        'line-color': '#f7eded',
                        'width': '50px',
                        'height': '50px',
                        'borderColor': 'grey',  # Set border color to black
                        'borderWidth': 2
                    }
                },
                {
                    'selector': '.blue',
                    'style': {
                        'background-color': '#c9c9c9',
                        'line-color': '#c9c9c9',
                        'weight': '1px'
                    }
                }
            ]),

    ])

    return comp


def affiliation_network(affiliation_name):
    comp = html.Div(
        children=[
            html.P("Network of Universities"),
            dcc.Dropdown(
                id='dpdn2',
                value='random',
                clearable=False,
                options=[
                    {'label': name.capitalize(), 'value': name}
                    for name in ['breadthfirst', 'grid', 'random', 'circle']
                ],
                style={'width': '700px'}

            ),
            cyto.Cytoscape(
                id='university',
                # input university name and professor name here
                elements=collaborated_university(affiliation_name),
                minZoom=configs['cyto_minzoom'],
                maxZoom=configs['cyto_maxzoom'],
                layout={'name': 'random'},
                style=styles['network-graph'],

                stylesheet=[
                    {
                        'selector': 'node',
                        'style': {
                            'label': 'data(label)'
                        }
                    },
                    {
                        'selector': 'edge',
                        'style':
                            {
                                'label': 'data(label)',
                                'text-valign': 'bottom',  # Align text to bottom of node
                                'text-margin-y': 10,  # Add margin to the bottom of the text
                                'z-index': 999
                            }
                    },
                    {
                        'selector': '.center',
                        'style': {
                            'background-color': '#f7dddd',
                            'line-color': '#f7dddd',
                            'width': '80px',
                            'height': '80px',
                            'borderColor': 'grey',  # Set border color to black
                            'borderWidth': 2
                        }
                    },
                    {
                        'selector': '.red',
                        'style': {
                            'background-color': '#f7eded',
                            'line-color': '#f7eded',
                            'width': '50px',
                            'height': '50px',
                            'borderColor': 'grey',  # Set border color to black
                            'borderWidth': 2
                        }
                    },
                    {
                        'selector': '.blue',
                        'style': {
                            'background-color': '#c9c9c9',
                            'line-color': '#c9c9c9',
                            'weight': '1px'
                        }
                    }
                ]

            ), ],

        style=styles['container-div'],
    )

    return comp


def publication_network(publication_id: int):

    mongo_data = mongodb.get_database()['publications'].aggregate([
        {"$match": {"id": int(publication_id)}},
    ])

    publication_title = list(mongo_data)[0]['title']

    comp = html.Div([html.P("Network of publication"),
                     dcc.Dropdown(
                         id='dpdn3',
                         value='random',
                         clearable=False,
                         options=[
                             {'label': name.capitalize(), 'value': name}
                             for name in ['breadthfirst', 'grid', 'random', 'circle']
                         ],
                         style={'width': '700px'}

                     ),
                     cyto.Cytoscape(
                         id='publication',
                         # input university name and professor name here
                         elements=publication_network_finder(publication_title),
                         minZoom=configs['cyto_minzoom'],
                         maxZoom=configs['cyto_maxzoom'],
                         layout={'name': 'random'},
                         style=styles['network-graph'],

                         stylesheet=[
                             {
                                 'selector': 'node',
                                 'style': {
                                     'label': 'data(label)'
                                 }
                             },
                             {
                                 'selector': 'edge',
                                 'style':
                                     {
                                         'label': 'data(label)',
                                         'text-valign': 'bottom',  # Align text to bottom of node
                                         'text-margin-y': 10,  # Add margin to the bottom of the text
                                         'z-index': 999
                                     }
                             },
                             {
                                 'selector': '.center',
                                 'style': {
                                     'background-color': '#f7dddd',
                                     'line-color': '#f7dddd',
                                     'width': '80px',
                                     'height': '80px',
                                     'borderColor': 'grey',  # Set border color to black
                                     'borderWidth': 2
                                 }
                             },
                             {
                                 'selector': '.red',
                                 'style': {
                                     'background-color': '#f7eded',
                                     'line-color': '#f7eded',
                                     'width': '60px',
                                     'height': '60px',
                                     'borderColor': 'grey',  # Set border color to black
                                     'borderWidth': 2
                                 }
                             },
                             {
                                 'selector': '.blue',
                                 'style': {
                                     'background-color': '#c9c9c9',
                                     'line-color': '#c9c9c9',
                                     'weight': '1px'
                                 }
                             },
                             {
                                 'selector': '.keyword',
                                 'style': {
                                     'background-color': '#b3bdbe',
                                     'line-color': '#b3bdbe',
                                     'width': '40px',
                                     'height': '40px',
                                     'borderColor': 'grey',  # Set border color to black
                                     'borderWidth': 2
                                 }
                             }
                         ]

                     ),
                     html.P(id='node-info')

                     ])

    return comp


@callback(Output('professor', 'layout'),
          Input('dpdn', 'value'))
def update_layout(layout_value):
    if layout_value == 'breadthfirst':
        return {
            'name': layout_value,
            'roots': '[id = "ed"]',
            'animate': True
        }
    else:
        return {
            'name': layout_value,
            'animate': True
        }


@callback(Output('university', 'layout'),
          Input('dpdn2', 'value'))
def update_layout2(layout_value):
    if layout_value == 'breadthfirst':
        return {
            'name': layout_value,
            'roots': '[id = "ed"]',
            'animate': True
        }
    else:
        return {
            'name': layout_value,
            'animate': True
        }


@callback(
    Output('node-info', 'children'),  # Update the modal body content
    [Input('publication', 'tapNodeData')]
)
def display_node_info(node_data):
    if node_data:
        node_type = node_data['info']['type']
        node_id = node_data['info']['id']
        node_name = node_data['info']['name']
        if 'numcitation' in node_data['info']:
            node_numcitation = node_data['info']['numcitation']
            return [
                html.P(f'{node_type}', style={'background-color': '#f7eded', 'width': '700px'}),
                html.P(f'NAME: {node_name}'),
                html.P(f'ID: {node_id}'),
                html.P(f'CITATION: {node_numcitation}')]
        # Use HTML elements to create line breaks or paragraphs
        else:
            return [
                html.P(f'{node_type}', style={'background-color': '#b3bdbe', 'width': '700px'}),
                html.P(f'NAME: {node_name}'),
                html.P(f'ID: {node_id}')

            ]
    else:
        # Clear the modal body content
        return []

# DROPDOWN CALLBACK

@callback(Output('publication', 'layout'),
          Input('dpdn3', 'value'))
def update_layout3(layout_value):
    if layout_value == 'breadthfirst':
        return {
            'name': layout_value,
            'roots': '[id = "ed"]',
            'animate': True
        }
    else:
        return {
            'name': layout_value,
            'animate': True
        }
