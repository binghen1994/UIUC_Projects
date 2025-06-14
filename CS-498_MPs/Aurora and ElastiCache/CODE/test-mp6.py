import requests
import json

url = 'https://u46j45y1h6.execute-api.us-east-1.amazonaws.com/prod/'

payload = {
			"submitterEmail": "yuluw2@illinois.edu",
			"secret": "pSFUrJYKUhUFyAhZ",
			"dbApi": "https://5g10w9fq1l.execute-api.us-east-1.amazonaws.com/cs498mp6"
		}
print(json.dumps(payload))
r = requests.post(url, data=json.dumps(payload))

print(r.status_code, r.reason)
print(r.text)