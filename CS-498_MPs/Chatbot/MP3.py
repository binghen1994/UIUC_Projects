# SHORTEST DISTANCE LAMBDA
import json
import boto3


def lambda_handler(event, context):
    # TODO implement
    source = event['currentIntent']['slots']['Source']
    dest = event['currentIntent']['slots']['Destination']
    input = source + ',' + dest

    dynamodb = boto3.resource('dynamodb')
    table = dynamodb.Table('city_distance')
    response = table.get_item(Key={'key': input})

    return {
        "dialogAction": {
            "type": "Close",
            "fulfillmentState": "Fulfilled",
            "message": {
                "contentType": "PlainText",
                "content": str(int(response['Item']['distance']))
            }
        }
    }












# LEX BOT LAMBDA
import json
import boto3


def lambda_handler(event, context):
    # TODO implement
    source = event['currentIntent']['slots']['Source']
    dest = event['currentIntent']['slots']['Destination']
    input = source + ',' + dest

    dynamodb = boto3.resource('dynamodb')
    table = dynamodb.Table('city_distance')
    response = table.get_item(Key={'key': input})

    return {
        "dialogAction": {
            "type": "Close",
            "fulfillmentState": "Fulfilled",
            "message": {
                "contentType": "PlainText",
                "content": str(int(response['Item']['distance']))
            }
        }
    }



