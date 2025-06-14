import requests
import json
import uuid

url = "https://seorwrpmwh.execute-api.us-east-1.amazonaws.com/prod/mp3-autograder-2022-spring"

payload = {
	"graphApi": 'https://yzgq2azzuj.execute-api.us-east-1.amazonaws.com/post_mp3',
	"botName": 'mpbot',
	"botAlias": 'lex_lex_bot',
	"identityPoolId": 'us-east-1:85dd6122-025c-4cab-adf4-510da85d8d26',
	"accountId": '822874362057',
	"submitterEmail": 'yuluw2@illinois.edu',
	"secret": 'YHgjlqrydkNsRKw0',
	"region": 'us-east-1'
    }

r = requests.post(url, data=json.dumps(payload))

print(r.status_code, r.reason)
print(r.text)